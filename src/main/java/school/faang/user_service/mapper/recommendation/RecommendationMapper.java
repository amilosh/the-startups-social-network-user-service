package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SkillOfferListMapper.class)
public interface RecommendationMapper {

    @Mapping(target = "author", expression = "java(mapAuthorId(recommendationDto.getAuthorId()))")
    @Mapping(target = "receiver", expression = "java(mapReceiverId(recommendationDto.getReceiverId()))")
    @Mapping(target = "request", expression = "java(mapRequestId(recommendationDto.getRequestId()))")
    Recommendation toRecommendation(RecommendationDto recommendationDto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "request.id", target = "requestId")
    RecommendationDto toRecommendationDto(Recommendation recommendation);


    default User mapAuthorId(Long authorId) {
        return User.builder()
                .id(authorId)
                .build();
    }

    default User mapReceiverId(Long receiverId) {
        return User.builder()
                .id(receiverId)
                .build();
    }

    default RecommendationRequest mapRequestId(Long requestId) {
        return RecommendationRequest.builder()
                .id(requestId)
                .build();
    }
}

