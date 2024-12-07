package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserGoogleInfoDto;
import school.faang.user_service.service.user.google.UserGoogleInfoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/google")
public class UserGoogleInfoController {
    private final UserGoogleInfoService userGoogleInfoService;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClient;

    @GetMapping()
    public String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return oAuth2AuthorizedClient.loadAuthorizedClient("google", authentication.getName()).getAccessToken().getTokenValue();
    }

    @PostMapping("/emails")
    public List<String> getGoogleEmailsByUserIds(@RequestBody List<Long> ids) {
        return userGoogleInfoService.getAllGoogleEmailsByUserIds(ids);
    }

    @GetMapping("/emails/{userId}")
    public String getGoogleEmailOrDefaultByUserId(@PathVariable long userId) {
        return userGoogleInfoService.getGoogleEmailOrDefaultByUserId(userId);
    }

    @PostMapping
    public UserGoogleInfoDto bindUserWithGoogleInfo() {
        return userGoogleInfoService.bindUserWithGoogleInfo();
    }
}
