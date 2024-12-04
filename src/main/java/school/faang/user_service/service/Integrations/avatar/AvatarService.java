package school.faang.user_service.service.Integrations.avatar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class AvatarService {

    @Value("${integration.dice-bear.base-url}")
    private String baseUrl;

    @Value("${integration.dice-bear.styles}")
    private List<String> styles;

    @Value("${integration.dice-bear.seed-names}")
    private List<String> seedNames;

    @Value("${integration.dice-bear.version}")
    private String version;

    private final Random random = new Random();

    private void validateConfiguration() {
        if (styles == null || styles.isEmpty()) {
            throw new IllegalStateException("Styles list is empty or not configured properly.");
        }
        if (seedNames == null || seedNames.isEmpty()) {
            throw new IllegalStateException("Seed names list is empty or not configured properly.");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Base URL is not configured properly.");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalStateException("API version is not configured properly.");
        }
    }

    private String generateUrlRandomAvatar() {
        validateConfiguration();

        String randomStyle = styles.get(random.nextInt(styles.size()));
        String randomSeed = seedNames.get(random.nextInt(seedNames.size()));

        return String.format("%s/%s/%s/svg?seed=%s", baseUrl, version, randomStyle, randomSeed);
    }

    public String getRandomAvatar() {
        try {
            return generateUrlRandomAvatar();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate random avatar URL", e);
        }
    }
}

