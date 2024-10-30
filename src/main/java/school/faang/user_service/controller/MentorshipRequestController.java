package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @GetMapping
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping
    public MentorshipRequestDto createRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.createRequestMentorship(mentorshipRequestDto);
    }
}
