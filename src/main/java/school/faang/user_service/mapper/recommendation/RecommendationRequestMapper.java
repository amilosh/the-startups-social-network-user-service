package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "map")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    List<RecommendationRequestDto> toDto(List<RecommendationRequest> recommendationRequests);

    List<RecommendationRequest> toEntity(List<RecommendationRequestDto> recommendationRequestDtos);

    @Named("map")
    default List<Long> map(List<SkillRequest> skills) {
        return skills.stream().map(skillRequest -> skillRequest.getSkill().getId()).toList();
    }
}
