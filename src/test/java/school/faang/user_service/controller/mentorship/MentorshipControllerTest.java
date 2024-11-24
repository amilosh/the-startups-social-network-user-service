package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.MentorshipService;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {
    @InjectMocks
    private MentorshipController mentorshipController;
    @Mock
    private MentorshipService mentorshipService;

    @Test
    void testGetMentees() {
        mentorshipController.getMentees(2);
        Mockito.verify(mentorshipService).getMentees(2);
    }

    @Test
    void testGetMentors() {
        mentorshipController.getMentors(2);
        Mockito.verify(mentorshipService).getMentors(2);
    }

    @Test
    void testDeleteMentee() {
        mentorshipController.deleteMentee(2, 3);
        Mockito.verify(mentorshipService).deleteMentee(2, 3);
    }

    @Test
    void testDeleteMentors() {
        mentorshipController.deleteMentor(2, 3);
        Mockito.verify(mentorshipService).deleteMentor(2, 3);
    }
}