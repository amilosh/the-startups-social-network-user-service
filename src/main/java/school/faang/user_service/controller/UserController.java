package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validation.ValidationController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ValidationController validationController;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        validationController.validateIdCorrect(userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        validationController.validateListIdsCorrect(ids);
        return userService.getUsersByIds(ids);
    }
}