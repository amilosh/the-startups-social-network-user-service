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
    private final MentorshipRepository mentorshipRepository;

    public void stopMentorship(User mentor) {
        mentor.getMentees().forEach(mentee -> {
            mentee.getSettingGoals().forEach(goal -> goal.setMentor(mentee));
            mentee.removeMentor(mentor);
        });

        log.info("Mentorship with {} is stopped", mentor);
    }
}
