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
import school.faang.user_service.filter.MentorshipRequestFilter.DescriptionFilter;
import school.faang.user_service.filter.MentorshipRequestFilter.RequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private MentorshipRequestService requestService;
    private MentorshipRequestDto requestDto;
    private AutoCloseable mocks;
    private RequestFilterDto filterDto;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        requestDto = TestDataCreator.createMentorshipRequestDto(1L, 1L, 2L, RequestStatus.ACCEPTED,
                "Need help with Java!");

        filterDto = TestDataCreator.createRequestFilterDto(1L, 2L, "HELP", RequestStatus.ACCEPTED);
        List<RequestFilter> requestFilters = List.of(descriptionFilter);
        requestService = new MentorshipRequestService(requestRepository, requestValidator, requestMapper,
                requestFilters);
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
        User user1 = TestDataCreator.createUser(1L);
        User user2 = TestDataCreator.createUser(2L);
        MentorshipRequest request1 = TestDataCreator.createMentorshipRequest(1L, user1, user2, RequestStatus.ACCEPTED,
                "Need Help With Java");
        MentorshipRequest request2 = TestDataCreator.createMentorshipRequest(2L, user2, user1, RequestStatus.REJECTED,
                "Need assistance");

        when(requestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(descriptionFilter.isApplicable(filterDto)).thenReturn(true);
        when(descriptionFilter.apply(any(), any())).thenAnswer(invocationOnMock -> Stream.of(request1));
        when(requestMapper.toDto(request1)).thenReturn(requestDto);

        List<MentorshipRequestDto> requestDtoList = requestService.getRequests(filterDto);

        assertEquals(1, requestDtoList.size());
        verify(descriptionFilter, times(1)).isApplicable(any(RequestFilterDto.class));
        verify(descriptionFilter, times(1)).apply(any(Stream.class), any(RequestFilterDto.class));
        verify(requestMapper, times(1)).toDto(any(MentorshipRequest.class));
        verify(requestRepository, times(1)).findAll();
    }
}
