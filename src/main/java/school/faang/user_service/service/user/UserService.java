package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public void deactivateUser(long userId) {

//        stop the activities
//        stop and delete events
//        delete goals if nobody do this goal
//
        User user = userValidator.isUserExistsThenReturn(userId);
        user.setActive(false);

//        after deactivation stop mentorship in mentee - add method in MentorshipService
//        but the goals mentee which give user should be existed but looks like mentee did it by self


    }
}
