package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipRequestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.mentorship_request.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService service;
    @InjectMocks
    private MentorshipRequestController controller;

    private MentorshipRequestDto requestWithEmptyDescription;
    private MentorshipRequestDto requestWithNullDescription;

    @BeforeEach
    public void initData() {
        requestWithEmptyDescription = MentorshipRequestDto.builder()
                .description("   ")
                .build();
        requestWithNullDescription = MentorshipRequestDto.builder()
                .description(null)
                .build();
    }

    @Test
    public void testMentorshipRequestNullDescription() {
        assertThrows(DataValidationException.class,() -> controller.requestMentorship(requestWithNullDescription));
    }

    @Test
    public void testMentorshipRequestEmptyDescription() {
        assertThrows(DataValidationException.class,() -> controller.requestMentorship(requestWithEmptyDescription));
    }
}
