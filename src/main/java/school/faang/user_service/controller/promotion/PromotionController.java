package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.promotion.EventPromotionResponseDto;
import school.faang.user_service.dto.promotion.PromotedEventResponseDto;
import school.faang.user_service.dto.promotion.RequestPromotionDto;
import school.faang.user_service.dto.promotion.UserPromotionResponseDto;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotions")
public class PromotionController {
    private final PromotionService promotionService;
    private final UserContext userContext;

    @PostMapping("/buy")
    public UserPromotionResponseDto buyUserPromotion(@RequestBody RequestPromotionDto buyPromotionDto) {
        PromotionTariff tariff = PromotionTariff.fromViews(buyPromotionDto.numberOfViews());
        long userId = userContext.getUserId();
        return promotionService.buyUserPromotion(userId, tariff);
    }

    @PostMapping("/events/{id}/buy")
    public EventPromotionResponseDto buyEventPromotion(@PathVariable(name = "id") long eventId,
                                                       @RequestBody RequestPromotionDto buyPromotionDto) {
        PromotionTariff tariff = PromotionTariff.fromViews(buyPromotionDto.numberOfViews());
        long userId = userContext.getUserId();
        return promotionService.buyEventPromotion(userId, eventId, tariff);
    }

    @GetMapping("/users/per-page")
    public List<UserResponseDto> getPromotedUsersBeforeAllPerPage(@RequestParam(name = "offset") int offset,
                                                                  @RequestParam(name = "limit") int limit) {
        return promotionService.getPromotedUsersBeforeAllPerPage(offset, limit);
    }

    @GetMapping("/events/per-page")
    public List<PromotedEventResponseDto> getPromotedEventsBeforeAllPerPage(@RequestParam(name = "offset") int offset,
                                                                            @RequestParam(name = "limit") int limit) {
        return promotionService.getPromotedEventsBeforeAllPerPage(offset, limit);
    }
}