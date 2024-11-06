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
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship_request.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.request_filter.RequestFilter;
import school.faang.user_service.validator.mentorship_request.MentorshipRequestValidation;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private MentorshipRequestMapperImpl mapper;
    @Mock
    private MentorshipRequestValidation validator;

    @Captor
    private ArgumentCaptor<MentorshipRequest> requestCaptor;

    private final long CORRECT_ID_1 = 1L;
    private final long CORRECT_ID_2 = 2L;
    private User receiver;
    private User requester;
    private MentorshipRequestDto mentorshipRequestDto;
    private MentorshipRequest mentorshipRequest;
    private RejectionDto rejection;
    private List<RequestFilter> filters;

    @BeforeEach
    void initData() {
        RequestFilter requestFilter = Mockito.mock(RequestFilter.class);
        filters = List.of(requestFilter);
        mentorshipRequestService = new MentorshipRequestService(mentorshipRequestRepository
                , userRepository
                , mapper
                , filters
                , validator);
        receiver = User.builder()
                .id(CORRECT_ID_1)
                .username("Max")
                .email("max@mail.ru")
                .city("Amsterdam")
                .receivedMentorshipRequests(new ArrayList<>())
                .sentMentorshipRequests(new ArrayList<>())
                .build();
        requester = User.builder()
                .id(CORRECT_ID_2)
                .username("Denis")
                .email("denis@mail.ru")
                .city("New York")
                .mentors(new ArrayList<>())
                .receivedMentorshipRequests(new ArrayList<>())
                .sentMentorshipRequests(new ArrayList<>())
                .build();
        mentorshipRequestDto = MentorshipRequestDto.builder()
                .id(1L)
                .description("Запрос на менторство")
                .requesterId(CORRECT_ID_2)
                .receiverId(CORRECT_ID_1)
                .createdAt(LocalDateTime.of(2022, Month.APRIL, 2, 15, 20, 13))
                .build();
        mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .rejectionReason(null)
                .status(RequestStatus.PENDING)
                .build();
        rejection = RejectionDto.builder()
                .requesterId(1L)
                .reason("Не хватает скиллов")
                .build();
    }

    @Test
    public void testRequestMentorshipSavedSuccessfully() {
        doNothing().when(validator).validateSameId(CORRECT_ID_1, CORRECT_ID_2);

        when(validator.validateId(CORRECT_ID_1)).thenReturn(receiver);
        when(validator.validateId(CORRECT_ID_2)).thenReturn(requester);

        doNothing().when(validator).validate3MonthsFromTheLastRequest(requester,receiver);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        List<MentorshipRequest> realList = requester.getSentMentorshipRequests();
        verify(mentorshipRequestRepository).save(requestCaptor.capture());
        verify(userRepository).save(requester);
        mentorshipRequest = requestCaptor.getValue();
        List<MentorshipRequest> expectedList = new ArrayList<>(Collections.singletonList(mentorshipRequest));

        assertEquals(expectedList, realList);
        assertEquals(mentorshipRequest.getRequester(), requester);
        assertEquals(mentorshipRequest.getReceiver(), receiver);
    }

    @Test
    public void testGetRequestsSuccessfully() {
        List<MentorshipRequest> mentorshipRequests = Collections.singletonList(mentorshipRequest);
        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequests);
        when(filters.get(0).isApplicable(new RequestFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(List.of(mentorshipRequest));

        List<MentorshipRequestDto> realList = mentorshipRequestService.getRequests(new RequestFilterDto());

        verify(mentorshipRequestRepository).findAll();
        assertEquals(realList,mapper.toMentorshipRequestDtoList(Collections.singletonList(mentorshipRequest)));
    }

    @Test
    public void testAcceptRequestSuccessfully() {
        when(validator.validateRequestId(1L)).thenReturn(mentorshipRequest);
        doNothing().when(validator).validateOfBeingInMentorship(mentorshipRequest);
        doNothing().when(validator).validateStatus(mentorshipRequest);

        List<User> expectedList = Collections.singletonList(receiver);

        mentorshipRequestService.acceptRequest(1L);

        verify(mentorshipRequestRepository).save(mentorshipRequest);
        verify(userRepository).save(requester);

        assertEquals(expectedList, requester.getMentors());
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
    }

    @Test
    public void testRejectRequestSuccessfully() {
        when(validator.validateRequestId(1L)).thenReturn(mentorshipRequest);

        mentorshipRequestService.rejectRequest(1L, rejection);

        verify(mentorshipRequestRepository).save(mentorshipRequest);
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals(rejection.getReason(), mentorshipRequest.getRejectionReason());
    }
}
