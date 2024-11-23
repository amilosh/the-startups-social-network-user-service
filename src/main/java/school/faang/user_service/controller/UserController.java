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
import school.faang.user_service.convertor.CsvToPojoConverter;
import school.faang.user_service.domain.Person;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

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

//    @PostMapping("/import")
//    public ResponseEntity<String> uploadToCsv(@RequestParam("file")@FileNotEmpty @CsvFile MultipartFile file) throws IOException {
////        System.out.println(file.getSize());
////        System.out.println(file.getName());
////        log.info("File name: {}", file.getOriginalFilename());
////        log.info("File size: {}", file.getSize());
////        log.info("File content type: {}", file.getContentType());
//        InputStream inputStream = file.getInputStream();
//        List<Person> persons = csvToPojoConverter.convertCsvToPojo(inputStream);
//        userService.processUsers(persons);
//        log.info("file upload");
//
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadToCsv(@RequestParam("file") MultipartFile file) {
        try {
//          userService.processCsv(file.getInputStream());
            InputStream inputStream = file.getInputStream();
        List<Person> persons = csvToPojoConverter.convertCsvToPojo(inputStream);
        userService.processUsers(persons);
        log.info("file upload");
          return ResponseEntity.ok("Users processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload CSV");
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
