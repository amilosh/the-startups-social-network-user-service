package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.service.UserService;

@Controller
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    public String deactivateUser(@NotNull @Positive long userId){
        boolean isDeactivate = userService.isDeactivatedUser(userId);

        if(isDeactivate){
            return "User successfully deactivated";
        } else {
            return "User not found";
        }
    }
}
