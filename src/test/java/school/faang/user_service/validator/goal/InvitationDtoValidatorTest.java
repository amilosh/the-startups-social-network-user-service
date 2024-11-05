package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.InvitationEntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
@ExtendWith(MockitoExtension.class)
public class InvitationDtoValidatorTest {


    @InjectMocks
    private InvitationDtoValidator invitationDtoValidator;
    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        goalInvitationDto = new GoalInvitationDto();
        User invitedUser = new User();
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invitedUser);
        goalInvitation.setStatus(RequestStatus.PENDING);
    }

    @Test
    void testValidateValidInvitation() {
        assertDoesNotThrow(() -> invitationDtoValidator.validate(goalInvitationDto));
    }

    @Test
    void testValidateInvalidInvitation() {
        goalInvitationDto.setInvitedUserId(null);
        assertThrows(IllegalArgumentException.class, () -> invitationDtoValidator.validate(goalInvitationDto));
    }

    @Test
    void testValidateUserNotFound() {
        goalInvitationDto.setInvitedUserId(999L); // предполагается, что такого пользователя нет

        assertThrows(InvitationEntityNotFoundException.class, () -> invitationDtoValidator.validate(goalInvitationDto));
    }

    @Test
    void testValidateNullInvitation() {
        assertThrows(IllegalArgumentException.class, () -> invitationDtoValidator.validate(null));
    }
}
