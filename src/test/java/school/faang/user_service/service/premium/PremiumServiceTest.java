package school.faang.user_service.service.premium;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.scheduler.premium.ListPartitioner;
import school.faang.user_service.scheduler.premium.PremiumRemover;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PremiumServiceTest {

    @InjectMocks
    private PremiumRemover premiumRemover;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private ListPartitioner listPartitioner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(premiumRemover, "batchSize", 2); // Set batchSize manually
    }

    @Test
    void removeExpiredPremium_shouldCallDeleteAllOnRepository() {
        Premium premium1 = new Premium();
        Premium premium2 = new Premium();
        List<Premium> expiredPremiums = Arrays.asList(premium1, premium2);
        List<List<Premium>> partitionedList = Collections.singletonList(expiredPremiums);

        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(expiredPremiums);
        when(listPartitioner.partition(expiredPremiums, 2)).thenReturn(partitionedList);

        premiumRemover.removeExpiredPremium();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
        verify(premiumRepository, times(1)).deleteAll(expiredPremiums);
    }

    @Test
    void removeExpiredPremium_shouldThrowEntityNotFoundException_whenNoExpiredPremiums() {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> premiumRemover.removeExpiredPremium());

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
        assertEquals(EntityNotFoundException.class, exception.getClass());
    }
}
