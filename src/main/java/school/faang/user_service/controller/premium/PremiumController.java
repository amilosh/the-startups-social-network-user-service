package school.faang.user_service.controller.premium;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.UserPremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

@RequestMapping("/premium")
@RequiredArgsConstructor
@RestController
public class PremiumController {

    private final PremiumService premiumService;

    @PostMapping("/buy")
    public ResponseEntity<PremiumDto> buyPremium(@RequestBody @Valid PremiumDto premiumDto) {
        return premiumService.buyPremium(premiumDto,
                        UserPremiumPeriod.fromDays(premiumDto.getDays(), premiumDto.getPremiumType()));
    }

}
