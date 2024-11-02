package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.ResponseSkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    ResponseSkillOfferDto toDto(SkillOffer skillOffer);
}
