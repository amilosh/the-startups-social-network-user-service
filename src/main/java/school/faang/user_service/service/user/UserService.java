package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.userJira.UserJiraCreateUpdateDto;
import school.faang.user_service.dto.userJira.UserJiraDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.userJira.UserJira;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.mapper.userJira.UserJiraMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.userJira.UserJiraService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final UserJiraMapper userJiraMapper;
    private final UserJiraService userJiraService;


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

    @Transactional(readOnly = true)
    public List<Long> getNotExistingUserIds(List<Long> userIds) {
        return userIds.isEmpty() ? Collections.emptyList() : userRepository.findNotExistingUserIds(userIds);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getNotPremiumUsers(UserFilterDto filterDto) {
        Stream<User> usersToFilter = userRepository.findAll().stream();
        Stream<User> notPremiumUsers = filterPremiumUsers(usersToFilter);

        List<UserDto> filteredUsers = filter(notPremiumUsers, filterDto);
        log.info("Got {} filtered users, by filter {}", filteredUsers.size(), filterDto);
        return filteredUsers;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> users = userRepository.findPremiumUsers();

        List<UserDto> filteredUsers = filter(users, filterDto);
        log.info("Got {} filtered premium users, by filter {}", filteredUsers.size(), filterDto);
        return filteredUsers;
    }

    private List<UserDto> filter(Stream<User> usersStream, UserFilterDto filterDto) {
        return userMapper.entityStreamToDtoList(userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(usersStream,
                        (users, userFilter) -> userFilter.apply(users, filterDto),
                        (a, b) -> b));
    }

    private Stream<User> filterPremiumUsers(Stream<User> users) {
        return users.filter(user -> user.getPremium() == null
                || user.getPremium().getEndDate() == null
                || user.getPremium().getEndDate().isBefore(LocalDateTime.now()));
    }

    @Transactional
    public UserJiraDto saveOrUpdateUserJiraInfo(long userId, String jiraDomain, UserJiraCreateUpdateDto createUpdateDto) {
        log.info("Request received to save or update user (ID {}) Jira account information for Jira domain {}", userId, jiraDomain);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND, userId)));
        UserJira userJira = userJiraMapper.toEntity(createUpdateDto);
        userJira.setUser(user);
        userJira.setJiraDomain(jiraDomain);
        UserJira savedUserJira = userJiraService.saveOrUpdate(userJira);

        log.info("Request to save or update user (ID {}) Jira account information for Jira domain {} processed successfully",
                userId, jiraDomain
        );
        return userJiraMapper.toDto(savedUserJira);
    }

    @Transactional
    public UserJiraDto getUserJiraInfo(long userId, String jiraDomain) {
        log.info("Received request to get user (ID {}) Jira account information for Jira domain {}", userId, jiraDomain);
        UserJira userJira = userJiraService.getByUserIdAndJiraDomain(userId, jiraDomain);
        log.info("Found user (ID {}) Jira account information for Jira domain {}", userId, jiraDomain);
        return userJiraMapper.toDto(userJira);
    }
}
