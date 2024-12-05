package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import school.faang.user_service.service.user.PremiumSubscriptionService;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/premium-subscription")

public class PremiumSubscriptionController {

    private final PremiumSubscriptionService premiumSubscriptionService;

    public PremiumSubscriptionController(PremiumSubscriptionService premiumSubscriptionService) {
        this.premiumSubscriptionService = premiumSubscriptionService;
    }

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public void buyPremium(
            @RequestParam Long userId,
            @RequestParam Double amount,
            @RequestParam Integer duration
    ) {
        log.info("Received request to purchase premium subscription. User ID: {}, Amount: {}, Duration: {} days",
                userId, amount, duration);

        premiumSubscriptionService.buyPremium(userId, amount, duration);
    }
}
