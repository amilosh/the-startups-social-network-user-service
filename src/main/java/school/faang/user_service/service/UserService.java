package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;
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
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
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
        return filterUsers(premiumRepository.findPremiumUsers(), userFilterDto);
    }

    private List<UserSubResponseDto> filterUsers(Stream<User> users, UserFilterDto filterDto) {
        List<User> filteredUsers = users
                .filter(user -> filters.stream()
                        .filter(filter -> filter.isApplicable(filterDto))
                        .allMatch(filter -> filter.apply(user)))
                .toList();
        return userMapper.toUserSubResponseList(filteredUsers);
    }

}
