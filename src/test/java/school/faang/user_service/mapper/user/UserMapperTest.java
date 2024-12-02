package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserMapperTest {
    private final User john = User.builder()
            .id(1L)
            .username("JohnDoe")
            .email("johndoe@example.com")
            .build();
    private final User alex = User.builder()
            .id(2L)
            .username("AlexSmith")
            .email("alexsmith@example.com")
            .build();
    private final User jane =User.builder()
            .username("JaneBlack")
            .build();
    @Autowired
    private UserMapper userMapper;

    @Test
    public void userToUserDtoSuccessTest() {
        UserDto userDto = new UserDto(1L, "JohnDoe", "johndoe@example.com");
        assertEquals(userDto, userMapper.userToUserDto(john));
    }

    @Test
    public void userToUserDtoFailTest() {
        UserDto userDto = new UserDto(1L, "JohnDoe", "johndoe@example.com");
        assertNotEquals(userDto, userMapper.userToUserDto(alex));
    }

    @Test
    public void userToUserDtoForNullFieldsUserSuccessTest() {
        UserDto userDto = new UserDto(null, "JaneBlack", null);
        assertEquals(userDto, userMapper.userToUserDto(jane));
    }

    @Test
    public void userToUserDtoForNullUserSuccessTest() {
        assertNull(userMapper.userToUserDto(null));
    }

    @Test
    public void userListToUserDtoListSuccessTest() {
        assertEquals(3, userMapper.userListToUserDtoList(List.of(john, alex, jane)).size());
    }
}
