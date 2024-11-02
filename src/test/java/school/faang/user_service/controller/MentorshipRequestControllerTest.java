package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.TestDataCreator;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.RequestFilterValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService requestService;
    @Mock
    private MentorshipRequestValidator requestValidator;
    @Mock
    private RequestFilterValidator filterValidator;

    @InjectMocks
    private MentorshipRequestController requestController;

    private MentorshipRequestDto requestDto;
    private RequestFilterDto filterDto;
    private RejectionDto rejectionDto;

    @BeforeEach
    void setUp() {
        requestDto = TestDataCreator.createMentorshipRequestDto(1L, 1L, 2L,
                RequestStatus.PENDING, "Need help with Java!");
        filterDto = TestDataCreator.createRequestFilterDto(1L, 2L, "HELP", RequestStatus.ACCEPTED);
        rejectionDto = TestDataCreator.createRejectionDto("Reason");
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

    @Test
    void testControllerRejectRequest() {
        requestController.rejectRequest(requestDto.getId(), rejectionDto);

        verify(requestService, times(1)).rejectRequest(requestDto.getId(), rejectionDto);
    }
}