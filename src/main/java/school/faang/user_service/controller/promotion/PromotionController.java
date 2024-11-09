package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.promotion.EventPromotionResponseDto;
import school.faang.user_service.dto.promotion.RequestPromotionDto;
import school.faang.user_service.dto.promotion.UserPromotionResponseDto;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.service.promotion.PromotionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotions")
public class PromotionController {
    private final PromotionService promotionService;
    private final UserContext userContext;

    @PostMapping("/buy")
    public UserPromotionResponseDto buyPromotion(@RequestBody RequestPromotionDto buyPromotionDto) {
        PromotionTariff tariff = PromotionTariff.fromViews(buyPromotionDto.numberOfViews());
        long userId = userContext.getUserId();
        return promotionService.buyPromotion(userId, tariff);
    }

    @PostMapping("/events/{id}/buy")
    public EventPromotionResponseDto buyEventPromotion(@PathVariable(name = "id") long eventId,
                                                       @RequestBody RequestPromotionDto buyPromotionDto) {
        PromotionTariff tariff = PromotionTariff.fromViews(buyPromotionDto.numberOfViews());
        long userId = userContext.getUserId();
        return promotionService.buyEventPromotion(userId, eventId, tariff);
    }
}