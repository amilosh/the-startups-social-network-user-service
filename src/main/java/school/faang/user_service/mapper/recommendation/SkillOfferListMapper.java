package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SkillOfferMapper.class)
public interface SkillOfferListMapper {
    List<SkillOffer> toSkillOfferList(List<SkillOfferDto> skillOfferDtos);

    List<SkillOfferDto> toSkillOfferDtoList(List<SkillOffer> skillOffers);
}
