package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.goal.GoalInvitationEqualsException;
import school.faang.user_service.exception.goal.GoalInvitationNullObjectException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationDtoValidatorTest {

    @InjectMocks
    private GoalInvitationDtoValidator validator;

    private GoalInvitationDto dto;

    @BeforeEach
    public void setUp() {
        dto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .status(RequestStatus.ACCEPTED)
                .build();
    }

    @Test
    @DisplayName("InviterId null test")
    public void testInviterIdIsNull() {
        dto.setInviterId(null);

        assertThrows(GoalInvitationNullObjectException.class, () -> validator.validate(dto));
    }

    @Test
    @DisplayName("InvitedUserId null test")
    public void testInvitedUserIdIsNull() {
        dto.setInvitedUserId(null);

        assertThrows(GoalInvitationNullObjectException.class, () -> validator.validate(dto));
    }

    @Test
    @DisplayName("Invitee and his ID equals test")
    public void testEqualsInviteeAndHisIdSame() {
        dto.setInviterId(1L);
        dto.setInvitedUserId(1L);

        assertThrows(GoalInvitationEqualsException.class, () -> validator.validate(dto));
    }

    @Test
    @DisplayName("Invalid status")
    public void testInvalidStatus() {
        dto.setStatus(null);

        assertThrows(GoalInvitationNullObjectException.class, () -> validator.validate(dto));
    }

    @Test
    @DisplayName("Goal ID empty test")
    public void testGoalIdIsEmpty() {
        dto.setStatus(null);

        assertThrows(GoalInvitationNullObjectException.class, () -> validator.validate(dto));
    }
}
