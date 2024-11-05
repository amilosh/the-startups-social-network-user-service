package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final GoalInvitationRepository goalRepository;

    public void checkingCorrectnessPlayers(GoalInvitationDto invitationDto) {
        if (invitationDto.getInvitedUserId() == null || invitationDto.getInviterId() == null) {
            throw new IllegalArgumentException("Приглашающий и приглашенный игроки должны быть указаны");
        }

        if (invitationDto.getInvitedUserId().equals(invitationDto.getInviterId())) {
            throw new IllegalArgumentException("Приглашающий и приглашенный игрок не должен быть одним пользователем");
        }

        if (!goalRepository.existsById(invitationDto.getInvitedUserId()) ||
                !goalRepository.existsById(invitationDto.getInviterId())) {
            throw new IllegalArgumentException("Игроки должны существовать в бд");
        }
    }
}
