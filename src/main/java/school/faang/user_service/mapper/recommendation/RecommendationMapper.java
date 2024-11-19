package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.recommendation.SkillRequest;

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
    @Mapping(target = "id", ignore = true)
    Recommendation toEntity(RequestRecommendationDto recommendationDto);

    @Mapping(target = "request", ignore = true)
    void updateFromDto(RequestRecommendationDto recommendationDto, @MappingTarget Recommendation recommendation);

    List<ResponseRecommendationDto> toDtoList(List<Recommendation> recommendations);

    @Mapping(source = "requester", target = "receiver")
    @Mapping(source = "receiver", target = "author")
    @Mapping(source = "message", target = "content")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "skills", target = "skillOffers")
    @Mapping(target = "request", ignore = true)
    Recommendation fromRequestEntity(RecommendationRequest recommendationRequest);

    @Mapping(source = "skill", target = "skill")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer skillRequestToSkillOffer(SkillRequest skillRequest);

    List<SkillOffer> skillRequestsToSkillOffers(List<SkillRequest> skillRequests);
}
