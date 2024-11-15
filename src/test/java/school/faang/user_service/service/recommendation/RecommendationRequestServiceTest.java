package school.faang.user_service.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.recommendation_request.CreatedAfterFilterRecommendation;
import school.faang.user_service.filters.recommendation_request.CreatedBeforeFilterRecommendation;
import school.faang.user_service.filters.recommendation_request.RecommendationRequestFilter;
import school.faang.user_service.filters.recommendation_request.StatusFilterRecommendation;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestServiceValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.longThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestServiceValidator validator;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationRequestMapperImpl mapper;
    @Captor
    ArgumentCaptor<RecommendationRequest> captor;
    @InjectMocks
    RecommendationRequestService recommendationRequestService;

    @Spy
    private ArrayList<RecommendationRequestFilter> recommendationRequestFilters;
    @Mock
    CreatedAfterFilterRecommendation createdAfterFilter;
    @Mock
    CreatedBeforeFilterRecommendation createdBeforeFilter;
    @Mock
    StatusFilterRecommendation statusFilter;


    @BeforeEach
    void init() {
        List<RecommendationRequestFilter> mockFilters = Arrays.asList(createdAfterFilter, createdBeforeFilter, statusFilter);
        recommendationRequestFilters.addAll(mockFilters);
    }

    @Test
    void createWithValidData() {
        List<Long> skillIds = Arrays.asList(1L, 2L);
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto()
                .setId(1L)
                .setSkillIds(skillIds)
                .setRequesterId(2L)
                .setReceiverId(3L);
        ArgumentMatcher<Long> allSkillIds = skillId -> Objects.equals(skillId, skillIds.get(0))
                || Objects.equals(skillId, skillIds.get(1));
        ArgumentMatcher<Long> requesterAndReceiverIds = id -> Objects.equals(id, recommendationRequestDto.getRequesterId())
                || Objects.equals(id, recommendationRequestDto.getReceiverId());

        when(skillRequestRepository.create(eq(recommendationRequestDto.getId()), longThat(allSkillIds)))
                .thenReturn(new SkillRequest());
        when(userRepository.findById(longThat(requesterAndReceiverIds)))
                .thenReturn(Optional.of(new User()));

        recommendationRequestService.create(recommendationRequestDto);

        verify(mapper).toEntity(recommendationRequestDto);
        verify(skillRequestRepository, times(skillIds.size()))
                .create(eq(recommendationRequestDto.getId()), longThat(allSkillIds));
        verify(userRepository, times(2)).findById(longThat(requesterAndReceiverIds));

        verify(recommendationRequestRepository).save(captor.capture());
        RecommendationRequest recommendationRequest = captor.getValue();
        assertEquals(recommendationRequestDto.getId(), recommendationRequest.getId());
    }

    @Test
    void testGetRequestIfRecommendationRequestNotExistsInDatabase() {
        Long id = 1L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationRequestService.getRequest(id));
        verify(recommendationRequestRepository).findById(id);
    }

    @Test
    void testGetRequestValidData() {
        Long id = 1L;
        RecommendationRequest recommendationRequest = new RecommendationRequest().setId(id);
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        RecommendationRequestDto expectedResult = new RecommendationRequestDto().setId(1L);
        doReturn(expectedResult).when(mapper).toDTO(any(RecommendationRequest.class));

        RecommendationRequestDto actualResult = recommendationRequestService.getRequest(id);

        verify(recommendationRequestRepository).findById(id);
        verify(mapper).toDTO(captor.capture());
        RecommendationRequest recommendationRequestFromToDTO = captor.getValue();
        assertEquals(recommendationRequest.getId(), recommendationRequestFromToDTO.getId());
        assertEquals(expectedResult.getId(), actualResult.getId());
    }

    @Test
    void testRejectRequestIfRecommendationRequestNotExistsInDatabase() {
        Long id = 1L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationRequestService.rejectRequest(id, new RejectionDto()));
        verify(recommendationRequestRepository).findById(id);
    }

    @Test
    void testRejectRequestIfRecommendationRequestWithStatusIsNotPENDING() {
        long id = 1L;
        RecommendationRequest recommendationRequest = new RecommendationRequest()
                .setStatus(RequestStatus.REJECTED);
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        assertThrows(DataValidationException.class, () -> recommendationRequestService.rejectRequest(id, new RejectionDto()));
    }

    @Test
    void testRejectRequestValidData() {
        long id = 1L;
        RecommendationRequest recommendationRequest = new RecommendationRequest()
                .setId(id)
                .setStatus(RequestStatus.PENDING)
                .setSkills(new ArrayList<>());
        RejectionDto rejectionDto = new RejectionDto().setReason("TEST");
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequestDto recommendationRequestDto = recommendationRequestService.rejectRequest(id, rejectionDto);

        verify(mapper).toDTO(recommendationRequest);
        assertEquals(recommendationRequest.getId(), recommendationRequestDto.getId());
        assertEquals(RequestStatus.REJECTED, recommendationRequestDto.getStatus());
    }

    @Test
    void testGetRequestsValidData() {
        RequestFilterDto filterDto = new RequestFilterDto();
        RecommendationRequest recommendationRequest1 = new RecommendationRequest().setId(1L);
        RecommendationRequest recommendationRequest2 = new RecommendationRequest().setId(2L);
        List<RecommendationRequest> allRecommendationRequests = Arrays.asList(recommendationRequest1, recommendationRequest2);

        recommendationRequestFilters.forEach(filter -> {
            when(filter.isFilterApplicable(filterDto)).thenReturn(true);
            when(filter.apply(any(RecommendationRequest.class), eq(filterDto))).thenReturn(true);
        });
        when(recommendationRequestRepository.findAll()).thenReturn(allRecommendationRequests);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filterDto);

        verify(recommendationRequestRepository).findAll();
        recommendationRequestFilters.forEach(filter -> {
            verify(filter).isFilterApplicable(filterDto);
            verify(filter, times(allRecommendationRequests.size())).apply(any(RecommendationRequest.class), eq(filterDto));
        });
        verify(mapper).allToDTO(allRecommendationRequests);
        assertEquals(result.size(), allRecommendationRequests.size());
        assertEquals(recommendationRequest1.getId(), result.get(0).getId());
        assertEquals(recommendationRequest2.getId(), result.get(1).getId());
    }
}