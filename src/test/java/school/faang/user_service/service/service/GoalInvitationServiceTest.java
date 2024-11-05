package school.faang.user_service.service.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.validator.GoalValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @InjectMocks
    private GoalInvitationService invitationService;

    @Mock
    private GoalInvitationRepository invitationRepository;

    @Mock
    private GoalValidator validator;

    @Spy
    private GoalMapperImpl mapper;

    @Test
    void creatInvitation() {
        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(1);
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setId(1L);

        when(invitationRepository.save(any())).thenReturn(invitation);

        GoalInvitationDto result = invitationService.creatInvitation(invitationDto);
        assertEquals(invitationDto, result);

        verify(invitationRepository, times(1)).save(any());
    }
}
