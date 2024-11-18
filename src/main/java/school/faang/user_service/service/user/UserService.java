package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
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

    public ResponseEntity<UserDto> getUserDtoById(long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found!"));
        return ResponseEntity.ok().body(userMapper.toDto(user));
    }

    public ResponseEntity<List<UserDto>> getUserDtosByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllByIds(userIds)
                .orElseThrow(() -> new IllegalArgumentException("users not found!"));
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok().body(userDtos);
    }
}
