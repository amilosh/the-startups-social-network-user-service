package school.faang.user_service.service.user.google;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserGoogleInfoDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserGoogleInfo;
import school.faang.user_service.mapper.user.google.UserGoogleInfoMapper;
import school.faang.user_service.repository.UserGoogleInfoRepository;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGoogleInfoService {
    private final UserGoogleInfoRepository userGoogleInfoRepository;
    private final UserService userService;
    private final UserGoogleInfoMapper userGoogleInfoMapper;
    private final UserContext userContext;

    public UserGoogleInfoDto bindUserWithGoogleInfo() {
        User user = userService.getUserById(userContext.getUserId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        UserGoogleInfo userGoogleInfo =
                userGoogleInfoMapper.userAttributesToUserGoogleInfo(token.getPrincipal().getAttributes());
        userGoogleInfo.setUser(user);

        Optional<UserGoogleInfo> existingEntity = userGoogleInfoRepository.findBySub(userGoogleInfo.getSub());

        existingEntity.ifPresent(googleInfo -> userGoogleInfo.setId(googleInfo.getId()));
        return userGoogleInfoMapper.toDto(userGoogleInfoRepository.save(userGoogleInfo));
    }

    public String getGoogleEmailOrDefaultByUserId(long userId) {
        User user = userService.getUserById(userId);
        Optional<UserGoogleInfo> optionalUserGoogleInfo = userGoogleInfoRepository.findByUser(user);

        if (optionalUserGoogleInfo.isPresent()) {
            return optionalUserGoogleInfo.get().getEmail();
        }

        return user.getEmail();
    }

    public List<String> getAllGoogleEmailsByUserIds(List<Long> userIds) {
        Map<Long, String> emails = userGoogleInfoRepository.findAllByUserIds(userIds).stream()
                .collect(Collectors.toMap(UserGoogleInfo::getId, UserGoogleInfo::getEmail));

        return userIds.stream()
                .map(userId -> {
                    if (emails.get(userId) == null) {
                        return userService.getUserById(userId).getEmail();
                    }

                    return emails.get(userId);
                })
                .toList();
    }
}
