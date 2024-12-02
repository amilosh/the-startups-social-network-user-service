package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{userId}")
    public ResponseEntity<List<UserDto>> getMentees(@PathVariable @Valid Long userId, @RequestBody(required = false) UserFilterDto filters) {
        List<UserDto> mentees = mentorshipService.getMentees(userId, filters);
        return ResponseEntity.ok(mentees);
    }

    @GetMapping("/mentors/{userId}")
    public ResponseEntity<List<UserDto>> getMentors(@RequestBody @Valid @PathVariable Long userId, @RequestBody(required = false) UserFilterDto filters) {
        List<UserDto> mentors = mentorshipService.getMentors(userId, filters);
        return ResponseEntity.ok(mentors);
    }

    @DeleteMapping("/mentee/{menteeId}/{mentorId}")
    public ResponseEntity<Boolean> deleteMentee(@PathVariable Long menteeId, @PathVariable Long mentorId) {
        boolean isDeleted = mentorshipService.deleteMentee(menteeId, mentorId);
        return isDeleted ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

    @DeleteMapping("/mentor/{menteeId}/{mentorId}")
    public ResponseEntity<Boolean> deleteMentor(@PathVariable Long menteeId, @PathVariable Long mentorId) {
        boolean isDeleted = mentorshipService.deleteMentor(menteeId, mentorId);
        return isDeleted ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }
}
