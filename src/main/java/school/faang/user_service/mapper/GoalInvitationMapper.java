package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.entity.goal.GoalInvitation;

public interface GoalInvitationMapper {

    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "invited", ignore = true)
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedId")
    @Mapping(source = "status", target = "status")
    GoalInvitationResponseDto toResponseDto(GoalInvitation goalInvitation);

    List<GoalInvitationDto> toDtoList(List<GoalInvitation> goalInvitations);

    List<GoalInvitation> toEntityList(List<GoalInvitationDto> goalInvitationDto);
    RequestStatusDto toRequestStatusDto(RequestStatus goalInvitation);
}
