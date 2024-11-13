package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.RequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestServiceValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    @Mock
    private List<RequestFilter> requestFilters;
    @Spy
    private RecommendationRequestMapperImpl mapper;
    @Captor
    ArgumentCaptor<RecommendationRequest> captor;
    @InjectMocks
    RecommendationRequestService recommendationRequestService;

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
    void testGetRequestIfRequestNotExistsInDatabase() {
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
}