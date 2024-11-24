package school.faang.user_service.controller.promotion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.promotion.EventPromotionResponseDto;
import school.faang.user_service.dto.promotion.PromotedEventResponseDto;
import school.faang.user_service.dto.promotion.RequestPromotionDto;
import school.faang.user_service.dto.promotion.UserPromotionResponseDto;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/promotions")
@Tag(name = "Promotion Controller", description = "Controller for managing promotions for users and events")
@ApiResponse(responseCode = "201", description = "Promotion purchased successfully")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Event not found")
@ApiResponse(responseCode = "500", description = "Server error")
public class PromotionController {

    private final PromotionService promotionService;
    private final UserContext userContext;

    @Operation(
            summary = "Buy user promotion",
            description = "Allows a user to purchase a promotion package for their profile."
    )
    @PostMapping("/users/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public UserPromotionResponseDto buyUserPromotion(@RequestBody RequestPromotionDto buyPromotionDto) {
        PromotionTariff tariff = PromotionTariff.fromViews(buyPromotionDto.numberOfViews());
        long userId = userContext.getUserId();
        return promotionService.buyUserPromotion(userId, tariff);
    }

    @Operation(
            summary = "Buy event promotion",
            description = "Allows a user to purchase a promotion package for a specific event."
    )
    @PostMapping("/events/{id}/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public EventPromotionResponseDto buyEventPromotion(
            @PathVariable(name = "id") @NotNull(message = "Event ID must not be null") Long eventId,
            @RequestBody RequestPromotionDto buyPromotionDto) {
        PromotionTariff tariff = PromotionTariff.fromViews(buyPromotionDto.numberOfViews());
        long userId = userContext.getUserId();
        return promotionService.buyEventPromotion(userId, eventId, tariff);
    }

    @Operation(
            summary = "Get promoted users per page",
            description = "Retrieve a paginated list of promoted users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Promoted users retrieved successfully")
            }
    )
    @GetMapping("/users/per-page")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getPromotedUsersBeforeAllPerPage(
            @RequestParam(name = "offset") @Min(value = 0, message = "Offset must be a non-negative number") int offset,
            @RequestParam(name = "limit") @Min(value = 1, message = "Limit must be at least 1") int limit) {
        return promotionService.getPromotedUsersBeforeAllPerPage(offset, limit);
    }

    @Operation(
            summary = "Get promoted events per page",
            description = "Retrieve a paginated list of promoted events.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Promoted events retrieved successfully")
            }
    )
    @GetMapping("/events/per-page")
    @ResponseStatus(HttpStatus.OK)
    public List<PromotedEventResponseDto> getPromotedEventsBeforeAllPerPage(
            @RequestParam(name = "offset") @Min(value = 0, message = "Offset must be a non-negative number") int offset,
            @RequestParam(name = "limit") @Min(value = 1, message = "Limit must be at least 1") int limit) {
        return promotionService.getPromotedEventsBeforeAllPerPage(offset, limit);
    }
}
