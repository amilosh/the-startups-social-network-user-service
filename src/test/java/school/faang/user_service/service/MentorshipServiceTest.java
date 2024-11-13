package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private User mentor;

    @Mock
    private User mentee1, mentee2;

    @Mock
    private Goal goal1, goal2;

    @Test
    void testStopMentorshipWithUpdatesGoalsAndRemovesMentor() {
        when(mentor.getMentees()).thenReturn(List.of(mentee1, mentee2));
        when(mentee1.getSettingGoals()).thenReturn(List.of(goal1));
        when(mentee2.getSettingGoals()).thenReturn(List.of(goal2));
        when(goal1.getMentor()).thenReturn(mentor);
        when(goal2.getMentor()).thenReturn(mentor);
        when(mentorshipRepository.save(any(User.class))).thenReturn(mock(User.class));
        when(goalRepository.save(any(Goal.class))).thenReturn(mock(Goal.class));

        mentorshipService.stopMentorship(mentor);

        verify(goal1).setMentor(mentee1);
        verify(goal2).setMentor(mentee2);
        verify(mentee1).removeMentor(mentor);
        verify(mentee2).removeMentor(mentor);
        verify(mentorshipRepository, times(2)).save(any(User.class));
        verify(goalRepository, times(2)).save(any(Goal.class));
    }

    @Test
    void testStopMentorshipWithNoMenteesDoesNothing() {
        when(mentor.getMentees()).thenReturn(List.of());

        mentorshipService.stopMentorship(mentor);

        verifyNoInteractions(goal1, goal2, mentee1, mentee2);
    }
}