package school.faang.user_service.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import static org.mockito.Mockito.verify;

class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService requestService;
    @Mock
    private MentorshipRequestValidator validator;

    @InjectMocks
    private MentorshipRequestController requestController;

    private MentorshipRequestDto dto;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setDescription("Need help with Java!");
    }

    @AfterEach
    void closeMocks() {
        try {
            mocks.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testControllerRequestMentorshipShouldCreateRequest() {
        requestController.requestMentorship(dto);

        verify(requestService).requestMentorship(dto);
    }
}