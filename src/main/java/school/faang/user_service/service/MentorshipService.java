package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;

    public void stopMentorship(User mentor) {
        if (mentor == null) {
            return;
        }
        mentor.getMentees().forEach(mentee -> {
            mentee.getSettingGoals().stream()
                    .filter(goal -> goal.getMentor().equals(mentor))
                    .forEach(goal -> {
                        goal.setMentor(mentee);
                        goalRepository.save(goal);
                    });

            mentee.removeMentor(mentor);
            mentorshipRepository.save(mentee);
        });
        log.info("Mentorship with {} is stopped", mentor.getId());
    }
}
