package school.faang.user_service.validator.mentortshipRequestValidator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.ValidationContext;

@Component
@RequiredArgsConstructor
public class SameUserValidator implements MentorshipRequestValidator {

    @Override
    public void validate(MentorshipRequestDto mentorshipRequestDto, ValidationContext validationContext) {
        if (mentorshipRequestDto.getRequesterUserId().equals(mentorshipRequestDto.getReceiverUserId())) {
            throw new IllegalArgumentException("requester user is the same as receiver user");
        }
    }
}
