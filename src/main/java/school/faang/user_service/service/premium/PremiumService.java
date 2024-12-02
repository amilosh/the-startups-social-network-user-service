package school.faang.user_service.service.premium;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.PremiumServiceValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final UserContext userContext;
    private final PaymentServiceClient paymentServiceClient;
    private final UserService userService;

    public List<PremiumDto> getActivePremium() {
        log.info("Getting list of active premium from repository");
        return premiumRepository.getAllActivePremium().stream()
                .map(premiumMapper::toDto)
                .toList();
    }

    public List<PremiumDto> updatePremium(List<PremiumDto> premiums) {
        log.info("validate argument for null exception");
        PremiumServiceValidator.checkListForNull(premiums);

        log.info("updating premium");
        List<Premium> updatedPremium = new ArrayList<>();
        premiums.forEach(premiumDto -> {
            Premium premium = getPremium(premiumDto.getId());
            premiumMapper.update(premiumDto, premium);
            save(premium);
            updatedPremium.add(premium);
        });

        return updatedPremium.stream()
                .map(premiumMapper::toDto)
                .toList();
    }

    private Premium getPremium(long id) {
        log.info("trying to get premium entity from db");
        return premiumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
    private void save(Premium premium){
        log.info("check argument for null exception");
        PremiumServiceValidator.checkPremiumNotNull(premium);
        premiumRepository.save(premium);
    }
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
            newPremium.setUser(userService.getUserById(userId));
            newPremium.setStartDate(LocalDateTime.now());
            newPremium.setEndDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()));

            return premiumMapper.toDto(premiumRepository.save(newPremium));
        }
        return null;
    }
}
