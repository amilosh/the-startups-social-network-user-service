package school.faang.user_service.mapper.recommendation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SkillOfferListMapperTest {
    private SkillOfferListMapperImpl skillOfferListMapper;
    private final Long recommendationId = 1L;
    private final Long skillOfferFirstId = 3L;
    private final Long skillOfferSecondId = 4L;
    private final Long skillFirstId = 2L;
    private final Long skillSecondId = 5L;

    @BeforeAll
    public void setUp() {
        SkillOfferMapperImpl skillOfferMapper = new SkillOfferMapperImpl();
        skillOfferListMapper = new SkillOfferListMapperImpl(skillOfferMapper);
    }

    @Test
    public void toSkillOfferListSuccessTest() {
        List<SkillOfferDto> skillOfferDtoList = List.of(
                new SkillOfferDto() {{
                    setId(skillOfferFirstId);
                    setSkillId(skillFirstId);
                    setRecommendationId(recommendationId);
                }},
                new SkillOfferDto() {{
                    setId(skillOfferSecondId);
                    setSkillId(skillSecondId);
                    setRecommendationId(recommendationId);
                }}
        );
        List<SkillOffer> skillOfferList = skillOfferListMapper.toSkillOfferList(skillOfferDtoList);
        assertThat(skillOfferList).isNotNull();
        for (int i = 0; i < skillOfferDtoList.size(); i++) {
            assertThat(skillOfferList.get(i)).isNotNull();
            assertThat(skillOfferDtoList.get(i).getId()).isEqualTo(skillOfferList.get(i).getId());
            assertThat(skillOfferDtoList.get(i).getSkillId()).isEqualTo(skillOfferList.get(i).getSkill().getId());
            assertThat(skillOfferDtoList.get(i).getRecommendationId()).isEqualTo(skillOfferList.get(i).getRecommendation().getId());
        }
    }

    @Test
    public void toSkillOfferListWithNullFailTest() {
        List<SkillOffer> skillOfferList = skillOfferListMapper.toSkillOfferList(null);

        assertThat(skillOfferList).isNull();
    }

    @Test
    public void toSkillOfferListWithoutRecommendationAndSkillFailTest() {
        List<SkillOfferDto> skillOfferDtoList = List.of(
                new SkillOfferDto() {{
                    setId(skillOfferFirstId);
                }}
        );

        assertThrows(NullPointerException.class,
                () -> skillOfferListMapper.toSkillOfferList(skillOfferDtoList)
        );
    }

    @Test
    public void toSkillOfferListWithoutRecommendationFailTest() {
        List<SkillOfferDto> skillOfferDtoList = List.of(
                new SkillOfferDto() {{
                    setId(skillOfferFirstId);
                    setRecommendationId(recommendationId);
                }}
        );

        assertThrows(NullPointerException.class,
                () -> skillOfferListMapper.toSkillOfferList(skillOfferDtoList)
        );
    }

    @Test
    public void toSkillOfferListWithoutSkillOfferIdFailTest2() {
        List<SkillOfferDto> skillOfferDtoList = List.of(
                new SkillOfferDto()
        );

        assertThrows(NullPointerException.class,
                () -> skillOfferListMapper.toSkillOfferList(skillOfferDtoList)
        );
    }

    @Test
    public void toSkillOfferDtoListFailTest() {
        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .build();
        Skill skillFirst = Skill.builder()
                .id(skillFirstId)
                .build();
        Skill skillSecond = Skill.builder()
                .id(skillSecondId)
                .build();
        List<SkillOffer> skillOfferList = List.of(
                SkillOffer.builder()
                        .id(skillOfferFirstId)
                        .skill(skillFirst)
                        .recommendation(recommendation)
                        .build(),
                SkillOffer.builder()
                        .id(skillOfferSecondId)
                        .skill(skillSecond)
                        .recommendation(recommendation)
                        .build()
        );

        List<SkillOfferDto> skillOfferDtoList = skillOfferListMapper.toSkillOfferDtoList(skillOfferList);

        for (int i = 0; i < skillOfferDtoList.size(); i++) {
            assertThat(skillOfferDtoList.get(i)).isNotNull();
            assertThat(skillOfferDtoList.get(i).getId()).isEqualTo(skillOfferList.get(i).getId());
            assertThat(skillOfferDtoList.get(i).getSkillId()).isEqualTo(skillOfferList.get(i).getSkill().getId());
            assertThat(skillOfferDtoList.get(i).getRecommendationId()).isEqualTo(skillOfferList.get(i).getRecommendation().getId());
        }
    }

    @Test
    public void toSkillOfferDtoListWithNullFailTest() {
        List<SkillOfferDto> skillOfferDtoList = skillOfferListMapper.toSkillOfferDtoList(null);
        assertThat(skillOfferDtoList).isNull();
    }

    @Test
    public void toSkillOfferDtoListWithoutRecommendationAndSkillFailTest() {
        List<SkillOffer> skillOfferList = List.of(
                SkillOffer.builder()
                        .id(skillOfferFirstId)
                        .build()
        );
        List<SkillOfferDto> skillOfferDtoList = skillOfferListMapper.toSkillOfferDtoList(skillOfferList);
        assertThat(skillOfferDtoList).isNotNull();
        assertThat(skillOfferDtoList.size()).isEqualTo(skillOfferList.size());
        assertThat(skillOfferDtoList.get(0)).isNotNull();
        assertThat(skillOfferDtoList.get(0).getSkillId()).isNull();
        assertThat(skillOfferDtoList.get(0).getRecommendationId()).isNull();
    }

    @Test
    public void toSkillOfferDtoListWithoutRecommendationFailTest() {
        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .build();

        List<SkillOffer> skillOfferList = List.of(
                SkillOffer.builder()
                        .id(skillOfferFirstId)
                        .recommendation(recommendation)
                        .build()
        );
        List<SkillOfferDto> skillOfferDtoList = skillOfferListMapper.toSkillOfferDtoList(skillOfferList);
        assertThat(skillOfferDtoList).isNotNull();
        assertThat(skillOfferDtoList.size()).isEqualTo(skillOfferList.size());
        assertThat(skillOfferDtoList.get(0)).isNotNull();
        assertThat(skillOfferDtoList.get(0).getSkillId()).isNull();
        assertThat(skillOfferDtoList.get(0).getRecommendationId()).isEqualTo(recommendationId);
    }
}
