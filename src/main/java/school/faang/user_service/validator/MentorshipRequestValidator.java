package school.faang.user_service.validator;

import school.faang.user_service.dto.MentorshipRequestDto;

public interface MentorshipRequestValidator {
    void validate(MentorshipRequestDto mentorshipRequestDto, ValidationContext validationContext);
}
