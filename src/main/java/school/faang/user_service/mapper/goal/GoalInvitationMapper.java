package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationRequestDto;
import school.faang.user_service.dto.goal.GoalInvitationResponseDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {



    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationResponseDto toDto(GoalInvitation invitation);
    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "invited", ignore = true)
    @Mapping(target = "goal", ignore = true)
    GoalInvitation toEntity(GoalInvitationRequestDto dto);


}
