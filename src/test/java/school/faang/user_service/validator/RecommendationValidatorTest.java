package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationValidatorTest {
    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private RecommendationRepository recommendationRepository;

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
    void testRecommendationWithEarlyDate() {
        User author = new User();
        author.setId(dto.getAuthorId());
        recommendation.setAuthor(author);
        recommendation.setCreatedAt(LocalDateTime.now().minusDays(150));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateTimeAfterLastRecommendation(dto));
    }

    @Test
    void testSkillNotExists() {
        when(skillValidator.validateSkillExists(Mockito.anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateSkillExists(dto));
    }

    @Test
    void testRecommendationExistenceInvalidId() {
        recommendation.setId(1L);
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(false);

        Exception result = assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateRecommendationExistsById(recommendation.getId()));

        assertEquals("Recommendation with id #" + recommendation.getId() + " doesn't exist in the system",
                result.getMessage());
    }
}
