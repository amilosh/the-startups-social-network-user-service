package school.faang.user_service.controller.user;

import school.faang.user_service.service.user.SubscriptionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/premium-subscription")
public class PremiumSubscriptionController {

    private final SubscriptionService subscriptionService;

    public PremiumSubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/buy")
    public void buyPremium(
            @RequestParam Long userId,
            @RequestParam Double amount,
            @RequestParam Integer duration
    ) {

        subscriptionService.buyPremium(userId, amount, duration);
    }
}
