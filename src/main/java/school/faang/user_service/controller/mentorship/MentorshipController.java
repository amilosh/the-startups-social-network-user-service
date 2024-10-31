package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import school.faang.user_service.entity.dto.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{mentor_id}")
    public List<MentorshipDto> getMentees(@PathVariable("mentor_id") long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentor/{mentor_id}")
    public List<MentorshipDto> getMentor(@PathVariable("mentee_id") long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentees")
    public void deleteMentees(@RequestParam("mentee_id") long menteeId,
                              @RequestParam("mentor_id") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentors")
    public void deleteMentors(@RequestParam("mentor_id") long mentorId,
                              @RequestParam("mentee_id") long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
