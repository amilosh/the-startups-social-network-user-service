package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.AvatarService;

@RestController
@RequestMapping("/users/{userId}/avatar")
@RequiredArgsConstructor
@Validated
public class AvatarController {
    private final AvatarService avatarService;

    @PostMapping
    @Operation(summary = "Upload user avatar", description = "Upload an avatar for a chosen user")
    public ResponseEntity<String> uploadUserAvatar(
            @PathVariable @Min(1) Long userId,
            @RequestParam("avatar") MultipartFile avatar
    ) throws Exception {
        avatarService.uploadUserAvatar(userId, avatar);
        return ResponseEntity.status(HttpStatus.CREATED).body("Avatar uploaded successfully");
    }

    @DeleteMapping
    @Operation(summary = "Delete user avatar", description = "Delete the avatar of a chosen user")
    public ResponseEntity<String> deleteUserAvatar(
            @PathVariable @Min(1) Long userId
    ) throws Exception {
        avatarService.deleteUserAvatar(userId);
        return ResponseEntity.ok("Avatar deleted successfully");
    }
}
