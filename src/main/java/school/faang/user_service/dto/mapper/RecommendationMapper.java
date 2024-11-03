package school.faang.user_service.dto.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(source = "author.id",  target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "skillOffersToDto")
    RecommendationDto toDto (Recommendation recommendation);
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "skillOffers", ignore = true)
    Recommendation toEntity (RecommendationDto recommendationDto);

    default List<SkillOfferDto> skillOffersToDto(List<SkillOffer> skillOfferList) {
        return skillOfferList.stream()
                .map(skillOffer -> new SkillOfferDto(skillOffer.getId(), skillOffer.getSkill().getId())).toList();
    }
}
