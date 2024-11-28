package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumValidator premiumValidator;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private UserService userService;

    @Spy
    private PremiumMapper premiumMapper;

    @InjectMocks
    private PremiumService premiumService;


}
