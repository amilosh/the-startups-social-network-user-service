package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    @Mock
    private SkillRepository skillRepository;
    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

    private RecommendationRequestDto createRecommendationRequestDto() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Please recommend me");
        dto.setSkills(List.of(1L, 2L));
        return dto;
    }

    private RecommendationRequest createRecommendationRequest() {
        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);
        request.setRequester(User.builder().id(1L).build());
        request.setReceiver(User.builder().id(2L).build());
        request.setSkills(List.of(
                        new SkillRequest(1L, request, Skill.builder().id(1L).build()),
                        new SkillRequest(2L, request, Skill.builder().id(2L).build())
                )
        );
        return request;
    }

    @Test
    void shouldCreateRecommendationRequestSuccessfully() {
        RecommendationRequestDto dto = createRecommendationRequestDto();
        RecommendationRequest requestEntity = new RecommendationRequest();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.countExisting(dto.getSkills())).thenReturn(2);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest req = invocation.getArgument(0);
                    req.setId(1L);
                    return req;
                });
        when(skillRepository.getReferenceById(1L)).thenReturn(Skill.builder().id(1L).build());
        when(skillRepository.getReferenceById(2L)).thenReturn(Skill.builder().id(2L).build());

        RecommendationRequestDto createdDto = recommendationRequestService.create(dto);

        assertNotNull(createdDto);
        assertEquals(1L, createdDto.getId());
        assertEquals(RequestStatus.PENDING.toString(), createdDto.getStatus());
        assertEquals(1L, createdDto.getRequesterId());
        assertEquals(2L, createdDto.getReceiverId());
        assertEquals(2, createdDto.getSkills().size());
        assertEquals(1L, createdDto.getSkills().get(0));
        assertEquals(2L, createdDto.getSkills().get(1));

        verify(skillRepository, times(1)).countExisting(dto.getSkills());
        verify(recommendationRequestRepository, times(2)).save(recommendationRequestCaptor.capture());

        List<RecommendationRequest> capturedRequests = recommendationRequestCaptor.getAllValues();
        RecommendationRequest savedEntity = capturedRequests.get(1);

        assertEquals(1L, savedEntity.getRequester().getId());
        assertEquals(2L, savedEntity.getReceiver().getId());
        assertEquals(RequestStatus.PENDING, savedEntity.getStatus());
        assertEquals(2, savedEntity.getSkills().size());
        assertEquals(1L, savedEntity.getSkills().get(0).getSkill().getId());
        assertEquals(2L, savedEntity.getSkills().get(1).getSkill().getId());
    }

    @Test
    void shouldThrowExceptionWhenRequesterNotFound() {
        RecommendationRequestDto dto = createRecommendationRequestDto();

        when(userRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("Requester not found", exception.getMessage());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void shouldThrowExceptionWhenReceiverNotFound() {
        RecommendationRequestDto dto = createRecommendationRequestDto();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("Receiver not found", exception.getMessage());
        verify(userRepository, times(1)).existsById(2L);
    }

    @Test
    void shouldThrowExceptionWhenPendingRequestFoundWithinSixMonths() {
        RecommendationRequestDto dto = createRecommendationRequestDto();
        RecommendationRequest existingRequest = new RecommendationRequest();
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(2));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.of(existingRequest));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("A recommendation request can only be sent once every 6 months.", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findLatestPendingRequest(1L, 2L);
    }

    @Test
    void shouldThrowExceptionWhenSkillsDoNotExist() {
        RecommendationRequestDto dto = createRecommendationRequestDto();
        dto.setSkills(List.of(1L, 2L, 3L));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.countExisting(dto.getSkills())).thenReturn(2);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("One or more skills do not exist.", exception.getMessage());
        verify(skillRepository, times(1)).countExisting(dto.getSkills());
    }

    //***TEST FILTERS **************************************************************************************************
    @Test
    void shouldReturnFilteredRequests() {
        RecommendationRequestFilter filterMock = mock(RecommendationRequestFilter.class);
        List<RecommendationRequestFilter> filters = List.of(filterMock);

        recommendationRequestService = new RecommendationRequestService(
                recommendationRequestRepository,
                userRepository,
                recommendationRequestMapper,
                skillRepository,
                filters
        );
        RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto();
        RecommendationRequest request = createRecommendationRequest();
        List<RecommendationRequest> requests = List.of(request);
        RecommendationRequestDto dto = createRecommendationRequestDto();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);
        when(filters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(filters.get(0).apply(any(), eq(filterDto))).thenReturn(requests.stream());
        when(recommendationRequestMapper.toDto(request)).thenReturn(dto);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filterDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void shouldReturnEmptyListWhenNoRequestsMatchFilter() {
        RecommendationRequestFilter filterMock = mock(RecommendationRequestFilter.class);
        List<RecommendationRequestFilter> filters = List.of(filterMock);
        recommendationRequestService = new RecommendationRequestService(
                recommendationRequestRepository,
                userRepository,
                recommendationRequestMapper,
                skillRepository,
                filters
        );
        RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto();
        List<RecommendationRequest> requests = Collections.emptyList();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);
        when(filters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(filters.get(0).apply(any(), eq(filterDto))).thenReturn(requests.stream());

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filterDto);

        assertTrue(result.isEmpty());
    }

    //***TEST GET by id ************************************************************************************************
    @Test
    void shouldReturnRecommendationRequestSuccessfully() {
        RecommendationRequest request = createRecommendationRequest();

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        RecommendationRequestDto result = recommendationRequestService.getRequest(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenRecommendationRequestNotFound() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.getRequest(1L);
        });

        assertEquals("Recommendation request not found", exception.getMessage());
    }

    //***TEST REJECT ***************************************************************************************************
    @Test
    void shouldRejectRecommendationRequestSuccessfully() {
        RecommendationRequest request = createRecommendationRequest();
        request.setStatus(RequestStatus.PENDING);
        RejectionDto rejectionDto = new RejectionDto("Not eligible");

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        RecommendationRequestDto result = recommendationRequestService.rejectRequest(1L, rejectionDto);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED.toString(), result.getStatus());
        assertEquals("Not eligible", request.getRejectionReason());

        verify(recommendationRequestRepository, times(1)).findById(1L);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequestCaptor.capture());
        RecommendationRequest savedRequest = recommendationRequestCaptor.getValue();
        assertEquals(RequestStatus.REJECTED, savedRequest.getStatus());
        assertEquals("Not eligible", savedRequest.getRejectionReason());
    }

    @Test
    void shouldThrowExceptionWhenRecommendationRequestNotFoundForRejection() {
        RejectionDto rejectionDto = new RejectionDto("Invalid");

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.rejectRequest(1L, rejectionDto);
        });

        assertEquals("Recommendation request not found", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenRecommendationRequestIsNotPendingForRejection() {
        RecommendationRequest request = createRecommendationRequest();
        request.setStatus(RequestStatus.ACCEPTED);
        RejectionDto rejectionDto = new RejectionDto("Already processed");

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.rejectRequest(1L, rejectionDto);
        });

        assertEquals("Recommendation request is not pending", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(1L);
    }
}