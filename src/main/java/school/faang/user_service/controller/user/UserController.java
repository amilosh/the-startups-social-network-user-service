package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.userDto.UserFilterDto;
import school.faang.user_service.dto.userDto.UserPremiumDto;
import school.faang.user_service.service.UserPremiumService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserPremiumService userPremiumService;

    @GetMapping(value = "/premium")
    public ResponseEntity<List<UserPremiumDto>> getListPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            log.error("userFilterDto ничего не содержит");
            throw new IllegalArgumentException("userFilterDto ничего не содержит");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userPremiumService.getPremiumUsers(userFilterDto));
    }
}
