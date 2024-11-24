package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserAvatarSize;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.USERS)
public class UserController {
    private final UserService userService;

    @GetMapping(UrlUtils.ID)
    public UserDto getUser(@PathVariable("id") @Min(1) Long id) {
        return userService.getUser(id);
    }

    @PostMapping()
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PutMapping(UrlUtils.ID + UrlUtils.AVATAR)
    public void updateUserAvatar(@PathVariable("id") @Min(1) Long id, @RequestBody MultipartFile avatar) {
        userService.updateUserAvatar(id, avatar);
    }

    @GetMapping(value = UrlUtils.ID + UrlUtils.AVATAR + UrlUtils.SMALL)
    public ResponseEntity<byte[]> getSmallAvatar(@PathVariable("id") @Min(1) Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(userService.getUserAvatar(id, UserAvatarSize.SMALL));
    }

    @GetMapping(value = UrlUtils.ID + UrlUtils.AVATAR + UrlUtils.LARGE)
    public ResponseEntity<byte[]> getLargeAvatar(@PathVariable("id") @Min(1) Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(userService.getUserAvatar(id, UserAvatarSize.LARGE));
    }

    @DeleteMapping(UrlUtils.ID + UrlUtils.AVATAR)
    public void deleteUserAvatar(@PathVariable("id") @Min(1) Long id) {
        userService.deleteUserAvatar(id);
    }
}
