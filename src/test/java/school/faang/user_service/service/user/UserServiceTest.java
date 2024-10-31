package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_UserExists() {
        long userId = 1L;
        Country country = new Country();
        country.setId(1L);
        country.setTitle("CountryName");

        User mockUser = User.builder()
                .id(userId)
                .username("JohnDoe")
                .email("john.doe@example.com")
                .phone("+123456789")
                .password("hashed_password")
                .active(true)
                .aboutMe("A brief description about John Doe")
                .country(country)
                .city("CityName")
                .experience(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .followers(Collections.emptyList())
                .followees(Collections.emptyList())
                .ownedEvents(Collections.emptyList())
                .mentors(Collections.emptyList())
                .mentees(Collections.emptyList())
                .setGoals(Collections.emptyList())
                .goals(Collections.emptyList())
                .skills(Collections.emptyList())
                .participatedEvents(Collections.emptyList())
                .recommendationsGiven(Collections.emptyList())
                .recommendationsReceived(Collections.emptyList())
                .contacts(Collections.emptyList())
                .ratings(Collections.emptyList())
                .build();

        when(userRepository.getUserById(userId)).thenReturn(mockUser);

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("JohnDoe", result.getUsername());
        assertEquals("john.doe@example.com", result.getEmail());
        assertTrue(result.isActive());
        assertEquals("A brief description about John Doe", result.getAboutMe());
        assertEquals(country, result.getCountry());
        assertEquals("CityName", result.getCity());
        assertEquals(5, result.getExperience());
        verify(userRepository, times(1)).getUserById(userId);
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        long userId = 1L;
        when(userRepository.getUserById(userId)).thenReturn(null);

        User result = userService.getUserById(userId);

        assertNull(result);
        verify(userRepository, times(1)).getUserById(userId);
    }
}