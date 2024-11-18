package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DiceBearException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.MinioException;
import school.faang.user_service.properties.DiceBearProperties;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.minio.MinioService;
import school.faang.user_service.service.user.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static school.faang.user_service.exception.ErrorMessage.AVATAR_ALREADY_EXIST_ERROR;
import static school.faang.user_service.exception.ErrorMessage.AVATAR_NOT_FOUND;
import static school.faang.user_service.exception.ErrorMessage.DICE_BEAR_GENERATING_ERROR;
import static school.faang.user_service.exception.ErrorMessage.AVATAR_PROCESS_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final String AVATAR_FILE_FORMAT = "%d.jpg";
    private static final String SMALL_AVATAR_FILE_FORMAT = "%d_small.jpg";
    private static final int AVATAR_CONSTRAINT = 1080;
    private static final int SMALL_AVATAR_CONSTRAINT = 170;
    private static final String DEFAULT_AVATAR_CONTENT_TYPE = "image/jpeg";

    private final WebClient diceBearClient;
    private final MinioService minioService;
    private final DiceBearProperties diceBearProperties;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public byte[] getUserAvatar(Long userId) {
        log.info("Start receiving avatar for user ID: {}", userId);
        UserProfilePic userProfilePic = userRepository.findUserProfilePicByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(AVATAR_NOT_FOUND, userId)));
        return minioService.downloadFile(userProfilePic.getFileId());
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        log.info("Deleting avatar for user ID: {}", userId);
        UserProfilePic userProfilePic = userRepository.findUserProfilePicByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(AVATAR_NOT_FOUND, userId)));

        if (userProfilePic.getFileId() != null) {
            minioService.deleteFile(userProfilePic.getFileId());
        }
        if (userProfilePic.getSmallFileId() != null) {
            minioService.deleteFile(userProfilePic.getSmallFileId());
        }

        userRepository.deleteProfilePic(userId);
        log.info("Avatar successfully deleted for user ID: {}", userId);
    }

    @Transactional
    public UserProfilePic uploadUserAvatar(Long userId, MultipartFile file, boolean generate) {
        log.info("Start uploading avatar for user ID: {}", userId);
        checkAvatarExists(userId);

        byte[] avatarData;
        String contentType;

        if (generate) {
            log.info("No file provided for user ID: {}. Generating random avatar.", userId);
            avatarData = getRandomDiceBearAvatar(userId)
                    .orElseThrow(() -> new DiceBearException(DICE_BEAR_GENERATING_ERROR));
            contentType = DEFAULT_AVATAR_CONTENT_TYPE;
        } else {
            try {
                avatarData = file.getBytes();
                contentType = file.getContentType();
                log.info("Uploaded avatar received for user ID: {}. File size: {} bytes, Content type: {}", userId, avatarData.length, contentType);
            } catch (IOException e) {
                log.error("Failed to read the uploaded file for user ID: {}", userId, e);
                throw new RuntimeException("Failed to read the uploaded file", e);
            }
        }

        return processAndSaveAvatar(userId, avatarData, contentType);
    }

    private UserProfilePic processAndSaveAvatar(Long userId, byte[] avatarData, String contentType) {
        try {
            byte[] fileBytes = resizeImage(avatarData, AVATAR_CONSTRAINT);
            byte[] smallFileBytes = resizeImage(avatarData, SMALL_AVATAR_CONSTRAINT);

            String fileName = String.format(AVATAR_FILE_FORMAT, userId);
            String smallFileName = String.format(SMALL_AVATAR_FILE_FORMAT, userId);

            minioService.uploadFile(userId, fileName, fileBytes, contentType);
            minioService.uploadFile(userId, smallFileName, smallFileBytes, contentType);
            userRepository.updateProfilePic(userId, fileName, smallFileName);

            log.info("Avatar successfully uploaded for user ID: {}", userId);
            return new UserProfilePic(fileName, smallFileName);
        } catch (IOException e) {
            log.error("Error resizing and saving avatar for user ID: {}", userId, e);
            throw new RuntimeException(AVATAR_PROCESS_ERROR, e);
        }
    }

    private void checkAvatarExists(Long userId) {
        UserProfilePic userProfilePic = userService.getUserEntity(userId).getUserProfilePic();
        if (userProfilePic != null && (userProfilePic.getFileId() != null || userProfilePic.getSmallFileId() != null)) {
            throw new MinioException(String.format(AVATAR_ALREADY_EXIST_ERROR, userId));
        }
    }

    byte[] resizeImage(byte[] imageData, int size) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .size(size, size)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }

    Optional<byte[]> getRandomDiceBearAvatar(Long userId) {
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
            log.error("Error retrieving avatar from DiceBear API, status: {}, user ID: {}", e.getStatusCode(), userId, e);
            throw new DiceBearException(String.format(ErrorMessage.DICE_BEAR_RETRIEVAL_ERROR, e.getStatusCode()), e);
        } catch (Exception e) {
            log.error("Unexpected error retrieving avatar from DiceBear API for user ID: {}", userId, e);
            throw new DiceBearException(ErrorMessage.DICE_BEAR_UNEXPECTED_ERROR, e);
        }
    }
}
