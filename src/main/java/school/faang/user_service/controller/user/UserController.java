package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    public void deactivateUser(@RequestParam Long userId) {
        userService.deactivateUser(userId);
        log.info("User with ID {} has been scheduled for the deactivation", userId);
    }
    @GetMapping(value = "premium")
    public List<UserDto> getUsers(@ModelAttribute UserFilterDto filterDto) {
        return userService.getUser(filterDto).toList();
    }
}