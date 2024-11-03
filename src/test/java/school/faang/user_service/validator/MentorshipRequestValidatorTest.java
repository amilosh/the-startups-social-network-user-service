package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private UserService userService;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private Errors errors;

    private MentorshipRequestDto dto;

    @BeforeEach
    public void setUp() {
        dto = new MentorshipRequestDto();
        dto.setRequesterUserId(1L);
        dto.setReceiverUserId(2L);
    }

    @Test
    public void testSameUserValidator() {
        dto.setReceiverUserId(1L);
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validate(dto, errors));
    }

    @Test
    public void testRequesterUserExists() {
        when(userService.existsById(dto.getRequesterUserId())).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validate(dto, errors));
    }

    @Test
    public void testReceiverUserExists() {
        when(userService.existsById(dto.getRequesterUserId())).thenReturn(true);
        when(userService.existsById(dto.getReceiverUserId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validate(dto, errors));
    }

    @Test
    public void testCreateAtRequestIsAfterThreeMonths() {
        MentorshipRequest request = prepareData(2);
        setupMocksForValidation(request);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validate(dto, errors));
    }

    @Test
    public void testValidateSuccessful() {
        MentorshipRequest request = prepareData(4);
        setupMocksForValidation(request);

        mentorshipRequestValidator.validate(dto, errors);
    }

    private void setupMocksForValidation(MentorshipRequest request) {
        when(userService.existsById(dto.getRequesterUserId())).thenReturn(true);
        when(userService.existsById(dto.getReceiverUserId())).thenReturn(true);

        when(mentorshipRequestService.findLatestRequest(dto.getRequesterUserId(), dto.getReceiverUserId()))
                .thenReturn(request);
    }

    private MentorshipRequest prepareData(int countMonth) {
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now().minusMonths(countMonth));

        return request;
    }
}
