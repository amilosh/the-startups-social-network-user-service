package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PremiumCleanerService premiumCleanerService;
    @InjectMocks
    private PremiumService premiumService;
    @Captor
    private ArgumentCaptor<List<Premium>> captor;

    private Premium premium1;
    private Premium premium2;
    private Premium premium3;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(premiumService, "batchSize", 2);
        time = LocalDateTime.now();
        premium1 = mock(Premium.class);
        premium2 = mock(Premium.class);
        premium3 = mock(Premium.class);
    }

    @Test
    void testDeleteAllPremium_successfulDeletion() {
        when(premiumRepository.findAllByEndDateBefore(time))
                .thenReturn(Arrays.asList(premium1, premium2, premium3));

        premiumService.deleteExpiredPremiums(time);

        verify(premiumRepository, times(1)).findAllByEndDateBefore(time);
        verify(premiumCleanerService, times(2)).deletePremium(captor.capture());
        List<List<Premium>> capturedBatches = captor.getAllValues();

        assertEquals(2, capturedBatches.size());
        assertEquals(Arrays.asList(premium1, premium2), capturedBatches.get(0));
        assertEquals(Collections.singletonList(premium3), capturedBatches.get(1));
    }

    @Test
    void deleteAllPremiumWithEmptyListShouldNotCallDelete() {
        LocalDateTime mockTime = LocalDateTime.now();
        when(premiumRepository.findAllByEndDateBefore(mockTime)).thenReturn(List.of());

        premiumService.deleteExpiredPremiums(mockTime);

        verify(premiumCleanerService, never()).deletePremium(anyList());
    }
}