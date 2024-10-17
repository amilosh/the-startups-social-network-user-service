package school.faang.user_service.service.user;

import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.PaymentDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserDtoForRegistration;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.image.AvatarSize;

import java.awt.image.BufferedImage;
import java.util.List;

public interface UserService {
    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> userIds);

    UserDto uploadUserAvatar(Long userId, BufferedImage uploadedImage);

    Resource downloadUserAvatar(long userId, AvatarSize size);

    void deleteUserAvatar(long userId);

    UserDto register(UserDtoForRegistration userDto);

    void uploadUsers(MultipartFile file);

    void buyPremium(PaymentDto paymentDto);
}