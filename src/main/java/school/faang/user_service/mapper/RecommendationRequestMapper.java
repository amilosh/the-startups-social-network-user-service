package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillRequestToSkillId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    RecommendationRequestDto toDTO(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "requester", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapSkillRequestToSkillId")
    default List<Long> mapSkillRequestToSkillId(List<SkillRequest> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(SkillRequest::getSkill)
                .map(Skill::getId)
                .toList();
    }

    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillRequestToSkillId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    List<RecommendationRequestDto> allToDTO(List<RecommendationRequest> recommendationRequest);

}
