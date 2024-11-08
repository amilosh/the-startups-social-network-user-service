package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Tag(name = "API for managing information about users.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/not-existing-ids")
    public ResponseEntity<List<Long>> getNotExistingUserIds(@RequestBody @NotNull List<@NotNull Long> userIds) {
        List<Long> notExistingUserIds = userService.getNotExistingUserIds(userIds);
        return ResponseEntity.ok(notExistingUserIds);
    }

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        return userService.getPremiumUsers(filters);
    }
}