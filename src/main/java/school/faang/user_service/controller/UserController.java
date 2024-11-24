package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        return userMapper.toDto(userService.getUserById(id));
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PutMapping("/deactivate")
    public UserDto deactivateUser() {
        return userService.deactivateUser();
    }

}

