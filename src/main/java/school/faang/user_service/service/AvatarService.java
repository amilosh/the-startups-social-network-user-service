package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarNotFoundException;
import school.faang.user_service.exception.AvatarProcessingException;
import school.faang.user_service.exception.InvalidFileFormatException;
import school.faang.user_service.service.storage.StorageService;
import school.faang.user_service.validator.AvatarValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int LARGE_IMAGE_MAX_SIZE = 1080;
    private static final int SMALL_IMAGE_MAX_SIZE = 170;
    private static final String IMAGE_FORMAT = "jpeg";

    private final StorageService storageService;
    private final UserService userService;
    private final AvatarValidator avatarValidator;

    @Transactional
    public void uploadUserAvatar(Long userId, Long currentUserId, MultipartFile userAvatarPicture) {
        avatarValidator.validateUserAuthorization(currentUserId, userId);

        User user = userService.findUserById(userId);
        avatarValidator.validateAvatarFile(userAvatarPicture);

        UserProfilePic existingAvatar = user.getUserProfilePic();
        if (existingAvatar != null) {
            deleteAvatarFiles(existingAvatar);
        }

        UserProfilePic userProfilePic = processAndUploadAvatar(userId, userAvatarPicture);

        user.setUserProfilePic(userProfilePic);
        userService.saveUser(user);
    }

    @Transactional
    public void deleteUserAvatar(Long userId, Long currentUserId) {
        avatarValidator.validateUserAuthorization(currentUserId, userId);

        User user = userService.findUserById(userId);

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            deleteAvatarFiles(userProfilePic);

            user.setUserProfilePic(null);
            userService.saveUser(user);
        } else {
            throw new AvatarNotFoundException("User with ID " + userId + " does not have an avatar to delete");
        }
    }

    private byte[] createThumbnail(MultipartFile userAvatarPicture, int size, String imageFormat) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(userAvatarPicture.getInputStream())
                    .size(size, size)
                    .outputFormat(imageFormat)
                    .toOutputStream(outputStream);
            return outputStream.toByteArray();
        } catch (IOException error) {
            throw new AvatarProcessingException("Error processing avatar image.", error);
        }
    }

    private void deleteAvatarFiles(UserProfilePic avatar) {
        String largeImageFileName = avatar.getFileId();
        String smallImageFileName = avatar.getSmallFileId();

        storageService.deleteFile(largeImageFileName);
        storageService.deleteFile(smallImageFileName);
    }

    private UserProfilePic createUserProfilePic(String largeImageFileName, String smallImageFileName) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(largeImageFileName);
        userProfilePic.setSmallFileId(smallImageFileName);
        return userProfilePic;
    }

    private UserProfilePic processAndUploadAvatar(Long userId, MultipartFile userAvatarPicture) {
        String contentType = userAvatarPicture.getContentType();
        String imageFormat = getImageFormatFromContentType(contentType);

        String largeImageFileName = "avatars/avatar_userId_" + userId + "_large." + imageFormat;
        String smallImageFileName = "avatars/avatar_userId_" + userId + "_small." + imageFormat;

        try {
            byte[] largeImageBytes = createThumbnail(userAvatarPicture, LARGE_IMAGE_MAX_SIZE, imageFormat);
            byte[] smallImageBytes = createThumbnail(userAvatarPicture, SMALL_IMAGE_MAX_SIZE, imageFormat);

            storageService.uploadFile(largeImageFileName, largeImageBytes, "image/" + imageFormat);
            storageService.uploadFile(smallImageFileName, smallImageBytes, "image/" + imageFormat);
        } catch (Exception error) {
            throw new AvatarProcessingException("Error processing avatar image.", error);
        }

        return createUserProfilePic(largeImageFileName, smallImageFileName);
    }

    private String getImageFormatFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> "jpeg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> throw new InvalidFileFormatException("Unsupported image format");
        };
    }

}
