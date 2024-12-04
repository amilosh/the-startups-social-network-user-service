package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.UserPremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.publisher.PremiumBoughtPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {

    private final static String USER_NOT_FOUND_MESSAGE = "User not found";
    private final static String PREMIUM_ALREADY_EXISTS_MESSAGE = "Already have premium period";

    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final UserService userService;
    private final PremiumMapper premiumMapper;
    private final PremiumBoughtPublisher premiumBoughtPublisher;

    public ResponseEntity<PremiumDto> buyPremium(PremiumDto premiumDto, UserPremiumPeriod userPremiumPeriod) {
        User user = userService.findById(premiumDto.getUserId())
                .orElseThrow(() -> {
                    log.error(USER_NOT_FOUND_MESSAGE);
                    return new IllegalArgumentException(USER_NOT_FOUND_MESSAGE);
                });
        existsByUserId(premiumDto.getUserId());

        PaymentRequest paymentRequest = createPaymentRequest(premiumDto, userPremiumPeriod);
        log.info("sending request to payment service api {}", paymentRequest.toString());
        ResponseEntity<PaymentResponse> paymentResponse = paymentServiceClient.sendPayment(paymentRequest);
        log.info("response {} from payment service to request {}", paymentResponse.toString(), paymentRequest);

        if (paymentResponse.getStatusCode().equals(HttpStatus.OK)
                && Objects.requireNonNull(paymentResponse.getBody()).status().equals(PaymentStatus.SUCCESS)) {

            Premium premium = createPremiumEntity(user, userPremiumPeriod);
            premiumRepository.save(premium);
            log.info("premium entity (entityID:{}) saved success", premium.getId());
            PremiumDto savedPremiumDto = premiumMapper.toDto(premium);
            savedPremiumDto.setDays(premiumDto.getDays());
            savedPremiumDto.setCurrency(premiumDto.getCurrency());
            savedPremiumDto.setMessage(paymentResponse.getBody().message());
            premiumBoughtPublisher.publish(new PremiumBoughtEvent(
                    premium.getId(),
                    premium.getUser().getId(),
                    premium.getPremiumType(),
                    premium.getStartDate(),
                    premium.getEndDate()
            ));
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPremiumDto);
        }
        log.error("payment wasn't success, cause: {}", paymentResponse.getBody().message());
        throw new PaymentFailedException(paymentResponse.getBody().message());
    }

    private PaymentRequest createPaymentRequest(PremiumDto premiumDto, UserPremiumPeriod userPremiumPeriod) {
        return PaymentRequest.builder()
                .paymentNumber(premiumDto.getUserId())
                .amount(userPremiumPeriod.getAmount())
                .currency(premiumDto.getCurrency())
                .build();
    }

    public boolean existsByUserId(Long userId) {
        boolean isExists = premiumRepository.existsByUserId(userId);
        if (isExists) {
            log.error(PREMIUM_ALREADY_EXISTS_MESSAGE);
            throw new IllegalArgumentException(PREMIUM_ALREADY_EXISTS_MESSAGE);
        }
        return true;
    }

    private Premium createPremiumEntity(User user, UserPremiumPeriod userPremiumPeriod) {
        LocalDateTime now = LocalDateTime.now();
        return Premium.builder()
                .user(user)
                .premiumType(userPremiumPeriod.getPremiumType())
                .startDate(now)
                .endDate(now.plusDays(userPremiumPeriod.getDays()))
                .build();
    }
}
