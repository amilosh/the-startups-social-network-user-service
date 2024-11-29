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
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
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
    public ResponseEntity<ProcessResultDto> uploadToCsv(@RequestParam("file") @CsvFile MultipartFile file) {
        String filename = file.getOriginalFilename();
        long fileSize = file.getSize();
        log.info("Received file: name = {}, size = {} bytes", filename, fileSize);

        try {
            ProcessResultDto result = userService.importUsersFromCsv(file.getInputStream());
            log.info("File '{}' uploaded successfully. Processed {} records with {} errors.",
                    filename, result.getСountSuccessfullySavedUsers(), result.getErrors().size());
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Failed to process file '{}': {}", filename, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ProcessResultDto(0, List.of("Failed to read CSV file: " + e.getMessage())));
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ProcessResultDto(0, List.of("Internal server error: " + e.getMessage())));
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
