package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = RecommendationMapper.class)
public interface RecommendationListMapper {
    List<Recommendation> toRecommendationList(List<RecommendationDto> recommendationDtos);

    List<RecommendationDto> toRecommendationDtoList(List<Recommendation> recommendations);
}
