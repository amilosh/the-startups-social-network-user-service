package school.faang.user_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationValidatorTest {

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillService skillService;

    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    private RecommendationDto dto;
    private Recommendation recommendation;


    @BeforeEach
    void setUp() {
        dto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("text")
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .recommendationId(1L)
                        .skillId(1L)
                        .build()))
                .build();
        recommendation = recommendationMapper.toEntity(dto);
    }

    @Test
    @DisplayName("testAuthorIdIsNull")
    void testAuthorIdIsNull() {
        dto.setAuthorId(null);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testReceiverIdIsNull")
    void testReceiverIdIsNull() {
        dto.setReceiverId(null);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testReceiverIdEqualsAuthorId")
    void testReceiverIdEqualsAuthorId() {
        dto.setReceiverId(3L);
        dto.setAuthorId(3L);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testContentIsNull")
    void testContentIsNull() {
        dto.setContent(null);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testContentIsBlank")
    void testContentIsBlank() {
        dto.setContent(" ");

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    void testRecommendationWithEarlyDate() {
        User author = new User();
        author.setId(dto.getAuthorId());
        recommendation.setAuthor(author);
        recommendation.setCreatedAt(LocalDateTime.now().minusDays(150));


        Mockito.when(skillOfferRepository.findAllByUserId(dto.getReceiverId())).thenReturn(
                List.of(new SkillOffer(1L, new Skill(), recommendation)));

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateTimeAfterLastRecommendation(dto));
    }

    @Test
    void testSkillNotExists() {
        Mockito.when(skillService.validateSkillExists(Mockito.anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateSkillExists(dto));
    }
}
