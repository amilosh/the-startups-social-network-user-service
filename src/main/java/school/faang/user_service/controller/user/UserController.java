package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @Operation(
            summary = "Get Not Existing User IDs",
            description = "Returns a list of user IDs that do not exist in the database."
    )
    @PostMapping("/not-existing-ids")
    public ResponseEntity<List<Long>> getNotExistingUserIds(
            @RequestBody
            @Parameter(description = "List of user IDs to check for existence", required = true)
            @NotNull List<@NotNull Long> userIds
    ) {
        List<Long> notExistingUserIds = userService.getNotExistingUserIds(userIds);
        return ResponseEntity.ok(notExistingUserIds);
    }
}
