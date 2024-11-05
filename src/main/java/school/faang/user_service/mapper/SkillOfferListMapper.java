package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillOfferMapper.class)
public interface SkillOfferListMapper {
    List<SkillOffer> toSkillOfferList(List<SkillOfferDto> skillOfferDtos);

    List<SkillOfferDto> toSkillOfferDtoList(List<SkillOffer> skillOffers);
}
