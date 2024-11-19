package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long SKILL_ID = 1L;
    private static final long SKILL_REQUEST_ID = 1L;
    private static final long RECOMMENDATION_REQUEST_ID = 1L;
    private static final long RESPONSE_ID = 1L;
    private static final long SKILL_OFFER_ID = 1L;
    private static final RequestStatus RECOMMENDATION_REQUEST_STATUS = RequestStatus.PENDING;
    private static final String MESSAGE = "message";
    private static final String REJECTION_REASON = "reason";
    private static final String SKILL_TITLE = "Java";

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private List<RecommendationRequestFilter> recommendationRequestFilters;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;
    private RejectionDto rejectionDto;
    private RecommendationRequestFilterDto requestFilterDto;
    private List<RecommendationRequest> recommendationRequests;
    private List<RecommendationRequestDto> recommendationDtos;
    private ResponseRecommendationDto responseRecommendationDto;
    private Skill skill;

    @BeforeEach
    public void setUp() {
        User requester = User.builder().id(REQUESTER_ID).build();
        User receiver = User.builder().id(RECEIVER_ID).build();
        skill = Skill.builder().id(SKILL_ID).build();

        SkillRequest skillRequest = SkillRequest.builder()
                .id(SKILL_REQUEST_ID)
                .skill(skill)
                .build();

        SkillRequestDto skillRequestDto = SkillRequestDto.builder()
                .id(SKILL_REQUEST_ID)
                .recommendationRequestId(RECOMMENDATION_REQUEST_ID)
                .skillId(SKILL_ID)
                .skillTitle(SKILL_TITLE)
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .requester(requester)
                .receiver(receiver)
                .message(MESSAGE)
                .status(RECOMMENDATION_REQUEST_STATUS)
                .skills(List.of(skillRequest))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .message(MESSAGE)
                .status(RECOMMENDATION_REQUEST_STATUS)
                .skillRequests(List.of(skillRequestDto))
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .build();

        rejectionDto = RejectionDto.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .rejectionReason(REJECTION_REASON)
                .build();

        recommendationRequests = Collections.singletonList(recommendationRequest);

        recommendationDtos = List.of(RecommendationRequestDto.builder().build(),
                RecommendationRequestDto.builder().build());

        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .id(SKILL_OFFER_ID)
                .skillId(SKILL_ID)
                .skillTitle(SKILL_TITLE)
                .build();

        responseRecommendationDto = ResponseRecommendationDto.builder()
                .id(RESPONSE_ID)
                .authorId(RECEIVER_ID)
                .receiverId(REQUESTER_ID)
                .content(MESSAGE)
                .skillOffers(List.of(skillOfferDto))
                .build();
    }

    @Test
    @DisplayName("Successful recommendation request")
    public void testCreateThenSuccess() {
        when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.ofNullable(skill));

        RecommendationRequestDto resultRequestDto = recommendationRequestService.create(recommendationRequestDto);

        assertNotNull(resultRequestDto);
        verify(userValidator).validateUser(recommendationRequestDto.getRequesterId());
        verify(userValidator).validateUser(recommendationRequestDto.getReceiverId());
        verify(recommendationRequestValidator).validateRecommendation(recommendationRequestDto);
        verify(recommendationRequestMapper).toEntity(recommendationRequestDto);
        verify(skillRepository).findById(SKILL_ID);
        verify(recommendationRequestRepository).save(eq(recommendationRequest));
        verify(recommendationRequestMapper).toDto(recommendationRequest);
    }

    @Test
    @DisplayName("Fetch requests successfully with filters applied")
    void testGetRequestsSuccessfully() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(recommendationRequestMapper.toDtoList(anyList())).thenReturn(recommendationDtos);

        List<RecommendationRequestDto> resultList = recommendationRequestService.getRequests(requestFilterDto);

        assertNotNull(resultList);
        assertEquals(recommendationDtos, resultList);

        verify(recommendationRequestRepository).findAll();
        verify(recommendationRequestMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Fetch request successfully when validated")
    void testGetRequest() {
        when(recommendationRequestValidator.validateRecommendationFromBd(RECOMMENDATION_REQUEST_ID)).thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.getRequest(RECOMMENDATION_REQUEST_ID);

        verify(recommendationRequestValidator).validateRecommendationFromBd(RECOMMENDATION_REQUEST_ID);
        verify(recommendationRequestMapper).toDto(recommendationRequest);
        assertEquals(recommendationRequestDto, result);
    }

    @Test
    @DisplayName("Reject recommendation request successfully")
    void testRejectRequest() {
        when(recommendationRequestValidator.validateRecommendationFromBd(RECOMMENDATION_REQUEST_ID))
                .thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        recommendationRequestService.rejectRequest(RECOMMENDATION_REQUEST_ID, rejectionDto);

        verify(recommendationRequestValidator).validateRecommendationFromBd(RECOMMENDATION_REQUEST_ID);
        verify(recommendationRequestRepository).save(recommendationRequest);
        verify(recommendationRequestMapper).toDto(recommendationRequest);

        assertEquals(RequestStatus.REJECTED, recommendationRequest.getStatus());
        assertEquals(rejectionDto.getRejectionReason(), recommendationRequest.getRejectionReason());
    }

    @Test
    @DisplayName("Accept recommendation request successfully")
    void testAcceptRequest() {
        when(recommendationRequestValidator.validateRecommendationFromBd(RECOMMENDATION_REQUEST_ID))
                .thenReturn(recommendationRequest);
        when(recommendationService.createRecommendationAfterRequestAccepting(recommendationRequest))
                .thenReturn(responseRecommendationDto);

        recommendationRequestService.acceptRequest(RECOMMENDATION_REQUEST_ID);

        verify(recommendationRequestValidator).validateRecommendationFromBd(RECOMMENDATION_REQUEST_ID);
        verify(recommendationRequestRepository).save(recommendationRequest);
        verify(recommendationService).createRecommendationAfterRequestAccepting(recommendationRequest);

        assertEquals(RequestStatus.ACCEPTED, recommendationRequest.getStatus());
    }
}
