package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.service.UserDeactivationService;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserV1Controller {
    private final UserDeactivationService userDeactivationService;
    private final UserService userService;

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<DeactivatedUserDto> deactivateUser(@PathVariable @NotNull @Positive long userId) {
        DeactivatedUserDto deactivatedUser = userDeactivationService.deactivateUser(userId);
        return ResponseEntity.ok(deactivatedUser);
    }

    @PostMapping("/premium")
    public List<UserSubResponseDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }
}