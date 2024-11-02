package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testFindOwnerById() {
        Long ownerId = 1L;
        EventDto eventDto = EventDto.builder().ownerId(ownerId).build();

        User expectedUser = new User();
        expectedUser.setId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(expectedUser));

        User result = userService.findOwnerById(eventDto);

        assertEquals(expectedUser, result);

        verify(userRepository, times(1)).findById(ownerId);
    }
}
