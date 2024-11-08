package school.faang.user_service.service.userService;

import org.springframework.stereotype.Service;


import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

@Service
public class UserService {
    public UserDto convertToDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}