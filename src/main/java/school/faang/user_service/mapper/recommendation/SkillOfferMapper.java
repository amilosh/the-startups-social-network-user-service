package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {
    @Mapping(target = "skill", expression = "java(mapSkillId(skillOfferDto.getSkillId()))")
    @Mapping(target = "recommendation", expression = "java(mapRecommendationId(skillOfferDto.getRecommendationId()))")
    SkillOffer toSkillOffer(SkillOfferDto skillOfferDto);

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toSkillOfferDto(SkillOffer skillOffer);

    default Skill mapSkillId(long skillId) {
        return Skill.builder()
                .id(skillId)
                .build();
    }

    default Recommendation mapRecommendationId(long recommendationId) {
        return Recommendation.builder()
                .id(recommendationId)
                .build();
    }
}
