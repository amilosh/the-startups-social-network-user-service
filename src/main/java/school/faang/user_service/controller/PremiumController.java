package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@RestController
@RequestMapping("/premiums")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("user/{userId}/{days}")
    public ResponseEntity<PremiumDto> buyPremium(@PathVariable @Positive long userId,
                                                 @PathVariable @Positive int days) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        return ResponseEntity.ok(premiumService.buyPremium(userId, premiumPeriod));
    }
}
