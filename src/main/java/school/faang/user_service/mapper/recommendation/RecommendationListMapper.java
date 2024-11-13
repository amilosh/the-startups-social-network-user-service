package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = RecommendationMapper.class)
public interface RecommendationListMapper {
    List<Recommendation> toRecommendationList(List<RecommendationDto> recommendationDtos);

    List<RecommendationDto> toRecommendationDtoList(List<Recommendation> recommendations);
}
