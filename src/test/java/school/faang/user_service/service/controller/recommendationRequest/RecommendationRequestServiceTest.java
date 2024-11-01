package school.faang.user_service.service.controller.recommendationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendationRequest.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendationRequest.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendationRequest.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendationRequest.RecommendationRequestService;
import school.faang.user_service.service.SkillRequestService.SkillRequestService;
import school.faang.user_service.validator.recommendationRequest.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Mock
    private SkillRequestService saveSkillRequests;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    RecommendationRequestValidator recommendationRequestValidator;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @Mock
    private List<RecommendationRequestFilter> recommendationRequestFilter;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;
    private User requester;
    private User receiver;

    @BeforeEach
    public void setUp() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.REJECTED)
                .skillList(List.of(1L, 2L))
                .build();

        requester = User.builder()
                .id(1L)
                .build();
        receiver = User.builder()
                .id(2L)
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .skills(List.of(new SkillRequest(), new SkillRequest()))
                .build();
    }

    @Test
    @DisplayName("Verifying successful creation of a recommendation request")
    public void successfulCreateRecommendationRequestTest() {
        when(recommendationRequestMapper.toEntity(recommendationRequestDto))
                .thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(recommendationRequest))
                .thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest))
                .thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);

        assertNotNull(result);

        verify(recommendationRequestValidator, times(1)).validateCreate(
                recommendationRequestDto,
                recommendationRequestRepository,
                userRepository,
                skillRepository);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(saveSkillRequests, times(1)).saveSkillRequests(
                recommendationRequest,
                recommendationRequestDto.getSkillList());
    }

    @Test
    @DisplayName("Receive recommendation requests with filters")
    public void requestRecommendationsWithFiltersTest() {
        List<RecommendationRequest> recommendationRequestsList = new ArrayList<>();
        recommendationRequestsList.add(RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .skills(List.of(new SkillRequest(), new SkillRequest()))
                .build()
        );
        recommendationRequestsList.add(RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .skills(List.of(new SkillRequest(), new SkillRequest()))
                .build()
        );
        recommendationRequestsList.add(RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .skills(List.of(new SkillRequest(), new SkillRequest()))
                .status(RequestStatus.PENDING)
                .build()
        );
        RecommendationRequestFilterDto recommendationRequestFilterDto = RecommendationRequestFilterDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.PENDING)
                .createdAfter(LocalDateTime.now())
                .rejectionReason("Presence of obscene language")
                .build();

        when(recommendationRequestRepository.findAll())
                .thenReturn(recommendationRequestsList);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequest(recommendationRequestFilterDto);

        verify(recommendationRequestRepository, times(1)).findAll();
        assertEquals(result.size(), 3);
    }

    @Test
    @DisplayName("Checking for a recommendation by id")
    public void recommendationByUserIdTest() {
        when(recommendationRequestRepository.findById(1L))
                .thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toDto(recommendationRequest))
                .thenReturn(recommendationRequestDto);

        recommendationRequestService.getRequest(1L);

        assertNotNull(recommendationRequest);

        verify(recommendationRequestRepository, times(1))
                .findById(recommendationRequest.getId());
    }

    @Test
    @DisplayName("Checking the receipt of a recommendation for missing id")
    public void receivingRecommendationsMissingUserIDTest() {
        when(recommendationRequestRepository.findById(1L))
                .thenReturn(Optional.empty());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> recommendationRequestService.getRequest(1L));

        assertEquals("Recommendation request with ID 1 not found",
                dataValidationException.getMessage());
    }

    @Test
    @DisplayName("Declining a recommendation request from another user")
    public void rejectingARequestFromAnotherUserTest() {
        RejectionDto rejectionDto = RejectionDto.builder()
                .rejectionReason("Reason")
                .build();

        when(recommendationRequestValidator.validateRequestExists(recommendationRequestDto.getId(), recommendationRequestRepository))
                .thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest))
                .thenReturn(recommendationRequestDto);

        recommendationRequestService.rejectRequest(recommendationRequestDto.getId(), rejectionDto);

        assertEquals(RequestStatus.REJECTED, recommendationRequest.getStatus());
        assertEquals("Reason", recommendationRequest.getRejectionReason());

        verify(recommendationRequestRepository).save(recommendationRequest);
        verify(recommendationRequestValidator, times(1)).validateRequestExists(
                recommendationRequest.getId(),
                recommendationRequestRepository);
    }
}