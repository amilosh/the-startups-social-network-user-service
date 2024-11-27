package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.service.UserProfilePicService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/userProfile")
@Validated
public class UserProfileV1Controller {
    private final UserProfilePicService userProfilePicService;

    @PutMapping("/{userId}/avatar")
    public UserProfilePicDto updateAvatar(@PathVariable("userId") @Positive long userId,
                                          @RequestParam MultipartFile file) {
        return userProfilePicService.updateUserProfilePicture(userId, file);
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<InputStreamResource> getAvatar(@PathVariable("userId") @Positive long userId) {
        InputStreamResource file = userProfilePicService.getUserAvatar(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/avatar")
    public void deleteAvatar(@PathVariable("userId") @Positive long userId) {
         userProfilePicService.deleteUserAvatar(userId);
    }
}
