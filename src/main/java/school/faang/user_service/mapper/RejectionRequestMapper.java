package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RejectionRequestMapper {
    @Mapping(source = "rejectionReason", target = "reason")
    RejectionDto toDto(RecommendationRequest rejectionRequest);

    @Mapping(source = "reason", target = "rejectionReason")
    RecommendationRequest toEntity(RejectionDto rejectionDto);
}
