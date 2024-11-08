package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserService userService;
    private final UserContext userContext;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;

    public PremiumDto buyPremium(PremiumPeriod premiumPeriod) {
        long userId = userContext.getUserId();

        if (premiumRepository.existsByUserId(userId)) {
            throw new IllegalStateException(String.format("user %s already buy premium period %s", userId, premiumPeriod));
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
            newPremium.setUser(userService.findById(userId).orElseThrow(EntityNotFoundException::new));
            newPremium.setStartDate(LocalDateTime.now());
            newPremium.setEndDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()));

            return premiumMapper.toDto(premiumRepository.save(newPremium));
        }
        return null;
    }
}
