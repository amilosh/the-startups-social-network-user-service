package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.avatar.AvatarService;

import java.util.Optional;

@RestController
@RequestMapping("/user/avatar")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService avatarService;

    //TODO: удалить после ревью, только для проверки работоспособности
    @GetMapping("/random")
    public ResponseEntity<byte[]> getRandomAvatar() {
        Optional<byte[]> avatarData = avatarService.getRandomDiceBearAvatar(666L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/png");
        return new ResponseEntity<>(avatarData.get(), headers, HttpStatus.OK);
    }
}
