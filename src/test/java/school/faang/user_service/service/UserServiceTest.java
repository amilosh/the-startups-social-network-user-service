package school.faang.user_service.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.PersonMapper;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserServiceValidator;

<<<<<<< HEAD
import java.io.IOException;
=======
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
>>>>>>> 850a035afcc9bbdca60d8b6eafa7e135a817997c
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Spy
    private PersonMapper personMapper;

    @Mock
    private UserServiceValidator validator;

    @Mock
    private CountryService countryService;

    @Spy
    private CsvMapper csvMapper;

    private long userId;

    private User user;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = new User();
    }

    @Test
    public void testExistsUserById() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertTrue(userService.existsById(userId));
    }

    @Test
    public void testNotExistsUserById() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertFalse(userService.existsById(userId));
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUserById(userId));
    }

    @Test
    public void testThrowExceptionGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUserByIdNotfound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUsersByIds() {
        List<User> users = List.of(new User(), new User());
        List<Long> ids = List.of(1L, 2L);

        when(userRepository.findAllById(ids)).thenReturn(users);
        userService.getUsersByIds(ids);
    }

    @Test
    void findById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        assertEquals(Optional.of(new User()), userService.findById(userId));
    }

    @Test
    void getUserDtoByIdSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        assertEquals(userMapper.toDto(new User()), userService.getUserDtoById(userId));
    }

    @Test
    void getUserDtoByIdFailed() {
        when(userRepository.findById(userId)).thenThrow(new EntityNotFoundException(String.format("User with id %s not found", userId)));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(userId));
        assertEquals("User with id %s not found".formatted(userId), exception.getMessage());
    }

    @Test
    void uploadCsvUsersSuccess() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.lenient().when(file.getInputStream()).thenReturn(
                UserServiceTest.class.getClassLoader().getResourceAsStream("files/students-test.csv"));
        Mockito.lenient().when(userRepository.saveAll(Mockito.anyList())).thenReturn(List.of(new User()));

        assertEquals(List.of(userMapper.toDto(new User())), userService.uploadCsvUsers(file));
    }

    @Test
    void uploadCsvUsers_BadFile() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.lenient().when(file.getInputStream()).thenReturn(
                UserServiceTest.class.getClassLoader().getResourceAsStream("files/students-bad-file.csv"));
        Mockito.lenient().when(userRepository.saveAll(Mockito.anyList())).thenReturn(List.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.uploadCsvUsers(file));
        assertEquals("Error while reading file", exception.getMessage());
    }
}
