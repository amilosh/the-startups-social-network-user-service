package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
public class UserMapperTest {
    private static User john;
    private static User alex;
    private static User jane;
    @Autowired
    private UserMapper userMapper;

    @BeforeAll
    static void setup() {
        john = new User();
        john.setId(1L);
        john.setUsername("JohnDoe");
        john.setEmail("johndoe@example.com");

        alex = new User();
        alex.setId(2L);
        alex.setUsername("AlexSmith");
        alex.setEmail("alexsmith@example.com");

        jane = new User();
        jane.setId(null);
        jane.setUsername("JaneBlack");
        jane.setEmail(null);
    }

    @Test
    public void userToUserDtoWithExpectedResultTest() {
        UserDto userDto = new UserDto(1L, "JohnDoe", "johndoe@example.com");
        assertEquals(userDto, userMapper.userToUserDto(john));
    }

    @Test
    public void userToUserDtoWithWrongResultTest() {
        UserDto userDto = new UserDto(1L, "JohnDoe", "johndoe@example.com");
        assertNotEquals(userDto, userMapper.userToUserDto(alex));
    }

    @Test
    public void userToUserDtoWithNullFieldsTest() {
        UserDto userDto = new UserDto(null, "JaneBlack", null);
        assertEquals(userDto, userMapper.userToUserDto(jane));
    }

    @Test
    public void userToUserDtoWithNullUserTest() {
        assertNull(userMapper.userToUserDto(null));
    }

    @Test
    public void userListToUserDtoListWithExpectedResultTest() {
        assertEquals(3, userMapper.userListToUserDtoList(List.of(john, alex, jane)).size());
    }
}
