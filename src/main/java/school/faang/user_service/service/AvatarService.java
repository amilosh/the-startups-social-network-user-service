package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarNotFoundException;
import school.faang.user_service.exception.FileSizeExceededException;
import school.faang.user_service.service.storage.StorageService;

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

    @Transactional
    public void uploadUserAvatar(Long userId, MultipartFile userAvatarPicture) throws Exception {
        User user = userService.findUserById(userId);

        if (userAvatarPicture.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("File size should not exceed " + MAX_FILE_SIZE / (1024 * 1024) + " Mb");
        }

        String largeImageFileName = "avatar_userId_" + userId + "_large." + IMAGE_FORMAT;
        String smallImageFileName = "avatar_userId_" + userId + "_small." + IMAGE_FORMAT;

        byte[] largeImageBytes = createThumbnail(userAvatarPicture, LARGE_IMAGE_MAX_SIZE);
        byte[] smallImageBytes = createThumbnail(userAvatarPicture, SMALL_IMAGE_MAX_SIZE);

        storageService.uploadFile(largeImageFileName, largeImageBytes, "image/" + IMAGE_FORMAT);
        storageService.uploadFile(smallImageFileName, smallImageBytes, "image/" + IMAGE_FORMAT);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(largeImageFileName);
        userProfilePic.setSmallFileId(smallImageFileName);
        user.setUserProfilePic(userProfilePic);

        userService.saveUser(user);
    }

    private byte[] createThumbnail(MultipartFile userAvatarPicture, int size) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(userAvatarPicture.getInputStream())
                .size(size, size)
                .outputFormat(IMAGE_FORMAT)
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

    @Transactional
    public void deleteUserAvatar(Long userId) throws Exception {
        User user = userService.findUserById(userId);

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic != null) {
            String largeImageFileName = userProfilePic.getFileId();
            String smallImageFileName = userProfilePic.getSmallFileId();

            storageService.deleteFile(largeImageFileName);
            storageService.deleteFile(smallImageFileName);

            user.setUserProfilePic(null);
            userService.saveUser(user);
        } else {
            throw new AvatarNotFoundException("User does not have an avatar to delete");
        }
    }

}
