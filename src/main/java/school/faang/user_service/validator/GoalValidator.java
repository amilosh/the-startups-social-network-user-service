package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private static final int MAX_NUMBER_TARGET = 3;

    private final GoalInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;


    public void validateCorrectnessPlayers(GoalInvitationDto invitationDto) {
        if (invitationDto.getInvitedUserId() == null || invitationDto.getInviterId() == null) {
            throw new IllegalArgumentException("Приглашающий и приглашенный игроки должны быть указаны");
        }

        if (invitationDto.getInvitedUserId().equals(invitationDto.getInviterId())) {
            throw new IllegalArgumentException("Приглашающий и приглашенный игрок не должен быть одним пользователем");
        }

        if (!invitationRepository.existsById(invitationDto.getInvitedUserId()) ||
                !invitationRepository.existsById(invitationDto.getInviterId())) {
            throw new IllegalArgumentException("Игроки должны существовать в бд");
        }
    }

    public void validateUsersAndGoals(long id) {
        if (userRepository.getReferenceById(id).getGoals().size() >= MAX_NUMBER_TARGET) {
            throw new IllegalArgumentException("У вас максимальное количество пользователей");
        }

        if (userRepository.existsById(id)) {
            throw new IllegalArgumentException("Вы уже работаете над целью");
        }

        checkGoalExist(id);
    }

    public void validateTarget(long id) {
        checkGoalExist(id);
    }

    private void checkGoalExist(long id) {
        if (!goalRepository.existsById(id)) {
            throw new IllegalArgumentException("Цели не существует");
        }
    }
}
