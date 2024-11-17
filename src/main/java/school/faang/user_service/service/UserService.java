package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;
import java.util.stream.Stream;

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

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return filterUsers(premiumRepository.findPremiumUsers(), userFilterDto);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filterDto) {
        List<User> filtered_users = users
                .filter(user -> filters.stream()
                        .filter(filter -> filter.isApplicable(filterDto))
                        .allMatch(filter -> filter.apply(user)))
                .toList();
        return userMapper.toDto(filtered_users);
    }
}
