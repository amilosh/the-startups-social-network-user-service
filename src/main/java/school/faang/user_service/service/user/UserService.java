package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto getUser(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND, userId)));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow();
        user.setUsername(userDto.getUsername());
        user.setUpdatedAt(userDto.getUpdatedAt());
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<Long> getNotExistingUserIds(List<Long> userIds) {
        return userIds.isEmpty() ? Collections.emptyList() : userRepository.findNotExistingUserIds(userIds);
    }
}
