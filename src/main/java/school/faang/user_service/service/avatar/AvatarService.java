package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import school.faang.user_service.exception.DiceBearException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.properties.DiceBearProperties;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final WebClient diceBearClient;
    private final DiceBearProperties diceBearProperties;

    public Optional<byte[]> getRandomDiceBearAvatar(Long userId) {
        log.info("Requesting random avatar for user ID: {} with style: {}, format: {}",
                userId, diceBearProperties.getStyle(), diceBearProperties.getFormat());
        try {
            byte[] avatarData = diceBearClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment(
                                    diceBearProperties.getVersion(),
                                    diceBearProperties.getStyle(),
                                    diceBearProperties.getFormat())
                            .queryParam("seed", userId)
                            .build())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (avatarData == null || avatarData.length == 0) {
                log.warn("Received empty avatar content for user ID: {}", userId);
                return Optional.empty();
            }

            log.info("Successfully retrieved avatar for user ID: {}. Data length: {}", userId, avatarData.length);
            return Optional.of(avatarData);
        } catch (WebClientResponseException e) {
            throw new DiceBearException(String.format(ErrorMessage.DICE_BEAR_RETRIEVAL_ERROR, e.getStatusCode()), e);
        } catch (Exception e) {
            throw new DiceBearException(ErrorMessage.DICE_BEAR_UNEXPECTED_ERROR, e);
        }
    }
}
