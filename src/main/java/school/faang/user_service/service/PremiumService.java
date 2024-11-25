package school.faang.user_service.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.ServiceConnectionFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserService userService;
    private final UserContext userContext;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;

    @Retryable(
            retryFor = {FeignException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000, multiplier = 2)
    )
    public PremiumDto buyPremium(PremiumPeriod premiumPeriod) {
        long userId = userContext.getUserId();

        if (premiumRepository.existsByUserId(userId)) {
            throw new IllegalStateException(String.format("user %s already buy premium", userId));
        }

        PaymentRequest paymentRequest = new PaymentRequest(
                premiumPeriod.getDays(),
                BigDecimal.valueOf(premiumPeriod.getPrice()),
                premiumPeriod.getCurrency()
        );

        ResponseEntity<PaymentResponse> response = paymentServiceClient.sendPayment(paymentRequest);

        PaymentResponse body = response.getBody();

        if (body == null) {
            throw new RestClientException("Payment failed");
        }

        if (body.status().equals(PaymentStatus.SUCCESS)) {
            Premium newPremium = new Premium();
            newPremium.setUser(userService.getUserById(userId));
            newPremium.setStartDate(LocalDateTime.now());
            newPremium.setEndDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()));
            newPremium.setPremiumPeriod(premiumPeriod);
            newPremium.setActive(true);

            return premiumMapper.toDto(premiumRepository.save(newPremium));
        }

        return null;
    }

    @Recover
    public PremiumDto recover(FeignException e) {
        throw new ServiceConnectionFailedException("error connecting to payment service");
    }
}
