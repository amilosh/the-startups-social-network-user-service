package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.exception.DiceBearException;
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
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public Optional<byte[]> getRandomAvatar(Long userId) {
        log.info("Requesting random avatar for user ID: {} with style: {}, format: {}",
                userId, diceBearProperties.getStyle(), diceBearProperties.getFormat());

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
                .blockOptional()
                .orElseThrow(() -> new DiceBearException("Failed to retrieve avatar"));

        if (avatarData == null || avatarData.length == 0) {
            log.warn("Received empty avatar content for user ID: {}", userId);
            return Optional.empty();
        }

        log.info("Successfully retrieved avatar for user ID: {}. Data length: {}", userId, avatarData.length);
        return Optional.of(avatarData);
    }

    @Recover
    public Optional<byte[]> recover(DiceBearException e, Long userId) {
        log.error("Failed to retrieve avatar after retries for user ID: {}", userId, e);
        return Optional.empty();
    }
}
