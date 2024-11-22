package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/{userId}/deactivate")
    public void deactivateUser(@PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        userService.deactivateUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@Valid @ModelAttribute UserFilterDto filterDto) {
        return userService.getUser(filterDto).toList();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("{ids}")
    public List<UserDto> getUsersByIds(@RequestParam @NotNull(message = "The list of IDs should not be null")
                                       @NotEmpty(message = "The list of IDs should not be empty")
                                       List<@NotNull(message = "Each ID in the list should not be null") Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
