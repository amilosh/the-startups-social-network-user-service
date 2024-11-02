package school.faang.user_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.TestDataCreator;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.MentorshipRequestFilter.DescriptionFilter;
import school.faang.user_service.filter.MentorshipRequestFilter.RequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestValidator requestValidator;
    @Mock
    private MentorshipRequestRepository requestRepository;
    @Mock
    private MentorshipRequestMapper requestMapper;
    @Mock
    private DescriptionFilter descriptionFilter;
    @Mock
    private User firstUser;
    @Mock
    private MentorshipRequest firstRequest;
    @Mock
    private MentorshipRequest secondRequest;

    private MentorshipRequestService requestService;
    private MentorshipRequestDto requestDto;
    private AutoCloseable mocks;
    private RequestFilterDto filterDto;
    private Long firstRequestId;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        requestDto = TestDataCreator.createMentorshipRequestDto(1L, 1L, 2L, RequestStatus.ACCEPTED,
                "Need help with Java!");

        filterDto = TestDataCreator.createRequestFilterDto(1L, 2L, "HELP", RequestStatus.ACCEPTED);
        List<RequestFilter> requestFilters = List.of(descriptionFilter);
        requestService = new MentorshipRequestService(requestRepository, requestValidator, requestMapper,
                requestFilters);
        firstRequestId = firstRequest.getId();
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
        requestValidator.validateMentorshipRequest(requestDto);
        requestService.requestMentorship(requestDto);

        verify(requestRepository).create(requestDto.getRequesterId(),
                requestDto.getReceiverId(), requestDto.getDescription());
    }

    @Test
    void testGetRequestsShouldReturnEmptyList() {
        assertTrue(requestService.getRequests(filterDto).isEmpty());
    }

    @Test
    void testGetRequestsShouldReturnFilteredList() {
        when(requestRepository.findAll()).thenReturn(List.of(firstRequest, secondRequest));
        when(descriptionFilter.isApplicable(filterDto)).thenReturn(true);
        when(descriptionFilter.apply(any(), any())).thenAnswer(invocationOnMock -> Stream.of(firstRequest));
        when(requestMapper.toDto(firstRequest)).thenReturn(requestDto);

        List<MentorshipRequestDto> requestDtoList = requestService.getRequests(filterDto);

        assertEquals(1, requestDtoList.size());
        verify(descriptionFilter, times(1)).isApplicable(any(RequestFilterDto.class));
        verify(descriptionFilter, times(1)).apply(any(Stream.class), any(RequestFilterDto.class));
        verify(requestMapper, times(1)).toDto(any(MentorshipRequest.class));
        verify(requestRepository, times(1)).findAll();
    }

    @Test
    void testAcceptRequestGotNullRequestById() {
        when(requestRepository.findById(firstRequestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.acceptRequest(firstRequestId));
        verify(requestValidator, times(1)).validateMentorshipRequestExists(firstRequestId);
        verify(requestRepository, times(1)).findById(firstRequestId);
        verify(firstRequest, times(0)).getRequester();
        verify(firstRequest, times(0)).getReceiver();
        verify(requestValidator, times(0)).validateRequesterHasReceiverAsMentor(any(), any());
        verify(firstUser, times(0)).getMentors();
        verify(firstRequest, times(0)).setStatus(RequestStatus.ACCEPTED);
    }

    @Test
    void testAcceptRequestSuccessful() {
        List<User> mentors = new ArrayList<>();

        when(requestRepository.findById(firstRequestId)).thenReturn(Optional.of(firstRequest));
        when(firstRequest.getRequester()).thenReturn(firstUser);
        when(firstUser.getMentors()).thenReturn(mentors);

        requestService.acceptRequest(firstRequestId);

        verify(requestValidator, times(1)).validateMentorshipRequestExists(firstRequestId);
        verify(requestRepository, times(1)).findById(firstRequestId);
        verify(firstRequest, times(1)).getRequester();
        verify(firstRequest, times(1)).getReceiver();
        verify(requestValidator, times(1)).validateRequesterHasReceiverAsMentor(any(), any());
        verify(firstUser, times(1)).getMentors();
        verify(firstRequest, times(1)).setStatus(RequestStatus.ACCEPTED);
        assertEquals(1, mentors.size());
    }
}
