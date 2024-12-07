package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import school.faang.user_service.dto.UserFilterDto;
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
    public ResponseEntity<ProcessResultDto> uploadToCsv(@RequestParam("file") @CsvFile MultipartFile file) throws IOException{
        String filename = file.getOriginalFilename();
        long fileSize = file.getSize();
        log.info("Received file: name = {}, size = {} bytes", filename, fileSize);
            ProcessResultDto result = userService.importUsersFromCsv(file.getInputStream());
            log.info("File '{}' uploaded successfully. Processed {} records with {} errors.",
                    filename, result.getСountSuccessfullySavedUsers(), result.getErrors().size());
            return ResponseEntity.ok(result);
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

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of users with optional filtering and pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    public List<UserDto> getAllUsers(
            @Valid UserFilterDto filter) {
        return userService.getAllUsers(filter);
    }

    @GetMapping("/premium")
    @Operation(summary = "Get premium users", description = "Retrieve a list of premium users with optional filtering and pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list premium users")
    public List<UserDto> getPremiumUsers(
            @Valid UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    @GetMapping("/ids")
    public List<UserDto> getUsersByIds(@RequestParam List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
