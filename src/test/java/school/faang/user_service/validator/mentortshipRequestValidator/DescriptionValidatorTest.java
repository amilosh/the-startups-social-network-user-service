package school.faang.user_service.validator.mentortshipRequestValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.ValidationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DescriptionValidatorTest {
    @InjectMocks
    private DescriptionValidator descriptionValidator;

    @Mock
    private UserService userService;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    ValidationContext validationContext;

    @BeforeEach
    public void setUp() {
        validationContext = new ValidationContext(mentorshipRequestService, userService);
    }

    @Test
    public void testDescriptionValidateFailed() {
        MentorshipRequestDto dto = prepareData(1L, 2L, "  ");
        assertThrows(IllegalArgumentException.class, () -> descriptionValidator.validate(dto, validationContext));
    }

    @Test
    public void testDescriptionValidateSuccessful() {
        MentorshipRequestDto dto = prepareData(1L, 2L, "description");
        descriptionValidator.validate(dto, validationContext);
    }


    private MentorshipRequestDto prepareData(long requesterId, long receiverId, String description) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterUserId(requesterId);
        dto.setReceiverUserId(receiverId);
        dto.setDescription(description);

        return dto;
    }
}
