package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RequestSkillOfferDto;
import school.faang.user_service.dto.recommendation.ResponseSkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    ResponseSkillOfferDto toDto(SkillOffer skillOffer);

    @Mapping(source = "skillId", target = "skill.id")
    @Mapping(source = "recommendationId", target = "recommendation.id")
    SkillOffer toEntity(RequestSkillOfferDto skillOfferDto);
}
