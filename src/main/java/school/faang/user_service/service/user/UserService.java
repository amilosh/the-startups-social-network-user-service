package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }

    public UserDto getUserDtoById(long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("user not found!"));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUserDtosByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllByIds(userIds)
                .orElseThrow(() -> new DataValidationException("users not found!"));
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }
}
