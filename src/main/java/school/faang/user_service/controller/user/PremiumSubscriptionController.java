package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.service.user.PremiumSubscriptionService;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/premium-subscription")
@RequiredArgsConstructor
public class PremiumSubscriptionController {

    private final PremiumSubscriptionService premiumSubscriptionService;

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> buyPremium(
            @RequestParam Long userId,
            @RequestParam Double amount,
            @RequestParam Integer duration
    ) {
        log.info("Received request to purchase premium subscription. User ID: {}, Amount: {}, Duration: {} days",
                userId, amount, duration);

        premiumSubscriptionService.buyPremium(userId, amount, duration);

        return ResponseEntity.status(HttpStatus.CREATED).body("Premium subscription purchased successfully.");
    }
}