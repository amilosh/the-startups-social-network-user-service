package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.InvitationEntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;
import school.faang.user_service.validator.goal.InvitationDtoValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<InvitationFilter> filters;
    private final InvitationDtoValidator invitationDtoValidator;
    private final List<InvitationFilter> invitationFilters;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        invitationDtoValidator.validate(goalInvitationDto);
        GoalInvitation savedInvitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));
        return goalInvitationMapper.toDto(savedInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such goal invitation with id:" + id));

        User invited = goalInvitation.getInvited();
        if (invited.getReceivedGoalInvitations().size() > 3)
            throw new IllegalArgumentException("Exception invited user can`t have more than 3 goal invitations");

        invited.getGoals().add(goalInvitation.getGoal());
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        userRepository.save(invited);
        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);

    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        log.info("Reject goal with id: {}", id);
        GoalInvitation invitation = findGoalInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toDto(invitation);
    }

    private GoalInvitation findGoalInvitationById(long id) {
        log.info("Find invitation with id: {}", id);
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException("invitation to a goal with id: %s not found"));
    }


    public List<GoalInvitationDto> getInvitationsByFilter(InvitationFilterDto filterDto) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();
        if (invitations.isEmpty()) {
            return new ArrayList<>();
        }
        filters.stream()
                .filter(f -> f.isAcceptable(filterDto))
                .forEach(f -> f.apply(invitations.stream(), filterDto));
        return invitations.stream().map(goalInvitationMapper::toDto).toList();
    }
}
