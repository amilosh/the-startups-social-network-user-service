package school.faang.user_service.exception;

public class SkillNotFoundException extends RuntimeException {
    public SkillNotFoundException(Long skillId) {
        super("Skill with Id" + skillId + " not found.");
    }
}
