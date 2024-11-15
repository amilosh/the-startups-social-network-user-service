package school.faang.user_service.mapper.recommendation;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SkillOfferMapperTest {
    private final SkillOfferMapper skillOfferMapper = Mappers.getMapper(SkillOfferMapper.class);

    private final Long recommendationId = 1L;
    private final Long skillOfferId = 2L;
    private final Long skillId = 3L;

    @Test
    public void toSkillOfferDtoSuccessTest() {
        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .build();
        Skill skill = Skill.builder()
                .id(skillId)
                .build();
        SkillOffer skillOffer = SkillOffer.builder()
                .id(skillOfferId)
                .skill(skill)
                .recommendation(recommendation)
                .build();

        SkillOfferDto skillOfferDto = skillOfferMapper.toSkillOfferDto(skillOffer);

        assertThat(skillOfferDto).isNotNull();
        assertThat(skillOfferDto.getId()).isEqualTo(skillOffer.getId());
        assertThat(skillOfferDto.getSkillId()).isEqualTo(skillOffer.getSkill().getId());
        assertThat(skillOfferDto.getRecommendationId()).isEqualTo(skillOffer.getRecommendation().getId());
    }

    @Test
    public void toSkillOfferDtoWithoutSkillOfferIdSuccessTest() {
        SkillOffer skillOffer = SkillOffer.builder().build();

        SkillOfferDto skillOfferDto = skillOfferMapper.toSkillOfferDto(skillOffer);

        assertThat(skillOfferDto).isNotNull();
        assertThat(skillOfferDto.getId()).isEqualTo(0L);
    }

    @Test
    public void toSkillOfferDtoWithoutRecommendationFailTest() {
        SkillOffer skillOffer = SkillOffer.builder()
                .id(skillOfferId)
                .build();

        SkillOfferDto skillOfferDto = skillOfferMapper.toSkillOfferDto(skillOffer);

        assertThat(skillOfferDto).isNotNull();
        assertThat(skillOfferDto.getId()).isNotNull();
        assertThat(skillOfferDto.getRecommendationId()).isNull();
    }

    @Test
    public void toSkillOfferDtoWithoutSkillFailTest() {
        Recommendation recommendation = Recommendation.builder()
                .id(recommendationId)
                .build();
        SkillOffer skillOffer = SkillOffer.builder()
                .id(skillOfferId)
                .recommendation(recommendation)
                .build();

        SkillOfferDto skillOfferDto = skillOfferMapper.toSkillOfferDto(skillOffer);

        assertThat(skillOfferDto).isNotNull();
        assertThat(skillOfferDto.getId()).isNotNull();
        assertThat(skillOfferDto.getRecommendationId()).isNotNull();
        assertThat(skillOfferDto.getSkillId()).isNull();
    }

    @Test
    public void toSkillOfferSuccessTest() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setRecommendationId(recommendationId);
        skillOfferDto.setId(skillOfferId);
        skillOfferDto.setSkillId(skillId);

        SkillOffer skillOffer = skillOfferMapper.toSkillOffer(skillOfferDto);

        assertThat(skillOffer).isNotNull();
        assertThat(skillOfferDto.getId()).isEqualTo(skillOffer.getId());
        assertThat(skillOfferDto.getSkillId()).isEqualTo(skillOffer.getSkill().getId());
        assertThat(skillOfferDto.getRecommendationId()).isEqualTo(skillOffer.getRecommendation().getId());
    }

    @Test
    public void toSkillOfferWithoutRecommendationIdFailTest() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();

        assertThrows(NullPointerException.class,
                () -> skillOfferMapper.toSkillOffer(skillOfferDto)
        );
    }

    @Test
    public void toSkillOfferWithoutSkillOfferIdFailTest() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setRecommendationId(recommendationId);
        skillOfferDto.setId(skillOfferId);

        assertThrows(NullPointerException.class,
                () -> skillOfferMapper.toSkillOffer(skillOfferDto)
        );
    }

    @Test
    public void toSkillOfferWithoutSkillIdFailTest() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setRecommendationId(recommendationId);
        skillOfferDto.setId(skillOfferId);

        assertThrows(NullPointerException.class,
                () -> skillOfferMapper.toSkillOffer(skillOfferDto)
        );
    }
}
