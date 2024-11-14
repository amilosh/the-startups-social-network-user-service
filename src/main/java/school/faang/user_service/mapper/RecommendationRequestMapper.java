package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(target = "skillIdentifiers", source = "skills", qualifiedByName = "mapSkills")
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationRequestDto toDto(RecommendationRequest entity);

    @Named("mapSkills")
    default List<Long> mapSkills(List<SkillRequest> skillRequests) {
        if (skillRequests == null || skillRequests.isEmpty()) {
            return null;
        }
        return skillRequests.stream()
                .map(skillRequest -> skillRequest.getSkill().getId())
                .toList();
    }
}
