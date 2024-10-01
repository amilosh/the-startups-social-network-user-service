package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.FileOperationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.image.AvatarSize;
import school.faang.user_service.service.image.BufferedImagesHolder;
import school.faang.user_service.service.image.ImageProcessor;
import school.faang.user_service.service.s3.S3Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String BIG_AVATAR_PICTURE_NAME = "bigPicture";
    public static final String SMALL_AVATAR_PICTURE_NAME = "smallPicture";
    public static final String FOLDER_PREFIX = "user";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ImageProcessor imageProcessor;
    private final S3Service s3Service;

    @Override
    public UserDto getUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with id %s not found".formatted(userId));
        }
        log.debug("User with id %s found".formatted(userId));
        return userMapper.userToUserDto(user.get());
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        List<Long> foundUserIds = users.stream().map(User::getId).toList();
        List<Long> notFoundUserIds = userIds.stream()
                .filter(userId -> !foundUserIds.contains(userId))
                .toList();
        log.debug("Users with ids %s not found"
                .formatted(String.join(", ", notFoundUserIds.stream().map(Object::toString).toList())));
        return userMapper.usersToUserDtos(users);
    }

    @Transactional
    public UserDto uploadUserAvatar(Long userId, BufferedImage uploadedImage) {
        log.debug("Starting upload user avatar for user ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id %s not found".formatted(userId)));
        BufferedImagesHolder scaledImage = imageProcessor.scaleImage(uploadedImage);
        String fileId = uploadFile(userId, imageProcessor.getImageOS(scaledImage.getBigPic()), BIG_AVATAR_PICTURE_NAME);
        String smallFieldId = uploadFile(userId, imageProcessor.getImageOS(scaledImage.getSmallPic()), SMALL_AVATAR_PICTURE_NAME);
        if (user.getUserProfilePic() != null) {
            log.info("Deleting old user avatar for user ID: {}", userId);
            deleteUserAvatar(userId);
        }
        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(fileId)
                .smallFileId(smallFieldId)
                .build());
        User updateUser = userRepository.save(user);
        log.info("User avatar uploaded successfully for user ID: {}", userId);
        return userMapper.userToUserDto(updateUser);
    }

    public Resource downloadUserAvatar(long userId, AvatarSize size) {
        log.debug("Starting download user avatar for user ID: {}, size: {}", userId, size);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id %s not found".formatted(userId)));
        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic == null) {
            throw new EntityNotFoundException("User with id %s does not have an avatar".formatted(userId));
        }
        String fileId = size == AvatarSize.SMALL ? userProfilePic.getSmallFileId() : userProfilePic.getFileId();
        try (InputStream userAvatarIS = s3Service.downloadFile(fileId)) {
            byte[] avatarBytes = userAvatarIS.readAllBytes();
            Resource avatarResource = new ByteArrayResource(avatarBytes);
            log.info("User avatar downloaded successfully for user ID: {}, size: {}", userId, size);
            return avatarResource;
        } catch (IOException e) {
            throw new FileOperationException("Failed to download user avatar for user ID: {}, size: {}"
                    .formatted(userId, size));
        }
    }

    public void deleteUserAvatar(long userId) {
        log.debug("Starting delete user avatar for user ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id %s not found"
                        .formatted(userId)));
        UserProfilePic userProfilePic = user.getUserProfilePic();
        log.info("Delete user avatar for user ID: {}", userId);
        if (userProfilePic == null) {
            throw new EntityNotFoundException("User with id %s does not have an avatar"
                    .formatted(userId));
        }
        user.setUserProfilePic(null);
        userRepository.save(user);
        log.info("User avatar removed from user profile for user ID: {}", userId);
        try {
            s3Service.deleteFile(userProfilePic.getFileId());
            s3Service.deleteFile(userProfilePic.getSmallFileId());
            log.info("User avatar deleted successfully for user ID: {}", userId);
        } catch (Exception e) {
            throw new FileOperationException("Failed to delete user avatar for user ID: {}"
                    .formatted(userId));
        }
    }

    private String uploadFile(Long userId, ByteArrayOutputStream outputStream, String fileName) {
        String key = String.format("%s/%d%s", FOLDER_PREFIX + userId, System.currentTimeMillis(), fileName);
        log.debug("Starting file upload for user ID: {}, key: {}", userId, key);
        try {
            s3Service.uploadFile(outputStream, key);
            log.info("File uploaded successfully for user ID: {}, key: {}", userId, key);
        } catch (Exception e) {
            throw new FileOperationException("Failed to upload file for user ID: {}, key: {}".formatted(userId, key));
        }
        return key;
    }
}