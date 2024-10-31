package school.faang.user_service.validator.mentortshipRequestValidator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.ValidationContext;

@Component
@RequiredArgsConstructor
public class UsersExistsValidator implements MentorshipRequestValidator {

    @Override
    public void validate(MentorshipRequestDto mentorshipRequestDto, ValidationContext validationContext) {
        UserService userService = validationContext.userService();

        if(!userService.existsById(mentorshipRequestDto.getRequesterUserId())) {
            throw new IllegalArgumentException("requester user not found");
        }

        if(!userService.existsById(mentorshipRequestDto.getReceiverUserId())) {
            throw new IllegalArgumentException("receiver user not found");
        }
    }
}
