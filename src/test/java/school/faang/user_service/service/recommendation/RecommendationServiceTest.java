package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.RequestSkillOfferDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.dto.recommendation.ResponseSkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationDtoValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private static final long AUTHOR_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long SKILL_ID = 1L;
    private static final long SKILL_OFFER_ID = 1L;
    private static final long RECOMMENDATION_ID = 1L;
    private static final String CONTENT = "content";
    private static final String UPDATED_CONTENT = "update";
    private static final String SKILL_TITLE = "Java";


    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationDtoValidator recommendationDtoValidator;

    @Mock
    private RecommendationMapperImpl recommendationMapper;

    private SkillOffer skillOffer;
    private RequestSkillOfferDto requestSkillOfferDto;
    private ResponseSkillOfferDto responseSkillOfferDto;
    private Recommendation recommendation;
    private RequestRecommendationDto requestRecommendationDto;
    private ResponseRecommendationDto responseRecommendationDto;
    private List<Skill> skills;
    private List<Recommendation> recommendations;
    private List<RequestRecommendationDto> recommendationDtos;

    @BeforeEach
    public void setUp() {
        User author = User.builder().id(AUTHOR_ID).build();
        User receiver = User.builder().id(RECEIVER_ID).build();
        skillOffer = SkillOffer.builder().id(SKILL_OFFER_ID).build();
        Skill skill = Skill.builder().id(SKILL_ID).build();
        skills = List.of(skill);

        requestSkillOfferDto = RequestSkillOfferDto.builder()
                .id(SKILL_OFFER_ID)
                .skillId(SKILL_ID)
                .skillTitle(SKILL_TITLE)
                .build();

        responseSkillOfferDto = ResponseSkillOfferDto.builder()
                .id(SKILL_OFFER_ID)
                .skillId(SKILL_ID)
                .build();

        recommendation = Recommendation.builder()
                .id(RECOMMENDATION_ID)
                .author(author)
                .receiver(receiver)
                .content(CONTENT)
                .skillOffers(List.of(skillOffer))
                .createdAt(LocalDateTime.now())
                .build();

        requestRecommendationDto = RequestRecommendationDto.builder()
                .id(RECOMMENDATION_ID)
                .receiverId(RECEIVER_ID)
                .authorId(AUTHOR_ID)
                .content(CONTENT)
                .skillOffers(List.of(requestSkillOfferDto))
                .createdAt(LocalDateTime.now())
                .build();

        responseRecommendationDto = ResponseRecommendationDto.builder()
                .id(RECOMMENDATION_ID)
                .receiverId(RECEIVER_ID)
                .authorId(AUTHOR_ID)
                .content(CONTENT)
                .skillOffers(List.of(responseSkillOfferDto))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        recommendations = List.of(new Recommendation(), new Recommendation());
        recommendationDtos = List.of(RequestRecommendationDto.builder().build(),
                RequestRecommendationDto.builder().build());
    }

    @Test
    @DisplayName("Test create recommendation with empty skill offers list")
    public void testCreateWhenSkillOffersListIsEmpty() {
        requestRecommendationDto.setSkillOffers(null);
        assertThrows(DataValidationException.class, () -> recommendationService.create(requestRecommendationDto));
    }

    @Test
    @DisplayName("Test create recommendation when receiver not found")
    public void testCreateWhenReceiverNotFound() {
        assertThrows(NoSuchElementException.class, () -> recommendationService.create(requestRecommendationDto));
    }

    @Test
    @DisplayName("Test create recommendation when author not found")
    public void testCreateWhenAuthorNotFound() {
        assertThrows(NoSuchElementException.class, () -> recommendationService.create(requestRecommendationDto));
    }

    @Test
    @DisplayName("Successful creation of a recommendation")
    public void testCreateThenSuccess() {
        when(recommendationRepository.create(requestRecommendationDto.getAuthorId(),
                requestRecommendationDto.getReceiverId(), requestRecommendationDto.getContent()))
                .thenReturn(RECOMMENDATION_ID);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.ofNullable(recommendation));
        when(recommendationMapper.toDto(recommendation)).thenReturn(responseRecommendationDto);

        ResponseRecommendationDto resultRecommendationDto = recommendationService.create(requestRecommendationDto);

        assertNotNull(resultRecommendationDto);
        verify(recommendationRepository).create(requestRecommendationDto.getAuthorId(),
                requestRecommendationDto.getReceiverId(), requestRecommendationDto.getContent());
        verify(recommendationRepository).findById(RECOMMENDATION_ID);
        verify(recommendationMapper).toDto(recommendation);
    }
}
