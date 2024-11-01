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
public class SameUserValidatorTest {
    @InjectMocks
    private SameUserValidator sameUserValidator;

    @Mock
    private UserService userService;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    private ValidationContext validationContext;

    @BeforeEach
    public void setUp() {
        validationContext = new ValidationContext(mentorshipRequestService, userService);
    }

    @Test
    public void testSameUserValidatorFailed() {
        MentorshipRequestDto dto = prepareData(1L, 1L, "description");
        assertThrows(IllegalArgumentException.class, () -> sameUserValidator.validate(dto, validationContext));
    }

    @Test
    public void testSameUserValidatorSuccessful() {
        MentorshipRequestDto dto = prepareData(1L, 2L, "description");
        sameUserValidator.validate(dto, validationContext);
    }

    private MentorshipRequestDto prepareData(long requestUserId, long receiverUserId, String description) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterUserId(requestUserId);
        dto.setReceiverUserId(receiverUserId);
        dto.setDescription(description);

        return dto;
    }
}
