package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillRequestMapper {

    @Mapping(target = "request", ignore = true)
    @Mapping(target = "skill", ignore = true)
    SkillRequest toEntity(SkillRequestDto dto);

    @Mapping(source = "skill.id", target = "skillId")
    SkillRequestDto toDto(SkillRequest request);
}
