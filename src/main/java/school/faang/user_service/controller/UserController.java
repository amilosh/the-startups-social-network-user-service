package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.request.UsersDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userMapper.toDto(userService.findUser(userId)));
    }

    @PostMapping
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody UsersDto ids) {
        return ResponseEntity.ok(userService.getUsersByIds(ids));
    }

    @PutMapping("{userId}")
    public ResponseEntity<UserDto> deactivateProfile(@PathVariable @Positive long userId) {
        return ResponseEntity.ok(userService.deactivateProfile(userId));
    }
}
