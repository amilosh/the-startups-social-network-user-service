package school.faang.user_service.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.TestDataCreator;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.RequestFilterValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService requestService;
    @Mock
    private MentorshipRequestValidator requestValidator;
    @Mock
    private RequestFilterValidator filterValidator;

    @InjectMocks
    private MentorshipRequestController requestController;

    private AutoCloseable mocks;
    private MentorshipRequestDto requestDto;
    private RequestFilterDto filterDto;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        requestDto = TestDataCreator.createMentorshipRequestDto(1L, 1L, 2L,
                RequestStatus.PENDING, "Need help with Java!");
        filterDto = TestDataCreator.createRequestFilterDto(1L, 2L, "HELP", RequestStatus.ACCEPTED);
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
    void testControllerCreateRequest() {
        requestController.requestMentorship(requestDto);

        verify(requestService, times(1)).requestMentorship(any(MentorshipRequestDto.class));
    }

    @Test
    void testControllerGettingRequests() {
        requestController.getRequests(filterDto);

        verify(requestService, times(1)).getRequests(any(RequestFilterDto.class));
    }

    @Test
    void testControllerAcceptRequest() {
        requestController.acceptRequest(requestDto.getId());

        verify(requestService, times(1)).acceptRequest(requestDto.getId());
    }
}