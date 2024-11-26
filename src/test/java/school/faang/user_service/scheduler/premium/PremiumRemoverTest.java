package school.faang.user_service.scheduler.premium;

import org.apache.commons.collections4.ListUtils;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumRemoverTest {

    @InjectMocks
    private PremiumRemover premiumRemover;

    @Mock
    private PremiumRepository premiumRepository;

    @Captor
    private ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

    private int updaterBatchSize = 50;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(premiumRemover, "updaterBatchSize", updaterBatchSize);
    }

    @Test
    void testToRemoveExpiredPremiums_ShouldSuccessRemove() {
        List<Premium> premiums = Arrays.asList(
                Premium.builder().id(1L).build(),
                Premium.builder().id(2L).build(),
                Premium.builder().id(3L).build(),
                Premium.builder().id(4L).build(),
                Premium.builder().id(5L).build()
        );
        when(premiumRepository.findAllByEndDateBefore(timeCaptor.capture()))
                .thenReturn(premiums);

        premiumRemover.removeExpiredPremiums();

        for(List<Premium> premiumList : ListUtils.partition(premiums, updaterBatchSize)) {
            verify(premiumRepository, times(1))
                    .deleteAllInBatch(eq(premiumList));
        }
    }

    @Test
    void testToRemoveExpiredPremiums_ShouldFiled() {
        when(premiumRepository.findAllByEndDateBefore(timeCaptor.capture()))
                .thenReturn(null);

        premiumRemover.removeExpiredPremiums();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(timeCaptor.getValue());
        verify(premiumRepository, never()).findAllByEndDateBefore(null);
    }
}
