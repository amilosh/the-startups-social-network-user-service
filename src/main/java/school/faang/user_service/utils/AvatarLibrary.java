package school.faang.user_service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;

@Slf4j
@Component
public class AvatarLibrary {

    public URI getServiceUri() {
        String apiVersion = "9.x";
        List<String> styles = Arrays.asList("bottts", "big-smile", "bottts-neutral", "thumbs", "avataaars");
        int randomIndexOfStyles = new Random().nextInt(styles.size());
        int randomSeedOfAvatar = (int) (Math.random() * 5_000);

        try {
            return new URI(format("https://api.dicebear.com/%s/%s/svg?seed=%d", apiVersion, styles.get(randomIndexOfStyles), randomSeedOfAvatar));
        } catch (URISyntaxException e) {
            log.error("Failed to get avatar library uri");
            throw new RuntimeException(e);
        }
    }
}
