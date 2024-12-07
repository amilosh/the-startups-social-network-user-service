package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.NotificationUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller", description = "Controller for managing users")
@ApiResponse(responseCode = "200", description = "User retrieved successfully")
@ApiResponse(responseCode = "204", description = "User deactivated successfully")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class UserController {
    private final UserService userService;
    private final UserContext userContext;

    @Operation(
            summary = "Deactivate a user",
            description = "Deactivate a user by their ID"
    )
    @PutMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        userService.deactivateUser(userId);
    }

    @Operation(
            summary = "Get users by filters",
            description = "Retrieve a list of users based on filter criteria"
    )
    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsersByFilter(@Valid @ModelAttribute UserFilterDto filterDto) {
        return userService.getUser(filterDto).toList();
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a user by their ID"
    )
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return userService.getUser(userId);
    }

    @Operation(
            summary = "Get user by ID to send it to Notification Service",
            description = "Retrieve a user by their ID and turns it into Notification UserDTO"
    )
    @GetMapping("/{id}/notification")
    @ResponseStatus(HttpStatus.OK)
    public NotificationUserDto getUserForNotification(@PathVariable Long id) {
        return userService.getNotificationUserDto(id);
    }

    @Operation(
            summary = "Get users by a list of IDs",
            description = "Retrieve a list of users by their IDs"
    )
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsersByIds(
            @RequestParam @NotNull(message = "The list of IDs should not be null")
            @NotEmpty(message = "The list of IDs should not be empty")
            List<@NotNull(message = "Each ID in the list should not be null") Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/avatar")
    public void addAvatar(@RequestParam("file") MultipartFile file) {
        userService.addAvatar(userContext.getUserId(), file);
    }

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar() {
        byte[] fileBytes = userService.getAvatar(userContext.getUserId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("image/svg+xml"));

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/upload-file")
    public void loadingUsersViaFile(@RequestParam("file") MultipartFile file)  {
        userService.loadingUsersViaFile(file);
    }

}
