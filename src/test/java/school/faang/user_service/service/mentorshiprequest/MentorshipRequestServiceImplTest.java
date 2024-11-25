package school.faang.user_service.service.mentorshiprequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorshiprequest.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceImplTest {
    private static final Long REQUESTER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final Long REQUEST_ID = 1L;
    private static final String REQUEST_DESCRIPTION = "test description";
    private static final String REJECTION_REASON = "rejection reason description";
    private static final String REQUEST_DOESNT_EXIST_EXCEPTION = "request doesn't exist";

    private final List<MentorshipRequestFilter> mentorshipRequestFilters = new ArrayList<>();

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    private MentorshipRequestServiceImpl mentorshipRequestServiceImpl;

    @BeforeEach
    public void setUp() {
        mentorshipRequestServiceImpl = new MentorshipRequestServiceImpl(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                mentorshipRequestValidator,
                mentorshipRequestFilters
        );
    }

    @Test
    public void testValidRequestMentorship() {
        MentorshipRequestDto requestDto = createRequestDto(REQUESTER_ID, RECEIVER_ID);
        MentorshipRequest requestEntity = createRequestEntity(REQUEST_ID,
                createUser(REQUEST_ID), createUser(RECEIVER_ID));
        when(mentorshipRequestRepository.create(REQUESTER_ID, RECEIVER_ID, REQUEST_DESCRIPTION))
                .thenReturn(requestEntity);

        MentorshipRequestDto resultDto = mentorshipRequestServiceImpl.requestMentorship(requestDto);

        verify(mentorshipRequestRepository).create(REQUESTER_ID, RECEIVER_ID, REQUEST_DESCRIPTION);
        assertEquals(requestDto.getRequesterId(), resultDto.getRequesterId());
        assertEquals(requestDto.getReceiverId(), resultDto.getReceiverId());
        assertEquals(REQUEST_DESCRIPTION, resultDto.getDescription());
    }

    @ParameterizedTest()
    @MethodSource("provideInvalidIdsAndException")
    public void testUserValidationInRequest(Long requesterId, Long receiverId, String exceptionMessage) {
        MentorshipRequestDto requestDto = createRequestDto(requesterId, receiverId);
        doThrow(new DataValidationException(exceptionMessage))
                .when(mentorshipRequestValidator).validateRequesterAndReceiver(requesterId, receiverId);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestServiceImpl.requestMentorship(requestDto));

        verify(mentorshipRequestValidator).validateRequesterAndReceiver(requesterId, receiverId);
        assertEquals(exceptionMessage, thrown.getMessage());
    }

    private static Stream<Arguments> provideInvalidIdsAndException() {
        return Stream.of(
                Arguments.of(REQUESTER_ID, REQUESTER_ID, "requester and receiver must be different"),
                Arguments.of(null, RECEIVER_ID, "requester doesn't exist"),
                Arguments.of(REQUESTER_ID, null, "receiver doesn't exist")
        );
    }

    @Test
    public void testRequiredTimeNotPassed() {
        MentorshipRequestDto requestDto = createRequestDto(REQUESTER_ID, RECEIVER_ID);
        String exceptionMessage = "you can send a request to one receiver once every 3 months";
        doThrow(new DataValidationException(exceptionMessage))
                .when(mentorshipRequestValidator).validateRequestInterval(REQUESTER_ID, RECEIVER_ID);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestServiceImpl.requestMentorship(requestDto));

        verify(mentorshipRequestValidator).validateRequestInterval(REQUESTER_ID, RECEIVER_ID);
        assertEquals(exceptionMessage, thrown.getMessage());
    }

    @Test
    public void testGetRequestsWithFilters() {
        RequestFilterDto filterDto = RequestFilterDto.builder().build();
        MentorshipRequestFilter filterMock = mock(MentorshipRequestFilter.class);
        mentorshipRequestFilters.add(filterMock);
        List<MentorshipRequest> requests = List.of(new MentorshipRequest());
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        when(filterMock.isApplicable(filterDto)).thenReturn(true);
        when(filterMock.apply(any(), eq(filterDto))).thenReturn(requests.stream());

        mentorshipRequestServiceImpl.getRequests(filterDto);

        verify(filterMock).isApplicable(filterDto);
        verify(filterMock).apply(any(), eq(filterDto));
    }

    @Test
    public void testAcceptRequestWithValidId() {
        MentorshipRequest requestEntity = createRequestEntity(REQUEST_ID,
                createUser(REQUESTER_ID), createUser(RECEIVER_ID));
        when(mentorshipRequestValidator.getRequestByIdOrThrowException(REQUEST_ID)).thenReturn(requestEntity);

        MentorshipRequestDto resultDto = mentorshipRequestServiceImpl.acceptRequest(REQUEST_ID);

        verify(mentorshipRequestRepository).save(requestEntity);
        assertEquals(REQUEST_ID, resultDto.getId());
        assertEquals(RequestStatus.ACCEPTED, resultDto.getStatus());
    }

    @Test
    public void testRejectRequestWithValidId() {
        RejectionDto rejectionDto = RejectionDto.builder().reason(REJECTION_REASON).build();
        MentorshipRequest requestEntity = createRequestEntity(REQUEST_ID,
                createUser(REQUESTER_ID), createUser(RECEIVER_ID));
        when(mentorshipRequestValidator.getRequestByIdOrThrowException(REQUEST_ID)).thenReturn(requestEntity);

        MentorshipRequestDto resultDto = mentorshipRequestServiceImpl.rejectRequest(REQUEST_ID, rejectionDto);

        verify(mentorshipRequestRepository).save(requestEntity);
        assertEquals(REQUEST_ID, resultDto.getId());
        assertEquals(REJECTION_REASON, resultDto.getRejectionReason());
        assertEquals(RequestStatus.REJECTED, resultDto.getStatus());
    }

    @Test
    public void testAcceptRequestWithInvalidId() {
        doThrow(new DataValidationException(REQUEST_DOESNT_EXIST_EXCEPTION))
                .when(mentorshipRequestValidator).getRequestByIdOrThrowException(REQUEST_ID);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestServiceImpl.acceptRequest(REQUEST_ID));

        verify(mentorshipRequestValidator).getRequestByIdOrThrowException(REQUEST_ID);
        assertEquals(REQUEST_DOESNT_EXIST_EXCEPTION, thrown.getMessage());
    }

    @Test
    public void testRejectRequestWithInvalidId() {
        RejectionDto rejectionDto = RejectionDto.builder().reason(REJECTION_REASON).build();
        doThrow(new DataValidationException(REQUEST_DOESNT_EXIST_EXCEPTION))
                .when(mentorshipRequestValidator).getRequestByIdOrThrowException(REQUEST_ID);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestServiceImpl.rejectRequest(REQUEST_ID, rejectionDto));

        verify(mentorshipRequestValidator).getRequestByIdOrThrowException(REQUEST_ID);
        assertEquals(REQUEST_DOESNT_EXIST_EXCEPTION, thrown.getMessage());
    }

    @Test
    public void testAcceptAlreadyAcceptedRequest() {
        String requestAlreadyAcceptedException = "the request has already been accepted";
        MentorshipRequest requestEntity = createRequestEntity(REQUEST_ID,
                createUser(REQUESTER_ID), createUser(RECEIVER_ID));
        when(mentorshipRequestValidator.getRequestByIdOrThrowException(REQUEST_ID)).thenReturn(requestEntity);
        doThrow(new DataValidationException(requestAlreadyAcceptedException))
                .when(mentorshipRequestValidator).validateRequestStatus(requestEntity, RequestStatus.ACCEPTED);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestServiceImpl.acceptRequest(REQUEST_ID));

        verify(mentorshipRequestValidator).validateRequestStatus(requestEntity, RequestStatus.ACCEPTED);
        assertEquals(requestAlreadyAcceptedException, thrown.getMessage());
    }

    @Test
    public void testRejectAlreadyRejectedRequest() {
        String requestAlreadyRejectedException = "the request has already been rejected";
        RejectionDto rejectionDto = RejectionDto.builder().reason(REJECTION_REASON).build();
        MentorshipRequest requestEntity = createRequestEntity(REQUEST_ID,
                createUser(REQUESTER_ID), createUser(RECEIVER_ID));
        when(mentorshipRequestValidator.getRequestByIdOrThrowException(REQUEST_ID))
                .thenReturn(requestEntity);
        doThrow(new DataValidationException(requestAlreadyRejectedException))
                .when(mentorshipRequestValidator).validateRequestStatus(requestEntity, RequestStatus.REJECTED);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestServiceImpl.rejectRequest(REQUEST_ID, rejectionDto));

        verify(mentorshipRequestValidator).validateRequestStatus(requestEntity, RequestStatus.REJECTED);
        assertEquals(requestAlreadyRejectedException, thrown.getMessage());
    }

    private MentorshipRequestDto createRequestDto(Long requesterId, Long receiverId) {
        return MentorshipRequestDto.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .description(REQUEST_DESCRIPTION)
                .build();
    }

    private MentorshipRequest createRequestEntity(Long requestId, User requester, User receiver) {
        return MentorshipRequest.builder()
                .id(requestId)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .description(REQUEST_DESCRIPTION)
                .build();
    }

    private User createUser(Long userId) {
        return User.builder()
                .id(userId)
                .mentors(new ArrayList<>())
                .build();
    }
}