package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id:%d не найден".formatted(id)));
    }

    public List<User> getAllUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void updateAllUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new
                EntityNotFoundException("User do not found by " + userId));
    }

    public List<UserSubResponseDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return userMapper.toUserSubResponseList(
                filterUsers(premiumRepository.findPremiumUsers(), userFilterDto));
    }

    private List<User> filterUsers(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> filters.stream()
                        .filter(filter -> filter.isApplicable(filterDto))
                        .allMatch(filter -> filter.apply(user)))
                .toList();
    }

    public UserSubResponseDto getUserDtoById(long userId) {
        User user = getUserById(userId);
        return userMapper.toUserSubResponseDto(user);
    }

    public List<UserSubResponseDto> getAllUsersDtoByIds(List<Long> ids) {
        List<User> users = getAllUsersByIds(ids);
        return userMapper.toUserSubResponseList(users);
    }
}
