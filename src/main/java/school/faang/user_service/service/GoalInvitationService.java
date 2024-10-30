package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Service
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;

    @Autowired
    public GoalInvitationService(GoalInvitationRepository goalInvitationRepository) {
        this.goalInvitationRepository = goalInvitationRepository;
    }

    public void createInvitation(GoalInvitationDto invitation) {
        // Валидация
        if (invitation.getInviterId() == null || invitation.getInvitedUserId() == null ||
                invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new IllegalArgumentException("Неверные данные для приглашения"); // Исключение для неверных данных
        }
        goalInvitationRepository.save(invitation);
    }
}
