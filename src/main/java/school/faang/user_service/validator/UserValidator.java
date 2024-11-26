package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.SkillDuplicateException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository repository;

    public void validateUserById(long userId) {
        if (!repository.existsById(userId)) {
            throw new EntityNotFoundException("User with id #" + userId + " not found");
        }
        log.info("User '{}' exists.", userId);
    }

    public boolean isUserMentor(User user) {
        return !user.getMentees().isEmpty();
    }

    public void validateSkillMissing(User user, Skill skill) {
        if (user.getSkills().contains(skill)) {
            throw new SkillDuplicateException("User " + user.getUsername() + " already possesses the skill " + skill.getTitle());
        }
    }
}
