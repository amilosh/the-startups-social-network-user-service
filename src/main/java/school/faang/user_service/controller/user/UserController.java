package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(
        name = "Users",
        description = "API for managing user accounts, including user details, subscriptions, skills, and goals."
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping("/by-ids")
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody @Valid UserIdsDto request) {
        List<UserDto> users = userService.getUsers(request.getUserIds());
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get Not Existing User IDs",
            description = "Returns a list of user IDs that do not exist in the database."
    )
    @PostMapping("/not-existing-ids")
    public ResponseEntity<List<Long>> getNotExistingUserIds(@RequestBody @Valid UserIdsDto request) {
        List<Long> notExistingUserIds = userService.getNotExistingUserIds(request.getUserIds());
        return ResponseEntity.ok(notExistingUserIds);
    }
}
