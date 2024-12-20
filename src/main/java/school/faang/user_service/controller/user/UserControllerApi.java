package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserResponseDto;

import java.util.List;

@Tag(name = "User Management", description = "Operations related to user management")
public interface UserControllerApi {
    @Operation(summary = "Register a new user", description = "Registers a new user and returns the created user data.")
    ResponseEntity<UserDto> registerUser(UserDto userDto);

    @Operation(summary = "Deactivate a user", description = "Deactivates a user by their ID.")
    ResponseEntity<Void> deactivatedUser(Long userId);

    List<UserResponseDto> getPremiumUsers(UserFilterDto userFilterDto);

    UserResponseDto getUser(long userId);

    List<UserResponseDto> getUsersByIds(List<Long> ids);

    List<Long> getOnlyActiveUsersFromList(List<Long> ids);
}
