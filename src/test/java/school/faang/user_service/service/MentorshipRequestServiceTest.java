package school.faang.user_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestValidator requestValidator;
    @Mock
    private MentorshipRequestRepository requestRepository;

    @InjectMocks
    private MentorshipRequestService requestService;

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
    void testServiceRequestMentorshipShouldCreateRequest() {
        when(requestValidator.validateMentorshipRequest(dto)).thenReturn(true);
        requestService.requestMentorship(dto);

        verify(requestRepository).create(dto.getRequesterId(), dto.getReceiverId(), dto.getDescription());
    }
}