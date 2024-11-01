package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.RequestDescriptionFilter;
import school.faang.user_service.service.mentorship.filter.RequestFilter;
import school.faang.user_service.service.mentorship.filter.RequestReceiverFilter;
import school.faang.user_service.service.mentorship.filter.RequestRequesterFilter;
import school.faang.user_service.service.mentorship.filter.RequestStatusFilter;
import school.faang.user_service.validation.mentorship.MentorshipRequestDtoValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestDtoValidator requestValidator;

    @Spy
    private MentorshipRequestMapper requestMapper;

    @Captor
    ArgumentCaptor<MentorshipRequest> requestCaptor;

    @Captor
    ArgumentCaptor<List<MentorshipRequest>> requestListCaptor;

    private MentorshipRequestServiceImpl requestService;

    private MentorshipRequest request;
    private MentorshipRequestCreationDto creationDto;

    private MentorshipRequest firstRequest;
    private MentorshipRequest secondRequest;
    private MentorshipRequest thirdRequest;
    private MentorshipRequest fourthRequest;

    private RequestFilterDto complexFilter;
    private RequestFilterDto emptyFilter;

    private List<MentorshipRequest> requests;

    @BeforeEach
    public void setup() {
        RequestFilter firstFilter = Mockito.spy(RequestDescriptionFilter.class);
        RequestFilter secondFilter = Mockito.spy(RequestRequesterFilter.class);
        RequestFilter thirdFilter = Mockito.spy(RequestReceiverFilter.class);
        RequestFilter fourthFilter = Mockito.spy(RequestStatusFilter.class);
        List<RequestFilter> requestFilters = List.of(firstFilter, secondFilter, thirdFilter, fourthFilter);

        requestMapper = Mockito.spy(new MentorshipRequestMapperImpl());
        requestService = new MentorshipRequestServiceImpl(
                requestRepository, userRepository, requestValidator, requestMapper, requestFilters
        );

        Long requesterId = 1L;
        Long receiverId = 2L;
        String description = "some description";
        creationDto = MentorshipRequestCreationDto.builder()
                .description(description)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();

        request = requestMapper.toMentorshipRequest(creationDto);
        request.setStatus(RequestStatus.PENDING);

        complexFilter = new RequestFilterDto("Request", 1L, 2L, RequestStatus.PENDING);
        emptyFilter = new RequestFilterDto(null, null, null, null);

        setupRequestsList();
    }

    @Test
    public void requestMentorshipValidTest() {
        when(requestRepository.save(any(MentorshipRequest.class))).thenReturn(request);

        assertDoesNotThrow(() -> requestService.requestMentorship(creationDto));

        verify(requestValidator, times(1)).validateCreationRequest(creationDto);
        verify(requestRepository, times(1)).save(requestCaptor.capture());
        MentorshipRequest savedRequest = requestCaptor.getValue();
        assertEquals(creationDto.getDescription(), savedRequest.getDescription());
        assertEquals(creationDto.getRequesterId(), savedRequest.getRequester().getId());
        assertEquals(creationDto.getReceiverId(), savedRequest.getReceiver().getId());
    }

    @Test
    public void getRequestsEmptyFilter() {
        when(requestRepository.findAll()).thenReturn(requests);

        List<MentorshipRequestDto> filteredRequests = requestService.getRequests(emptyFilter);

        verify(requestRepository, times(1)).findAll();
        assertEquals(requests.size(), filteredRequests.size());
    }

    @Test
    public void getRequestsComplexFilter() {
        when(requestRepository.findAll()).thenReturn(requests);

        requestService.getRequests(complexFilter);

        verify(requestRepository, times(1)).findAll();
        verify(requestMapper, times(1)).toDtoList(requestListCaptor.capture());
        assertEquals(1, requestListCaptor.getValue().size());
        assertTrue(requestListCaptor.getValue().contains(firstRequest));
    }

    @Test
    public void getRequestsWithEmptyRequests() {
        when(requestRepository.findAll()).thenReturn(Collections.emptyList());

        List<MentorshipRequestDto> filteredRequests = requestService.getRequests(emptyFilter);

        verify(requestRepository, times(1)).findAll();
        assertEquals(0, filteredRequests.size());
    }

    @Test
    public void acceptRequestValidTest() {
        Long requestId = 1L;
        when(requestValidator.validateAcceptRequest(requestId)).thenReturn(firstRequest);
        when(requestRepository.save(any(MentorshipRequest.class))).thenReturn(firstRequest);

        MentorshipRequestDto result = requestService.acceptRequest(requestId);

        verify(requestValidator, times(1)).validateAcceptRequest(requestId);
        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
        assertEquals(firstRequest.getId(), result.getId());
    }

    @Test
    public void rejectRequestValidTest() {
        Long requestId = 3L;
        RejectionDto rejectionDto = new RejectionDto("Not enough experience");
        when(requestValidator.validateRejectRequest(requestId)).thenReturn(firstRequest);
        when(requestRepository.save(any(MentorshipRequest.class))).thenReturn(firstRequest);

        MentorshipRequestDto result = requestService.rejectRequest(requestId, rejectionDto);

        verify(requestValidator, times(1)).validateRejectRequest(requestId);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals("Not enough experience", result.getRejectionReason());
        assertEquals(firstRequest.getId(), result.getId());
    }

    private void setupRequestsList() {
        firstRequest =
                createRequest(1L, "Request 1", 1L, 2L, RequestStatus.PENDING, null);
        secondRequest =
                createRequest(2L, "Request 2", 3L, 4L, RequestStatus.ACCEPTED, null);
        thirdRequest =
                createRequest(3L, "Request 3", 5L, 6L, RequestStatus.REJECTED, "Not enough experience");
        fourthRequest =
                createRequest(4L, "Request 4", 7L, 8L, RequestStatus.PENDING, null);

        requests = List.of(firstRequest, secondRequest, thirdRequest, fourthRequest);
    }

    private MentorshipRequest createRequest(Long id, String description, Long requesterId, Long receiverId,
                                            RequestStatus status, String rejectionReason) {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(id);
        request.setDescription(description);
        request.setRequester(User.builder().id(requesterId).username("user" + requesterId).build());
        request.setReceiver(User.builder().id(receiverId).username("user" + receiverId).build());
        request.setStatus(status);
        request.setRejectionReason(rejectionReason);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return request;
    }
}