package school.faang.user_service.validator.mentortshipRequestValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.ValidationContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DateTimeValidatorTest {
    @InjectMocks
    private DateTimeValidation dateTimeValidation;

    @Mock
    private UserService userService;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequest mentorshipRequest;

    ValidationContext validationContext;

    MentorshipRequestDto dto;

    @BeforeEach
    public void setUp() {
        validationContext = new ValidationContext(mentorshipRequestService, userService);
        dto = prepareData(1L, 2L, "description");
    }

    @Test
    public void testDateTimeValidatorSuccessful() {
        prepareLatestRequestMock(LocalDateTime.now().minusMonths(4));

        dateTimeValidation.validate(dto, validationContext);
        verify(mentorshipRequest).getCreatedAt();
    }

    @Test
    public void testDateTimeValidatorFailed() {
        prepareLatestRequestMock(LocalDateTime.now().minusDays(10));

        assertThrows(IllegalArgumentException.class,
                () -> dateTimeValidation.validate(dto, validationContext));
    }

    private void prepareLatestRequestMock(LocalDateTime date) {
        MentorshipRequestDto dto = prepareData(1L, 2L, "description");

        when(validationContext.mentorshipRequestService()
                .findLatestRequest(
                        dto.getRequesterUserId(),
                        dto.getReceiverUserId()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequest.getCreatedAt()).thenReturn(date);
    }

    private MentorshipRequestDto prepareData(long requesterId, long receiverId, String description) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterUserId(requesterId);
        dto.setReceiverUserId(receiverId);
        dto.setDescription(description);

        return dto;
    }
}
