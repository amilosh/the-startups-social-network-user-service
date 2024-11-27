package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.utils.AvatarLibrary;
import school.faang.user_service.validator.user.UserValidator;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final UserValidator userValidator;
    private final S3Service s3Service;
    private final AvatarLibrary avatarLibrary;
    private final RestTemplate restTemplate;

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userValidator.validateUser(userId);

        goalRepository.findGoalsByUserId(userId).forEach(goal -> {
            goalRepository.removeUserFromGoal(userId, goal.getId());
            log.info("User with ID {} doesn't make anymore goal with ID {} ", userId, goal.getId());
            if (goalRepository.findUsersByGoalId(goal.getId()).isEmpty()) {
                goalRepository.deleteById(goal.getId());
                log.info("Goal with ID {} deleted from database ", goal.getId());
            }
        });

        eventRepository.findAllByUserId(userId).forEach(event -> {
            eventRepository.deleteById(event.getId());
            log.info("Event with ID {} deleted for user with ID {} ", event.getId(), userId);
        });

        mentorshipService.stopMentorship(user);

        user.setActive(false);
        userRepository.save(user);
        log.info("User with ID {} has been scheduled for the deactivation", userId);
    }

    public Stream<UserDto> getUser(UserFilterDto filterDto) {
        Stream<User> usersStream = userRepository.findAll().stream();
        for (UserFilter filter : userFilters) {
            if (filter != null && filter.isApplicable(filterDto)) {
                usersStream = filter.apply(usersStream, filterDto);
            }
        }

        return usersStream.map(userMapper::toDto);
    }

    public UserDto getUser(long userId) {
        User user = userValidator.validateUser(userId);
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toListDto(users);
    }

    public void addAvatar(long userId, MultipartFile file) {
        User user = userValidator.validateUser(userId);
        s3Service.uploadFile(file, user);
        userRepository.save(user);
    }

    public byte[] getAvatar(long userId) {
        User user = userValidator.validateUser(userId);
        UserProfilePic profile = user.getUserProfilePic();

        if (profile == null || profile.getFileId() == null) {
            return restTemplate.getForObject(avatarLibrary.getServiceUri(), byte[].class);
        } else {
            try {
                return s3Service.getFile(profile.getFileId()).readAllBytes();
            } catch (IOException e) {
                log.error("Failed to read all bytes from the transferred file");
                throw new RuntimeException(e);
            }
        }
    }
}