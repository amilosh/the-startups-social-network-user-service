package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "string", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvitationFilterMapper {
    GoalInvitation toEntity(InvitationFilterIDto invitationFilterIDto);

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedId")
    InvitationFilterIDto toDto(GoalInvitation goalInvitation);
}
