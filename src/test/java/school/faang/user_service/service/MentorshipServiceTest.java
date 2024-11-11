package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private User mentor;

    @Mock
    private User mentee1, mentee2;

    @Mock
    private Goal goal1, goal2;

    @Test
    void testStopMentorshipUpdatesGoalsAndRemovesMentor() {
        when(mentor.getMentees()).thenReturn(List.of(mentee1, mentee2));
        when(mentee1.getSettingGoals()).thenReturn(List.of(goal1));
        when(mentee2.getSettingGoals()).thenReturn(List.of(goal2));

        mentorshipService.stopMentorship(mentor);

        verify(goal1).setMentor(mentee1);
        verify(goal2).setMentor(mentee2);
        verify(mentee1).removeMentor(mentor);
        verify(mentee2).removeMentor(mentor);
    }

    @Test
    void testStopMentorshipNoMenteesDoesNothing() {
        when(mentor.getMentees()).thenReturn(List.of());

        mentorshipService.stopMentorship(mentor);

        verifyNoInteractions(goal1, goal2, mentee1, mentee2);
    }
}