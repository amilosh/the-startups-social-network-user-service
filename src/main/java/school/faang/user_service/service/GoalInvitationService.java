package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository invitationRepository;
    private final GoalMapper mapper;
    private final GoalValidator validator;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public GoalInvitationDto creatInvitation(GoalInvitationDto invitationDto) {
        validator.validateCorrectnessPlayers(invitationDto);

        GoalInvitation invitation = mapper.dtoToEntity(invitationDto);
        invitation.setInviter(userRepository.findById(invitationDto.getInviterId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден")));
        invitation.setInvited(userRepository.findById(invitationDto.getInvitedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден")));
        invitation.setGoal(goalRepository.findById(invitationDto.getGoalId())
                .orElseThrow(() -> new IllegalArgumentException("Цель не найдена")));

        invitation = invitationRepository.save(invitation);
        return mapper.entityToDto(invitation);
    }

    public void acceptGoalInvitation(long id) {
        validator.validateUsersAndGoals(id);

        userRepository.getReferenceById(id).getGoals().add((int) id, goalRepository.getReferenceById(id));
    }

    public void rejectGoalInvitation(long id) {
        validator.validateTarget(id);

        goalRepository.getReferenceById(id).setStatus(GoalStatus.COMPLETED);
    }
}
