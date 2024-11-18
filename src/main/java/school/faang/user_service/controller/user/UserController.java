package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserIdsDto;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserProfilePicMapper;
import school.faang.user_service.service.avatar.AvatarService;
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
    private final AvatarService avatarService;
    private final UserProfilePicMapper userProfilePicMapper;

    @Operation(summary = "Get user information by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found successfully"),
            @ApiResponse(responseCode = "404", description = "User was not found")
    })
    @GetMapping("/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @Operation(summary = "Get users' information by theirs ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/by-ids")
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody @Valid UserIdsDto request) {
        List<UserDto> users = userService.getUsers(request.getUserIds());
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get IDs of non-existing users from provided list",
            description = "Returns a list of user IDs from the provided list that are not present in the database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of non-existing user IDs"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/not-existing-ids")
    public ResponseEntity<List<Long>> getNotExistingUserIds(@RequestBody @Valid UserIdsDto request) {
        List<Long> notExistingUserIds = userService.getNotExistingUserIds(request.getUserIds());
        return ResponseEntity.ok(notExistingUserIds);
    }

    @PostMapping("/not-premium")
    public List<UserDto> getNotPremiumUsers(@RequestBody UserFilterDto filters) {
        return userService.getNotPremiumUsers(filters);
    }

    @PostMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filters) {
        return userService.getPremiumUsers(filters);
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<UserProfilePicDto> uploadUserAvatar(@PathVariable Long userId,
                                                              @RequestParam(value = "file", required = false) MultipartFile file,
                                                              @RequestParam(value = "generate", required = false, defaultValue = "false") boolean generate) {
        UserProfilePic userProfilePic = avatarService.uploadUserAvatar(userId, file, generate);
        return ResponseEntity.ok(userProfilePicMapper.toDto(userProfilePic));
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable Long userId) {
        byte[] avatarData = avatarService.getUserAvatar(userId);
        ByteArrayResource resource = new ByteArrayResource(avatarData);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(avatarData.length)
                .body(resource);
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<Void> deleteUserAvatar(@PathVariable Long userId) {
        avatarService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}
