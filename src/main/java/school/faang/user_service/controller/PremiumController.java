package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@RestController
@RequestMapping("/premiums")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("user/{userId}/days/{days}")
    public ResponseEntity<PremiumDto> buyPremium(@PathVariable @Positive long userId,
                                                 @PathVariable @Positive int days) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        return ResponseEntity.ok(premiumService.buyPremium(userId, premiumPeriod));
    }
}
