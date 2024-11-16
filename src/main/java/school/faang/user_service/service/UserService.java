package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.request.UsersDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final EventService eventService;
    private final UserMapper userMapper;

    public boolean checkUserExistence(long userId) {
        return userRepository.existsById(userId);
    }

    public User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public List<UserDto> getUsersByIds(UsersDto usersDto) {
        return userRepository.findAllById(usersDto.getIds()).stream().map(userMapper::toDto).toList();
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found by id: %s", id)));
    }

    public UserDto deactivateProfile(long userId) {
        User user = findUser(userId);
        removeGoals(user);
        cancelUserOwnedEvents(userId);
        user.getOwnedEvents().clear();
        user.setActive(false);
        if (isUserMentor(user)) {
            user.getMentees().forEach(mentee -> {
                mentorshipService.moveGoalsToMentee(mentee.getId(), userId);
                mentorshipService.deleteMentor(mentee.getId(), userId);
            });
        }
        saveUser(user);
        return userMapper.toDto(user);
    }

    public void cancelUserOwnedEvents(long userId) {
        eventService.getEvents(userId).forEach(event -> {
            if (event.getStatus() == EventStatus.PLANNED || event.getStatus() == EventStatus.IN_PROGRESS) {
                event.setStatus(EventStatus.CANCELED);
            }
        });
    }

    private void removeGoals(User user) {
        user.getSetGoals().removeIf(goal -> goal.getUsers().isEmpty());
    }

    private boolean isUserMentor(User user) {
        return !user.getMentees().isEmpty();
    }
}