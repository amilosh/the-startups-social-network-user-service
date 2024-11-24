package school.faang.user_service.controller.premium;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.RequestPremiumDto;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequestMapping("api/v1/premiums")
@RequiredArgsConstructor
@Tag(name = "Premium Controller", description = "Controller for managing premium subscriptions")
@ApiResponse(responseCode = "201", description = "Premium subscription successfully purchased")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "500", description = "Server error")
public class PremiumController {
    private final PremiumService premiumService;
    private final UserContext userContext;

    @Operation(
            summary = "Buy premium subscription",
            description = "Allows a user to purchase a premium subscription for a specified number of days."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsePremiumDto buyPremium(@Valid @RequestBody RequestPremiumDto requestPremiumDto) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(requestPremiumDto.days());
        long userId = userContext.getUserId();
        return premiumService.buyPremium(userId, premiumPeriod);
    }
}
