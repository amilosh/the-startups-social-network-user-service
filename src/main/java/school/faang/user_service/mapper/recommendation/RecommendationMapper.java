package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SkillOfferMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers")
    ResponseRecommendationDto toDto(Recommendation recommendation);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "skillOffers", target = "skillOffers")
    @Mapping(target = "request", ignore = true)
    Recommendation toEntity(RequestRecommendationDto recommendationDto);

    @Mapping(target = "request", ignore = true)
    void updateFromDto(RequestRecommendationDto recommendationDto, @MappingTarget Recommendation recommendation);

    List<ResponseRecommendationDto> toDtoList(List<Recommendation> recommendations);
}
