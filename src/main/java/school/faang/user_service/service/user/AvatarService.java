package school.faang.user_service.service.user;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Setter
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final RestTemplate restTemplate;
    @Value("${dicebear.api.url}")
    private String dicebearApiUrl;
    private final AmazonS3 s3Client;
    @Value("${minio.avatarBucket}")
    private String bucketName;

    public String generateRandomAvatar(String seed, String filename) {
        String url = UriComponentsBuilder.fromHttpUrl(dicebearApiUrl)
                .queryParam("seed", seed)
                .toUriString();

        String avatar = restTemplate.getForObject(url, String.class);
        if (avatar == null) {
            throw new IllegalStateException("Could not generate an avatar");
        }
        return saveAvatar(avatar, filename);
    }

    public String saveAvatar(String svg, String fileName) {
        InputStream inputStream = new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8));
        try {
            if (!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(bucketName);
                String policyText = getPublicReadPolicy(bucketName);
                s3Client.setBucketPolicy(bucketName, policyText);
            }
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save an avatar to minio");
        }
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    private static String getPublicReadPolicy(String bucketName) {
        Policy bucketPolicy = new Policy().withStatements(
                new Statement(Statement.Effect.Allow)
                        .withPrincipals(Principal.AllUsers)
                        .withActions(S3Actions.GetObject)
                        .withResources(new Resource("arn:aws:s3:::" + bucketName + "/*")));
        return bucketPolicy.toJson();
    }
}
