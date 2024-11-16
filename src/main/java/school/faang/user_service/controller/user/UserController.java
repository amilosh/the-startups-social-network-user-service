package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;

    @PutMapping
    public void deactivateUser(@RequestParam Long userId) {
        userService.deactivateUser(userId);
        log.info("User with ID {} has been scheduled for the deactivation", userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@ModelAttribute UserFilterDto filterDto) {
        return userService.getUser(filterDto).toList();
    }

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        userValidator.validateIds(ids);
        return userService.getUsersByIds(ids);
    }
}