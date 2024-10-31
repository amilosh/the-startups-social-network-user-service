package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Tag(name = "MentorshipRequests")
@RestController
@RequestMapping("/api/mentorship/requests")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto creationRequestDto) {
        return mentorshipRequestService.requestMentorship(creationRequestDto);
    }

    @GetMapping
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filterDto) {
        return mentorshipRequestService.getRequests(filterDto);
    }
}