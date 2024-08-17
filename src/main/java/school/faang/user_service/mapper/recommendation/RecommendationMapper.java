package school.faang.user_service.mapper.recommendation;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.event.recommendation.RecommendationEvent;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillOfferMapper.class)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "toListSkillOfferDtos")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "toListSkillOffersEntity", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);

    default List<RecommendationDto> toListDto(List<Recommendation> recommendations){
        return recommendations.stream().map(this::toDto).toList();
    }

    default RecommendationEvent toEvent(RecommendationDto dto) {
        return RecommendationEvent.builder()
                .recommendationId(dto.getId())
                .authorId(dto.getAuthorId())
                .receiverId(dto.getReceiverId())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
