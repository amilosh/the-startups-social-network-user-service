package school.faang.user_service.mapper.user_jira;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user_jira.UserJiraCreateUpdateDto;
import school.faang.user_service.dto.user_jira.UserJiraDto;
import school.faang.user_service.entity.userJira.UserJira;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserJiraMapper {

    @Mapping(source = "user.id", target = "userId")
    UserJiraDto toDto(UserJira userJira);

    UserJira toEntity(UserJiraCreateUpdateDto createUpdateDto);

    void update(UserJiraCreateUpdateDto createUpdateDto, @MappingTarget UserJira userJiraTarget);
}
