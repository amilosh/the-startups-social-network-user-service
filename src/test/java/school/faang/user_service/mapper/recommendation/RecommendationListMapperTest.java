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
public class RecommendationListMapperTest {
    private RecommendationListMapperImpl recommendationListMapper;
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
        RecommendationMapperImpl recommendationMapper = new RecommendationMapperImpl(skillOfferListMapper);
        recommendationListMapper = new RecommendationListMapperImpl(recommendationMapper);
    }

    @Test
    public void toRecommendationListSuccessTest() {
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

        RecommendationDto recommendationFirstDto = new RecommendationDto();
        recommendationFirstDto.setId(recommendationId);
        recommendationFirstDto.setAuthorId(authorId);
        recommendationFirstDto.setReceiverId(receiverId);
        recommendationFirstDto.setRequestId(requestId);
        recommendationFirstDto.setContent("Context");
        recommendationFirstDto.setSkillOffers(skillOffers);
        recommendationFirstDto.setCreatedAt(LocalDateTime.now());
        recommendationFirstDto.setCreatedAt(null);

        RecommendationDto recommendationSecondDto = new RecommendationDto();
        recommendationSecondDto.setId(100L);
        recommendationSecondDto.setAuthorId(authorId);
        recommendationSecondDto.setReceiverId(receiverId);
        recommendationSecondDto.setRequestId(requestId);
        recommendationSecondDto.setContent("Context");
        recommendationSecondDto.setSkillOffers(skillOffers);
        recommendationSecondDto.setCreatedAt(LocalDateTime.now());
        recommendationSecondDto.setCreatedAt(null);

        List<RecommendationDto> recommendationDtoList = List.of(
                recommendationFirstDto,
                recommendationSecondDto
        );

        List<Recommendation> recommendationList = recommendationListMapper.toRecommendationList(recommendationDtoList);

        assertThat(recommendationList).isNotNull();
        assertThat(recommendationList.size()).isEqualTo(recommendationDtoList.size());
        for (int i = 0; i < recommendationList.size(); i++) {
            assertThat(recommendationList.get(i)).isNotNull();
            assertThat(recommendationList.get(i).getId()).isEqualTo(recommendationDtoList.get(i).getId());
            assertThat(recommendationList.get(i).getAuthor().getId()).isEqualTo(recommendationDtoList.get(i).getAuthorId());
            assertThat(recommendationList.get(i).getReceiver().getId()).isEqualTo(recommendationDtoList.get(i).getReceiverId());
            assertThat(recommendationList.get(i).getRequest().getId()).isEqualTo(recommendationDtoList.get(i).getRequestId());
            assertThat(recommendationList.get(i).getSkillOffers()).isNotNull();
            assertThat(recommendationList.get(i).getSkillOffers().size()).isEqualTo(recommendationDtoList.get(i).getSkillOffers().size());
            for (int j = 0; i < recommendationList.get(j).getSkillOffers().size(); i++) {
                assertThat(recommendationList.get(j).getSkillOffers().get(j)).isNotNull();
                assertThat(recommendationList.get(j).getSkillOffers().get(j).getId())
                        .isEqualTo(recommendationDtoList.get(i).getSkillOffers().get(j).getId());
                assertThat(recommendationList.get(j).getSkillOffers().get(j).getSkill().getId())
                        .isEqualTo(recommendationDtoList.get(i).getSkillOffers().get(j).getSkillId());
                assertThat(recommendationList.get(j).getSkillOffers().get(j).getRecommendation().getId())
                        .isEqualTo(recommendationDtoList.get(i).getSkillOffers().get(j).getRecommendationId());
            }
        }
    }


    @Test
    public void toRecommendationListFailTest() {
        List<Recommendation> recommendationList = recommendationListMapper.toRecommendationList(null);
        assertThat(recommendationList).isNull();
    }

    @Test
    public void toRecommendationListWithRecommendationEmptyListFailTest() {
        RecommendationDto recommendationFirstDto = new RecommendationDto();
        List<RecommendationDto> recommendationDtoList = List.of(
                recommendationFirstDto
        );

        assertThrows(NullPointerException.class,
                () -> recommendationListMapper.toRecommendationList(recommendationDtoList)
        );
    }

    @Test
    public void toRecommendationDtoListSuccessTest() {

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

        Recommendation recommendationFirst = Recommendation.builder()
                .id(recommendationId)
                .author(author)
                .receiver(receiver)
                .request(recommendationRequest)
                .skillOffers(skillOffers)
                .build();

        recommendationFirst.getSkillOffers().forEach(skillOffer -> {
            skillOffer.setRecommendation(recommendationFirst);
        });

        Recommendation recommendationSecond = Recommendation.builder()
                .id(recommendationId)
                .author(author)
                .receiver(receiver)
                .request(recommendationRequest)
                .skillOffers(skillOffers)
                .build();

        recommendationSecond.getSkillOffers().forEach(skillOffer -> {
            skillOffer.setRecommendation(recommendationSecond);
        });

        List<Recommendation> recommendationList = List.of(
                recommendationFirst,
                recommendationSecond
        );

        List<RecommendationDto> recommendationDtoList = recommendationListMapper.toRecommendationDtoList(recommendationList);

        assertThat(recommendationDtoList).isNotNull();
        assertThat(recommendationDtoList.size()).isEqualTo(recommendationList.size());
        for (int i = 0; i < recommendationDtoList.size(); i++) {
            assertThat(recommendationDtoList.get(i)).isNotNull();
            assertThat(recommendationDtoList.get(i).getId()).isEqualTo(recommendationList.get(i).getId());
            assertThat(recommendationDtoList.get(i).getAuthorId()).isEqualTo(recommendationList.get(i).getAuthor().getId());
            assertThat(recommendationDtoList.get(i).getReceiverId()).isEqualTo(recommendationList.get(i).getReceiver().getId());
            assertThat(recommendationDtoList.get(i).getRequestId()).isEqualTo(recommendationList.get(i).getRequest().getId());
            assertThat(recommendationDtoList.get(i).getSkillOffers()).isNotNull();
            assertThat(recommendationDtoList.get(i).getSkillOffers().size()).isEqualTo(recommendationList.get(i).getSkillOffers().size());
            for (int j = 0; i < recommendationDtoList.get(j).getSkillOffers().size(); i++) {
                assertThat(recommendationDtoList.get(j).getSkillOffers().get(j)).isNotNull();
                assertThat(recommendationDtoList.get(i).getSkillOffers().get(j).getId())
                        .isEqualTo(recommendationList.get(j).getSkillOffers().get(j).getId());
                assertThat(recommendationDtoList.get(i).getSkillOffers().get(j).getSkillId())
                        .isEqualTo(recommendationList.get(j).getSkillOffers().get(j).getSkill().getId());
                assertThat(recommendationDtoList.get(i).getSkillOffers().get(j).getRecommendationId())
                        .isEqualTo(recommendationList.get(j).getSkillOffers().get(j).getRecommendation().getId());
            }
        }
    }

    @Test
    public void toRecommendationDtoListFailTest() {
        Recommendation recommendationFirst = Recommendation.builder().build();

        List<Recommendation> recommendationList = List.of(
                recommendationFirst
        );

        List<RecommendationDto> recommendationDtoList = recommendationListMapper.toRecommendationDtoList(recommendationList);
        assertThat(recommendationDtoList).isNotNull();
        assertThat(recommendationDtoList.size()).isEqualTo(recommendationList.size());
        assertThat(recommendationDtoList.get(0).getId()).isEqualTo(0);
        assertThat(recommendationDtoList.get(0).getContent()).isNull();
        assertThat(recommendationDtoList.get(0).getAuthorId()).isNull();
        assertThat(recommendationDtoList.get(0).getReceiverId()).isNull();
        assertThat(recommendationDtoList.get(0).getReceiverId()).isNull();
    }
}
