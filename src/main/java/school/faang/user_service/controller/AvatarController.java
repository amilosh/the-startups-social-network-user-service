package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.AvatarService;

@RestController
@RequestMapping("/users/{userId}/avatar")
@RequiredArgsConstructor
@Validated
@Tag(name = "Avatar Management", description = "Operations related to user avatars")
public class AvatarController {
    private final AvatarService avatarService;

    @PostMapping
    @Operation(summary = "Upload user avatar", description = "Upload an avatar for a chosen user")
    public ResponseEntity<String> uploadUserAvatar(
            @PathVariable @Positive(message = "User ID must be positive") Long userId,
            @RequestParam("avatar") @NotNull(message = "Avatar file must be provided") MultipartFile avatar,
            @RequestHeader("Current-User-Id") Long currentUserId
    ) {
        avatarService.uploadUserAvatar(userId, currentUserId, avatar);
        return ResponseEntity.status(HttpStatus.CREATED).body("Avatar uploaded successfully");
    }

    @DeleteMapping
    @Operation(summary = "Delete user avatar", description = "Delete the avatar of a chosen user")
    public ResponseEntity<String> deleteUserAvatar(
            @PathVariable @Positive(message = "User ID must be positive") Long userId,
            @RequestHeader("Current-User-Id") Long currentUserId
    ) {
        avatarService.deleteUserAvatar(userId, currentUserId);
        return ResponseEntity.ok("Avatar deleted successfully");
    }
}
