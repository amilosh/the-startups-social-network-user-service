package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.RequestPremiumDto;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequestMapping("api/v1/premiums")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;
    private final UserContext userContext;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsePremiumDto buyPremium(@RequestBody RequestPremiumDto requestPremiumDto) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(requestPremiumDto.days());
        long userId = userContext.getUserId();
        return premiumService.buyPremium(userId, premiumPeriod);
    }
}
