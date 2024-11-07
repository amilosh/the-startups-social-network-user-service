package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(premiumUsers,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
