package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.request.UsersDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable @Positive long userId) {
        return ResponseEntity.ok(userService.findUserDtoById(userId));
    }

    @PostMapping
    public ResponseEntity<List<UserDto>> getUsersByIds(@Valid @RequestBody UsersDto ids) {
        return ResponseEntity.ok(userService.getUsersByIds(ids));
    }

    @PutMapping("{userId}")
    public ResponseEntity<UserDto> deactivateProfile(@PathVariable @Positive long userId) {
        return ResponseEntity.ok(userService.deactivateProfile(userId));
    }
}
