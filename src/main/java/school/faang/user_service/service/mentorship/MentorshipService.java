package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        List<UserDto> result = validateId(userId).getMentees().stream()
                .map(userMapper::toDto).toList();
        log.info("Mentees for userId={} have taken successfully from DB", userId);
        return result;
    }

    private User validateId(long userId) {
        //тест3
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("UserId is not found"));
    }


}
