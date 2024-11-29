package school.faang.user_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PremiumCleanerServiceTest {
    @Mock
    private PremiumRepository repository;
    private ThreadPoolTaskExecutor taskExecutor;
    @InjectMocks
    private PremiumCleanerService cleanerService;

    @BeforeEach
    void setUp() {
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.initialize();
    }
    @AfterEach
    void tearDown() {
        if (taskExecutor != null) {
            taskExecutor.shutdown();
        }
    }

    @Test
    void deleteAllPremiums() throws InterruptedException {
        List<Premium> premiumList = List.of(new Premium(), new Premium(), new Premium());

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> cleanerService.deletePremium(premiumList));
        future.join();

        ArgumentCaptor<Premium> captor = ArgumentCaptor.forClass(Premium.class);
        verify(repository, times(3)).delete(captor.capture());

        List<Premium> capturedPremiums = captor.getAllValues();
        assert capturedPremiums.size() == premiumList.size();
        assert capturedPremiums.containsAll(premiumList);
    }

    @Test
    void deletePremiumwithEmptyListShouldNotCallRepository() {
        cleanerService.deletePremium(List.of());

        verify(repository, never()).delete(any());
    }
}