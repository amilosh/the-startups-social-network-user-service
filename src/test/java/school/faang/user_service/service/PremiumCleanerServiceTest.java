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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class PremiumCleanerServiceTest {
    @Mock
    private PremiumRepository repository;
    private ThreadPoolTaskExecutor taskExecutor;
    @InjectMocks
    private PremiumCleanerService cleanerService;

    private Premium premium1;
    private Premium premium2;
    private List<Premium> premiums;

    @BeforeEach
    void setUp() {
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.initialize();

        premium1 = Premium.builder()
                .id(1L)
                .build();
        premium2 = Premium.builder()
                .id(2L)
                .build();
         premiums = List.of(premium1, premium2);
    }
    @AfterEach
    void tearDown() {
        if (taskExecutor != null) {
            taskExecutor.shutdown();
        }
    }

    @Test
    void deletePremium_successfulDeletions() throws Exception {

        CompletableFuture<List<Premium>> future = cleanerService.deletePremium(premiums);

        List<Premium> failedPremiums = future.get();
        assertTrue(failedPremiums.isEmpty());


        verify(repository, times(1)).delete(premium1);
        verify(repository, times(1)).delete(premium2);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deletePremiumWithEmptyListShouldNotCallRepository() {

        cleanerService.deletePremium(List.of());

        verify(repository, never()).delete(any());
    }

    @Test
    void deletePremiumWithFailures() throws Exception {

        doThrow(new RuntimeException("Error deleting premium")).when(repository).delete(premium1);

        CompletableFuture<List<Premium>> future = cleanerService.deletePremium(premiums);

        List<Premium> failedPremiums = future.get();
        assertEquals(1, failedPremiums.size());
        assertEquals(premium1, failedPremiums.get(0));

        verify(repository, times(1)).delete(premium1);
        verify(repository, times(1)).delete(premium2);
        verifyNoMoreInteractions(repository);
    }
}