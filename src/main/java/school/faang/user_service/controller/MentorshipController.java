package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MenteesDto;
import school.faang.user_service.dto.MentorsDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    public List<MenteesDto> getMentees(long userId) {

        return mentorshipService.getMentees(userId);
    }

    public List<MentorsDto> getMentors(long userId) {

        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {

        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {

        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
