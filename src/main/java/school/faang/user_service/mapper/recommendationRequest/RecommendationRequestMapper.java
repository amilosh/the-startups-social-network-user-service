package school.faang.user_service.mapper.recommendationRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {


   @Mapping(source = "skills", target = "skillsId",qualifiedByName = "mapSkillRequestsToIds")
   @Mapping(source = "receiver", target = "receiverId", qualifiedByName = "mapUserToId")
   @Mapping(source = "requester", target = "requesterId", qualifiedByName = "mapUserToId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapSkillRequestsToIds")
    default List<Long> mapSkillRequestsToIds(List<SkillRequest> skillRequests) {
        return skillRequests.stream().map(SkillRequest::getId).toList();
    }

    @Named("mapUserToId")
    default Long mapUserToId(User user){
        return user.getId();
    }
}
