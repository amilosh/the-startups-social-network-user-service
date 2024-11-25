package school.faang.user_service.controller.mentorshiprequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;

import java.util.List;

@Tag(name = "Mentorship request", description = "запрос на менторство (отправка, принятие, отклонение)")
public interface MentorshipRequestControllerOas {
    @Operation(summary = "Создать запрос", description = "Необходимо передать отправителя, получателя и описание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "отправителя или получателя не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    @Operation(summary = "Получить запросы с фильтрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное получение запросов")
    })
    List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto);

    @Operation(summary = "Принять запрос")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное принятие запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "запроса не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    MentorshipRequestDto acceptRequest(long requestId);

    @Operation(summary = "Отклонить запрос")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное отклонение запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "запроса не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    MentorshipRequestDto rejectRequest(long requestId, RejectionDto rejectionDto);
}