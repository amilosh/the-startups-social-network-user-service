package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getUsersByIds(List<Long> ids) {
        validateUserIds(ids);
        return userMapper.userListToUserDtoList(userRepository.findAllById(ids));
    }

    public UserDto getUser(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User not found by id: %s", id))));
    }

    private void validateUserIds(List<Long> ids) {
        if (ids.stream().anyMatch(id -> id < 1)) {
            log.error("Invalid user ID passed. User ID must not be less than 1");
            throw new IllegalArgumentException("Invalid user ID passed. User ID must not be less than 1");
        }
    }
}
