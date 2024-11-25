package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.CsvFile;
import school.faang.user_service.validator.FileNotEmpty;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @PostMapping("/upload")
    public ResponseEntity<ProcessResultDto> uploadToCsv(@RequestParam("file") @CsvFile @FileNotEmpty MultipartFile file) {
        try {
            ProcessResultDto result = userService.processUsers(file.getInputStream());
            log.info("file upload");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ProcessResultDto(0, List.of("Failed to read CSV file: " + e.getMessage())));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable @Positive long userId) {
        userValidator.validateUserById(userId);
        return ResponseEntity.ok(userService.findUserDtoById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> deactivateProfile(@PathVariable @Positive long userId) {
        userValidator.validateUserById(userId);
        return ResponseEntity.ok(userService.deactivateProfile(userId));
    }
}
