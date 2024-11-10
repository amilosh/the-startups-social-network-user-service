package school.faang.user_service.controller.mentorship;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RequestMapping("/v1/mentorship")
@RestController
@RequiredArgsConstructor
@Tag(name = "Mentorship controller", description = "Контроллер для управления менторством")
@ApiResponse(description = "Успешная отработка метода", responseCode = "200")
@ApiResponse(description = "Ошибка клиента", responseCode = "400")
@ApiResponse(description = "Ошибка сервера", responseCode = "500")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @Operation (
            summary = "Получение ментис ментора",
            description = "Получение всех ментис у ментора с помощью айди ментора"
    )
    @GetMapping("/mentees/{mentorId}")
    public List<UserDto> getMentees(@PathVariable long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @Operation (
            summary = "Получение ментторов ментис",
            description = "Получение всех менторов у ментис с помощью айди ментора"
    )
    @GetMapping("/mentors/{menteeId}")
    public List<UserDto> getMentors(@PathVariable long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @Operation (
            summary = "Удаление ментис",
            description = "Удаление ментис у ментора используя ментии айди и ментор айди"
    )
    @DeleteMapping("/{mentorId}/mentor/{menteeId}")
    public void deleteMentee(@PathVariable("menteeId") long menteeId, @PathVariable("mentorId") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Operation (
            summary = "Удаление ментора",
            description = "Удаление ментора у ментис используя ментии айди и ментор айди"
    )
    @DeleteMapping("/{menteeId}/mentee/{mentorId}")
    public void deleteMentor(@PathVariable("menteeId") long menteeId,@PathVariable("mentorId") long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
