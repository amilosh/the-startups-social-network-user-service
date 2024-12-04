package school.faang.user_service.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/users")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserSubResponseDto getUser(@Positive @PathVariable long userId) {
        return userService.getUserDtoById(userId);
    }

    @PostMapping("/get")
    public List<UserSubResponseDto> getUsersByIds(@NotEmpty @RequestBody List<@Positive Long> ids) {
        return userService.getAllUsersDtoByIds(ids);
    }
}
