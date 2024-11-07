package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFound;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final UserService userService;
    private final GoalService goalService;
    private final List<Filter<GoalInvitation, GoalInvitationFilterDto>> filters;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = initializeGoalInvitation(invitation);
        goalInvitation = goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        return goalInvitationRepository.getById(id)
                .map(invitation -> {
                    goalInvitationValidator.validateGoalInvitationAcceptance(invitation);

                    invitation.setStatus(RequestStatus.ACCEPTED);
                    invitation.getInvited()
                            .getGoals()
                            .add(invitation.getGoal());

                    return goalInvitationMapper.toDto(goalInvitationRepository.save(invitation));
                }).orElseThrow(() -> new EntityNotFound("Invitation not found for id: " + id));
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        return goalInvitationRepository.getById(id)
                .map(invitation -> {
                    goalInvitationValidator.validateGoalInvitationRejection(invitation);

                    invitation.setStatus(RequestStatus.REJECTED);

                    return goalInvitationMapper.toDto(goalInvitationRepository.save(invitation));
                }).orElseThrow(() -> new EntityNotFound("Invitation not found for id: " + id));
    }

    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filterDto) {
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();

        return filters.stream().filter(filter -> filter.isApplicable(filterDto))
                .reduce(invitations, (streamGoalInvitation, filter) ->
                        filter.apply(streamGoalInvitation, filterDto), (s1, s2) -> s1)
                .map(goalInvitationMapper::toDto)
                .toList();
    }

    private GoalInvitation initializeGoalInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        goalInvitation.setInviter(userService.findUserById(invitation.getInviterId()));
        goalInvitation.setInvited(userService.findUserById(invitation.getInvitedUserId()));
        goalInvitation.setGoal(goalService.findGoalById(invitation.getGoalId()));
        return goalInvitation;
    }
}
