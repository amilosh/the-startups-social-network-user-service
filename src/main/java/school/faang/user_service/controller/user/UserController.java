package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserIdsDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Tag(name = "API for managing information about users.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId){
        return userService.getUser(userId);
    }

    @PostMapping("/by-ids")
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody @Valid UserIdsDto request) {
        List<UserDto> users = userService.getUsers(request.getUserIds());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/not-existing-ids")
    public ResponseEntity<List<Long>> getNotExistingUserIds(@RequestBody @Valid UserIdsDto request) {
        List<Long> notExistingUserIds = userService.getNotExistingUserIds(request.getUserIds());
        return ResponseEntity.ok(notExistingUserIds);
    }

    @PostMapping("/save")
    public ResponseEntity<UserDto> saveUser(@RequestBody @Valid UserDto userDto) {
        UserDto savedUser = userService.saveUser(userDto);
        return ResponseEntity.ok(savedUser);
    }
}
