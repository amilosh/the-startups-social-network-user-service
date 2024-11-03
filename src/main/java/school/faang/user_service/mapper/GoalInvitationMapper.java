package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalInvitationResponseDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {

    @Mapping(source = "goal.id", target = "goalId")
    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedId")
    @Mapping(target = "status", expression = "java(RequestStatusDto.valueOf(goalInvitation.getStatus().name()))")
    GoalInvitationResponseDto toResponseDto(GoalInvitation goalInvitation);
}
