package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.UserProfilePicMapper;
import school.faang.user_service.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserProfilePicService {
    private static final int MAX_AVATAR_SIZE_MEGABYTE = 5;
    private static final int LARGE_PHOTO_WIDTH = 1080;
    private static final int SMALL_PHOTO_WIDTH = 170;
    private static final String FOLDER_NAME = "user_profile_avatar";

    private final UserService userService;
    private final S3Service s3Service;
    private final UserProfilePicMapper userProfilePicMapper;
    private final ImageUtils imageUtils;

    public UserProfilePicDto updateUserProfilePicture(Long userId, MultipartFile file) {
        log.info("Request to update profile picture for user {}", userId);
        User user = userService.getUserById(userId);
        validateAvatarSize(file);
        validateThatFileIsImage(file);

        BufferedImage originalImage = imageUtils.convertMultiPartFileToBufferedImage(file);
        BufferedImage largeImage = imageUtils.resizeImage(originalImage, LARGE_PHOTO_WIDTH);
        BufferedImage smallImage = imageUtils.resizeImage(originalImage, SMALL_PHOTO_WIDTH);

        String fileId = s3Service.uploadImage(file, FOLDER_NAME,
                "avatarPictureUser%s".formatted(user.getId()), largeImage);
        String smallFileId = s3Service.uploadImage(file, FOLDER_NAME,
                "smallAvatarPictureUser%s".formatted(user.getId()), smallImage);
        user.addUserProfilePic(fileId, smallFileId);
        userService.updateUser(user);

        log.info("Changed user profile picture for user {}", user.toStringProfilePicInfo());
        return userProfilePicMapper.userProfilePicToDto(user.getUserProfilePic());
    }

    public InputStreamResource getUserAvatar(long userId) {
        User user = userService.getUserById(userId);
        log.info("Request to get profile picture for user {}", user.toStringProfilePicInfo());
        if (user.getUserProfilePic() == null) {
            return new InputStreamResource(new ByteArrayInputStream(new byte[0]));
        }
        String key = user.getUserProfilePic().getFileId();
        return s3Service.getFile(key);
    }

    public void deleteUserAvatar(long userId) {
        User user = userService.getUserById(userId);
        log.info("Request to delete profile picture for user {}", user.toStringProfilePicInfo());
        if (user.getUserProfilePic() == null) {
            return;
        }
        String fileId = user.getUserProfilePic().getFileId();
        String smallFileId = user.getUserProfilePic().getSmallFileId();
        user.deleteUserProfilePic();
        log.info("User after deleted profile picture {}", user.toStringProfilePicInfo());
        userService.updateUser(user);

        s3Service.deleteFiles(fileId, smallFileId);
    }

    private void validateAvatarSize(MultipartFile file) {
        double fileSize = bytesToMegabytes(file.getSize());
        if (fileSize > MAX_AVATAR_SIZE_MEGABYTE) {
            throw new DataValidationException("The file size exceeds 5 megabytes");
        }
    }

    private double bytesToMegabytes(long bytes) {
        return bytes / (Math.pow(1024, 2));
    }

    private void validateThatFileIsImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (!contentType.contains("image")) {
            throw new DataValidationException("The file is not image");
        }
    }
}
