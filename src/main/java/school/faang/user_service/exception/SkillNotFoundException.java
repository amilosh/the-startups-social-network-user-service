package school.faang.user_service.exception;

public class SkillNotFoundException extends RuntimeException {
    public SkillNotFoundException(Long skillId) {
        super("Skill with ID " + skillId + " not found.");
    }
}
