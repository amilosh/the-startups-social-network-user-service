package school.faang.user_service.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;

@Slf4j
@Component
@AllArgsConstructor
public class AvatarLibrary {

    public ResponseEntity<byte[]> getPictureFromResponse(byte[] fileBytes) {
        return new ResponseEntity<>(fileBytes, getHeaders(), HttpStatus.OK);
    }

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

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("image/svg+xml"));

        return headers;
    }
}
