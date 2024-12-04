package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationRequestDto;
import school.faang.user_service.dto.goal.GoalInvitationResponseDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.InvitationEntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;
import school.faang.user_service.validator.goal.InvitationValidator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<InvitationFilter> filters;
    private final InvitationValidator invitationValidator;

    @Transactional
    public GoalInvitationResponseDto createInvitation(GoalInvitationRequestDto requestDto) {
        invitationValidator.validate(requestDto);

        GoalInvitation savedInvitation = goalInvitationMapper.toEntity(requestDto);
        goalInvitationRepository.save(savedInvitation);

        return goalInvitationMapper.toDto(savedInvitation);
    }

    @Transactional
    public GoalInvitationResponseDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = findGoalInvitationById(id);

        User invited = goalInvitation.getInvited();
        if (invited.getReceivedGoalInvitations().size() > MAX_ACTIVE_GOALS)
            throw new IllegalArgumentException("Exception invited user can`t have more than 3 goal invitations");

        invitationValidator.validateInvitationStatus(
                goalInvitation, RequestStatus.ACCEPTED,
                "Invitation is already accepted"
        );

        invited.getGoals().add(goalInvitation.getGoal());
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        userRepository.save(invited);
        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationResponseDto rejectGoalInvitation(long id) {
        log.info("Reject goal with id: {}", id);

        GoalInvitation invitation = findGoalInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);

        goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toDto(invitation);
    }

    public List<GoalInvitationResponseDto> getInvitationsByFilter(InvitationFilterDto filterDto) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();
        if (invitations.isEmpty()) {
            return new ArrayList<>();
        }
        filters.stream()
                .filter(f -> f.isAcceptable(filterDto))
                .forEach(f -> f.apply(invitations.stream(), filterDto));
        return invitations.stream().map(goalInvitationMapper::toDto).toList();
    }

    private GoalInvitation findGoalInvitationById(long id) {
        log.info("Find invitation with id: {}", id);
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException("invitation to a goal with id: %s not found"));
    }
}
