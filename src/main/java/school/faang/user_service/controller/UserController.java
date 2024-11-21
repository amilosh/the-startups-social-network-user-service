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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.convertor.CsvToPojoConverter;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.pojo.Person;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final CsvToPojoConverter csvToPojoConverter;

    @PostMapping("/import")
    public ResponseEntity<String> uploadToCsv(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file.getSize());
        System.out.println(file.getName());
        if (file.isEmpty()) {
            log.error("File is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        log.info("File name: {}", file.getOriginalFilename());
        log.info("File size: {}", file.getSize());
        log.info("File content type: {}", file.getContentType());

        if (!"text/csv".equals(file.getContentType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid file type. Please upload the CSV file");
        }
        InputStream inputStream = file.getInputStream();

        List<Person> persons = csvToPojoConverter.convertCsvToPojo(inputStream);
        userService.processUsers(persons);
        log.info("file upload");

        return ResponseEntity.status(HttpStatus.CREATED).build();
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
