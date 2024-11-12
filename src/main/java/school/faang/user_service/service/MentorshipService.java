package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    public void stopMentorship(User mentor) {
        if (mentor == null) {
            return;
        }
        mentor.getMentees().forEach(mentee -> {
            mentee.getSettingGoals().forEach(goal -> goal.setMentor(mentee));
            mentee.removeMentor(mentor);
        });

        log.info("Mentorship with {} is stopped", mentor);
    }
}
