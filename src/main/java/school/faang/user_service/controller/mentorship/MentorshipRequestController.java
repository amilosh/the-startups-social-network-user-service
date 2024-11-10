package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exception.mentorship_request.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/mentorshipRequest")
@Tag(name = "Mentorship Request", description = "Контроллер для запроса на менторство")
@ApiResponse(description = "Успешная отработка метода", responseCode = "200")
@ApiResponse(description = "Ошибка клиента", responseCode = "400")
@ApiResponse(description = "Ошибка сервера", responseCode = "500")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @Operation(
            summary = "Запрос на менторство",
            description = "Отправка запроса на менторство другому пользователю"
    )
    @PostMapping
    public void requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        validation(mentorshipRequestDto);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @Operation(
            summary = "Получение всех запросов на менторство",
            description = "Получение всех запросов на менторство используя фильтры"
    )
    @PostMapping("/requests")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @Operation(
            summary = "Принять запрос на менторство",
            description = "Возможность принимать запрос на менторство, пришедший от другого пользователя"
    )
    @PutMapping("/accept/{mentorshipRequestId}")
    public void acceptRequest(@PathVariable long mentorshipRequestId) {
        mentorshipRequestService.acceptRequest(mentorshipRequestId);
    }

    @Operation(
            summary = "Отклонить запрос на менторство",
            description = "Возможность пользователя отклонять запрос на менторство"
    )
    @PutMapping("/reject/{mentorshipRequestId}")
    public void rejectRequest(@PathVariable long mentorshipRequestId,@RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(mentorshipRequestId, rejection);
    }

    private void validation(MentorshipRequestDto recommendationDto) {
        if (recommendationDto.getDescription() == null || recommendationDto.getDescription().isBlank()) {
            throw new DataValidationException("A request for mentorship must contain a reason");
        }
    }

}
