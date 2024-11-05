package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.GoalValidator;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository invitationRepository;
    private final GoalMapper mapper;
    private final GoalValidator validator;

    public GoalInvitationDto creatInvitation(GoalInvitationDto invitationDto) {
        validator.checkingCorrectnessPlayers(invitationDto);

        GoalInvitation invitation = invitationRepository.save(mapper.dtoToEntity(invitationDto));
        return mapper.entityToDto(invitation);
    }
}
