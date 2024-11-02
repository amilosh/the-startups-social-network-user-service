package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {
    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    void update(@MappingTarget SkillOffer skillOffer, SkillOfferDto skillOfferDto);
}