package school.faang.user_service.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UpdateUsersRankDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    private UpdateUsersRankDto usersRankDto;

    @Mock
    private UserContext userContext;
    @Mock
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        usersRankDto = UpdateUsersRankDto.builder()
                .halfUserRank(50.0)
                .maximumUserRating(100.0)
                .minimumUserRating(0.0)
                .maximumGrowthRating(9.9)
                .ratingGrowthIntensive(0.05)
                .build();
    }

    @Test
    void findById_WithCorrectId_ReturnNotEmptyOptionalUser() {
        Optional<User> userOptional = Optional.of(User.builder().build());
        when(userRepository.findById(1L))
                .thenReturn(userOptional);

        assertNotNull(userService.findById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUsersRank_WhenUsersHaveValidRanks() {
        Map<Long, Double> usersNewRanks = Map.of(
                1L, 10.555,
                2L, 20.123,
                3L, 0.0);
        usersRankDto.setUsersRankByIds(usersNewRanks);

        userService.updateUsersRankByUserIds(usersRankDto);

        verify(userRepository).updateUserRankByUserId(1L, 10.56);
        verify(userRepository).updateUserRankByUserId(2L, 20.12);
        verify(userRepository, times(0)).updateUserRankByUserId(3L, 0.0);
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
    }

    @Test
    void testUpdatePassiveUsers_SuccessUpdating() {
        Map<Long, Double> usersNewRanks = Map.of(1L, 10.0, 2L, 15.0);
        usersRankDto.setUsersRankByIds(usersNewRanks);
        Set<Long> activeUserIds = usersNewRanks.keySet();
        BigDecimal maxPossibleRating = BigDecimal.valueOf(usersRankDto.getMaximumGrowthRating() * usersRankDto.getRatingGrowthIntensive());
        double roundedMaxPossibleRating = maxPossibleRating.setScale(2, RoundingMode.HALF_UP).doubleValue();

        userService.updateUsersRankByUserIds(usersRankDto);

        verify(userRepository).updatePassiveUsersRatingWhichRatingLessThanRating(eq(roundedMaxPossibleRating), eq(activeUserIds));
        verify(userRepository).updatePassiveUsersRatingWhichRatingMoreThanRating(eq(roundedMaxPossibleRating), eq(activeUserIds));
    }

    @Test
    void testFlushAndClearCalled_WhenBatchLimitExceeded() {
        Map<Long, Double> usersNewRanks = new HashMap<>();
        for (long i = 1; i <= 60; i++) {
            usersNewRanks.put(i, (double) i);
        }
        usersRankDto.setUsersRankByIds(usersNewRanks);

        userService.updateUsersRankByUserIds(usersRankDto);

        verify(entityManager, times(2)).flush();
        verify(entityManager, times(2)).clear();
    }

    @Test
    void testGenerateRandomAvatarSuccess() {
        Long userId = 1L;
        String avatarUrl = "http://localhost/avatar/1.svg";
        User user = new User();
        user.setId(userId);
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avatarService.generateRandomAvatar(anyString(), eq(userId + ".svg"))).thenReturn(avatarUrl);
        String result = userService.generateRandomAvatar();
        assertEquals(avatarUrl, result);
        assertNotNull(user.getUserProfilePic());
        assertEquals(avatarUrl, user.getUserProfilePic().getFileId());
        verify(userRepository).save(user);
    }

    @Test
    void testGenerateRandomAvatarUserNotFound() {
        Long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.generateRandomAvatar();
        });
        assertEquals("User not found", exception.getMessage());
        verify(avatarService, never()).generateRandomAvatar(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}