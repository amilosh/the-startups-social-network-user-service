package school.faang.user_service.mapper.recommendation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecommendationMapperTest {
    private RecommendationMapperImpl recommendationMapper;

    private final Long recommendationId = 1L;
    private final Long skillOfferFirstId = 5L;
    private final Long skillOfferSecondId = 7L;
    private final Long skillFirstId = 6L;
    private final Long skillSecondId = 7L;
    private final Long authorId = 2L;
    private final Long receiverId = 3L;
    private final Long requestId = 10L;

    @BeforeAll
    public void setUp() {
        SkillOfferMapperImpl skillOfferMapper = new SkillOfferMapperImpl();
        SkillOfferListMapperImpl skillOfferListMapper = new SkillOfferListMapperImpl(skillOfferMapper);
        recommendationMapper = new RecommendationMapperImpl(skillOfferListMapper);
    }


    @Test
    public void toRecommendationSuccessTest() {
        List<SkillOfferDto> skillOffers = List.of(
                new SkillOfferDto() {{
                    setId(skillOfferFirstId);
                    setRecommendationId(recommendationId);
                    setSkillId(skillFirstId);
                }},
                new SkillOfferDto() {{
                    setId(skillOfferSecondId);
                    setRecommendationId(recommendationId);
                    setSkillId(skillSecondId);
                }}
        );

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(recommendationId);
        recommendationDto.setAuthorId(authorId);
        recommendationDto.setReceiverId(receiverId);
        recommendationDto.setRequestId(requestId);
        recommendationDto.setContent("Context");
        recommendationDto.setSkillOffers(skillOffers);
        recommendationDto.setCreatedAt(LocalDateTime.now());
        recommendationDto.setCreatedAt(null);

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);

        assertThat(recommendation).isNotNull();
        assertThat(recommendation.getId()).isEqualTo(recommendationDto.getId());
        assertThat(recommendation.getAuthor().getId()).isEqualTo(recommendationDto.getAuthorId());
        assertThat(recommendation.getReceiver().getId()).isEqualTo(recommendationDto.getReceiverId());
        assertThat(recommendation.getRequest().getId()).isEqualTo(recommendationDto.getRequestId());
        assertThat(recommendation.getSkillOffers()).isNotNull();
        assertThat(recommendation.getSkillOffers().size()).isEqualTo(recommendationDto.getSkillOffers().size());
        for (int i = 0; i < recommendation.getSkillOffers().size(); i++) {
            assertThat(recommendation.getSkillOffers().get(i)).isNotNull();
            assertThat(recommendation.getSkillOffers().get(i).getId())
                    .isEqualTo(recommendationDto.getSkillOffers().get(i).getId());
            assertThat(recommendation.getSkillOffers().get(i).getSkill().getId())
                    .isEqualTo(recommendationDto.getSkillOffers().get(i).getSkillId());
            assertThat(recommendation.getSkillOffers().get(i).getRecommendation().getId())
                    .isEqualTo(recommendationDto.getSkillOffers().get(i).getRecommendationId());
        }
    }

    @Test
    public void toRecommendationWithAuthorOnlyFailTest() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(recommendationId);
        recommendationDto.setAuthorId(authorId);

        assertThrows(NullPointerException.class,
                () -> recommendationMapper.toRecommendation(recommendationDto)
        );
    }

    @Test
    public void toRecommendationWithAuthorAndReceiverOnlyFailTest() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(recommendationId);
        recommendationDto.setAuthorId(authorId);
        recommendationDto.setReceiverId(receiverId);

        assertThrows(NullPointerException.class,
                () -> recommendationMapper.toRecommendation(recommendationDto)
        );
    }

    @Test
    public void toRecommendationDtoSuccessTest() {
        Skill skillFirst = Skill.builder()
                .id(skillFirstId)
                .build();
        Skill skillSecond = Skill.builder()
                .id(skillSecondId)
                .build();

        List<SkillOffer> skillOffers = List.of(
                SkillOffer.builder()
                        .id(skillOfferFirstId)
                        .skill(skillFirst)
                        .build(),
                SkillOffer.builder()
                        .id(skillOfferSecondId)
                        .skill(skillSecond)
                        .build()
        );

        User author = User.builder()
                .id(authorId)
                .build();
        User receiver = User.builder()
                .id(receiverId)
                .build();

        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .id(requestId)
                .build();

        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .author(author)
                .receiver(receiver)
                .request(recommendationRequest)
                .skillOffers(skillOffers)
                .build();

        recommendation.getSkillOffers().forEach(skillOffer -> {
            skillOffer.setRecommendation(recommendation);
        });


        RecommendationDto recommendationDto = recommendationMapper.toRecommendationDto(recommendation);

        assertThat(recommendationDto).isNotNull();
        assertThat(recommendationDto.getId()).isEqualTo(recommendation.getId());
        assertThat(recommendationDto.getAuthorId()).isEqualTo(recommendation.getAuthor().getId());
        assertThat(recommendationDto.getReceiverId()).isEqualTo(recommendation.getReceiver().getId());
        assertThat(recommendationDto.getRequestId()).isEqualTo(recommendation.getRequest().getId());
        assertThat(recommendationDto.getSkillOffers()).isNotNull();
        assertThat(recommendationDto.getSkillOffers().size()).isEqualTo(recommendation.getSkillOffers().size());
        for (int i = 0; i < recommendationDto.getSkillOffers().size(); i++) {
            assertThat(recommendationDto.getSkillOffers().get(i)).isNotNull();
            assertThat(recommendationDto.getSkillOffers().get(i).getId())
                    .isEqualTo(recommendation.getSkillOffers().get(i).getId());
            assertThat(recommendationDto.getSkillOffers().get(i).getSkillId())
                    .isEqualTo(recommendation.getSkillOffers().get(i).getSkill().getId());
            assertThat(recommendationDto.getSkillOffers().get(i).getRecommendationId())
                    .isEqualTo(recommendation.getSkillOffers().get(i).getRecommendation().getId());
        }
    }

    @Test
    public void toRecommendationDtoWithNullFailTest() {
        RecommendationDto recommendationDto = recommendationMapper.toRecommendationDto(null);
        assertThat(recommendationDto).isNull();
    }

    @Test
    public void toRecommendationDtoWithoutRecommendationIdFailTest() {
        Recommendation recommendation = Recommendation.builder().build();
        RecommendationDto recommendationDto = recommendationMapper.toRecommendationDto(recommendation);

        assertThat(recommendationDto).isNotNull();
        assertThat(recommendationDto.getId()).isEqualTo(0L);
    }

    @Test
    public void toRecommendationDtoWithAuthorOnlyFailTest() {
        User author = User.builder()
                .id(authorId)
                .build();

        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .author(author)
                .build();

        RecommendationDto recommendationDto = recommendationMapper.toRecommendationDto(recommendation);

        assertThat(recommendationDto).isNotNull();
        assertThat(recommendationDto.getAuthorId()).isEqualTo(recommendation.getAuthor().getId());
        assertThat(recommendationDto.getReceiverId()).isNull();
        assertThat(recommendationDto.getSkillOffers()).isNull();
        assertThat(recommendationDto.getContent()).isNull();
        assertThat(recommendationDto.getRequestId()).isNull();
    }

    @Test
    public void toRecommendationDtoWithAuthorAndReceiverOnlyFailTest() {
        User author = User.builder()
                .id(authorId)
                .build();
        User receiver = User.builder()
                .id(receiverId)
                .build();

        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .author(author)
                .receiver(receiver)
                .build();

        RecommendationDto recommendationDto = recommendationMapper.toRecommendationDto(recommendation);

        assertThat(recommendationDto).isNotNull();
        assertThat(recommendationDto.getAuthorId()).isEqualTo(recommendation.getAuthor().getId());
        assertThat(recommendationDto.getReceiverId()).isEqualTo(recommendation.getReceiver().getId());
        assertThat(recommendationDto.getSkillOffers()).isNull();
        assertThat(recommendationDto.getContent()).isNull();
        assertThat(recommendationDto.getRequestId()).isNull();
    }

    @Test
    public void toRecommendationDtoWithAuthorAndReceiverAndRequestAndSkillOffersWithoutSkillOnlyFailTest() {
        Skill skillFirst = Skill.builder()
                .build();

        List<SkillOffer> skillOffers = List.of(
                SkillOffer.builder()
                        .id(skillOfferFirstId)
                        .skill(skillFirst)
                        .build()
        );

        User author = User.builder()
                .id(authorId)
                .build();
        User receiver = User.builder()
                .id(receiverId)
                .build();

        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .id(requestId)
                .build();

        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .author(author)
                .receiver(receiver)
                .request(recommendationRequest)
                .skillOffers(skillOffers)
                .build();


        RecommendationDto recommendationDto = recommendationMapper.toRecommendationDto(recommendation);

        assertThat(recommendationDto).isNotNull();
        assertThat(recommendationDto.getAuthorId()).isEqualTo(recommendation.getAuthor().getId());
        assertThat(recommendationDto.getReceiverId()).isEqualTo(recommendation.getReceiver().getId());
        assertThat(recommendationDto.getRequestId()).isEqualTo(recommendation.getRequest().getId());
        assertThat(recommendationDto.getSkillOffers()).isNotNull();
        assertThat(recommendationDto.getSkillOffers().size()).isEqualTo(recommendation.getSkillOffers().size());
        assertThat(recommendationDto.getSkillOffers().get(0).getId()).isEqualTo(recommendation.getSkillOffers().get(0).getId());
        assertThat(recommendationDto.getSkillOffers().get(0).getSkillId()).isNotNull();
        assertThat(recommendationDto.getContent()).isNull();
    }


    @Test
    public void toRecommendationDtoWithAuthorAndReceiverAndRequestAndSkillOffersOnlyFailTest() {
        Skill skillFirst = Skill.builder()
                .id(skillFirstId)
                .build();

        List<SkillOffer> skillOffers = List.of(
                SkillOffer.builder()
                        .id(skillOfferFirstId)
                        .skill(skillFirst)
                        .build()
        );

        User author = User.builder()
                .id(authorId)
                .build();
        User receiver = User.builder()
                .id(receiverId)
                .build();

        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .id(requestId)
                .build();

        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .author(author)
                .receiver(receiver)
                .request(recommendationRequest)
                .skillOffers(skillOffers)
                .build();

        RecommendationDto recommendationDto = recommendationMapper.toRecommendationDto(recommendation);

        assertThat(recommendationDto).isNotNull();
        assertThat(recommendationDto.getAuthorId()).isEqualTo(recommendation.getAuthor().getId());
        assertThat(recommendationDto.getReceiverId()).isEqualTo(recommendation.getReceiver().getId());
        assertThat(recommendationDto.getRequestId()).isEqualTo(recommendation.getRequest().getId());
        assertThat(recommendationDto.getSkillOffers()).isNotNull();
        assertThat(recommendationDto.getSkillOffers().size()).isEqualTo(recommendation.getSkillOffers().size());
        assertThat(recommendationDto.getSkillOffers().get(0).getId()).isEqualTo(recommendation.getSkillOffers().get(0).getId());
        assertThat(recommendationDto.getSkillOffers().get(0).getSkillId()).isEqualTo(recommendation.getSkillOffers().get(0).getSkill().getId());
        assertThat(recommendationDto.getSkillOffers().get(0).getRecommendationId()).isNull();
        assertThat(recommendationDto.getContent()).isNull();
    }
}


