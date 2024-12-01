package school.faang.user_service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumCleanerService;
import school.faang.user_service.service.PremiumService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SchedulerPremiumRemoverTest {
    @Mock
    private PremiumService service;
    @Mock
    private PremiumRepository repository;
    @Mock
    private PremiumCleanerService cleanerService;
    @InjectMocks
    private SchedulerPremiumRemover remover;

    @Test
    void removePremiumServiceAndLogSuccess() {
        LocalDateTime mockTime = LocalDateTime.now();
        doNothing().when(service).deleteExpiredPremiums(any(LocalDateTime.class));

        remover.deleteExpiredPremiums();

        verify(service, times(1)).deleteExpiredPremiums(any(LocalDateTime.class));
    }

    @Test
    void removePremiumWhenPremiumServiceFails() {
        LocalDateTime mockTime = LocalDateTime.now();
        doThrow(new RuntimeException("Service failure"))
                .when(service).deleteExpiredPremiums(any(LocalDateTime.class));

        remover.deleteExpiredPremiums();

        verify(service, times(1)).deleteExpiredPremiums(any(LocalDateTime.class));
    }

}