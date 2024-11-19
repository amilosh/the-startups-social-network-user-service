package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId) {
        validateIdCorrect(userId);
        return userService.getUser(userId);
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        validateIdCorrect(ids);
        return userService.getUsersByIds(ids);
    }

    private void validateIdCorrect(long id) {
        if (id <= 0) {
            throw new DataValidationException("Incorrect id");
        }
    }

    private void validateIdCorrect(List<Long> ids) {
        if (ids == null) {
            throw new DataValidationException("List ids is null");
        }
    }
}
