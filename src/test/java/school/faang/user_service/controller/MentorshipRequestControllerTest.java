package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.mentorship.MentorshipRequestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.mentorship_request.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService service;

    @InjectMocks
    private MentorshipRequestController controller;

    private MentorshipRequestDto request;

    @Test
    public void testMentorshipRequestIsNull() {
        request = MentorshipRequestDto.builder()
                .description("  ")
                .build();
        assertThrows(DataValidationException.class,() -> controller.requestMentorship(request));
    }


}
