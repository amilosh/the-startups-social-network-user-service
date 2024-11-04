package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationDtoValidatorTest {

    @InjectMocks
    private GoalInvitationValidator validator;

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

}
