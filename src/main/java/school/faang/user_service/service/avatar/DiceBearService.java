package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
public class DiceBearService {

    private final WebClient diceBearClient;
    private final DiceBearProperties diceBearProperties;

    @Retryable(
            retryFor = {DiceBearException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public Optional<byte[]> getRandomAvatar(Long userId) {
        log.info("Requesting random avatar for user ID: {} with style: {}, format: {}",
                userId, diceBearProperties.getStyle(), diceBearProperties.getFormat());

        try {
            Optional<byte[]> avatarData = diceBearClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment(
                                    diceBearProperties.getVersion(),
                                    diceBearProperties.getStyle(),
                                    diceBearProperties.getFormat())
                            .queryParam("seed", userId)
                            .build())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .blockOptional();

            if (avatarData.isEmpty() || avatarData.get().length == 0) {
                log.warn("Received empty avatar content for user ID: {}", userId);
                return Optional.empty();
            }

            log.info("Successfully retrieved avatar for user ID: {}. Data length: {}", userId, avatarData.get().length);
            return avatarData;
        } catch (WebClientResponseException e) {
            log.error("Error retrieving avatar from DiceBear API, status: {}, user ID: {}", e.getStatusCode(), userId, e);
            throw new DiceBearException(ErrorMessage.DICE_BEAR_UNEXPECTED_ERROR, e);
        }
    }
}
