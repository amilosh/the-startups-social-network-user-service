package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@Validated
@RequiredArgsConstructor
@RestController
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("/premium/buy")
    public PremiumDto buyPremium(@Positive @RequestParam int days,
                                 @Positive @RequestBody long id) {
        PremiumPeriod premPeriod = PremiumPeriod.fromDays(days);
        return premiumService.buyPremium(premPeriod, id);
    }
}
