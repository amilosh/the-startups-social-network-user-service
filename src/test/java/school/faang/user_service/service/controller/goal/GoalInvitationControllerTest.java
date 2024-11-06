package school.faang.user_service.service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {
    @InjectMocks
    private GoalInvitationController invitationController;

    @Mock
    private GoalInvitationService invitationService;

    @Test
    void createInvitation() {

    }
}
