package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.service.premium.PremiumService;

import java.util.List;

@RestController
@RequestMapping("/buy-premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @GetMapping("/getActivePremium")
    public List<PremiumDto> getActivePremium(){
        return premiumService.getActivePremium();
    }

    @PutMapping("/updatePremium")
    public List<PremiumDto> updatePremium(@RequestBody List<PremiumDto> premiums){
        return premiumService.updatePremium(premiums);
    }
}
