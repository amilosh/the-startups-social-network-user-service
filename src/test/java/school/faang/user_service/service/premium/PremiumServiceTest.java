package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.premium.PremiumMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    @InjectMocks
    private PremiumService premiumService;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Mock
    private PremiumRepository premiumRepository;

    private PremiumDto firstPremium;

    private Premium secondPremium;

    @BeforeEach
    void setUp() {
        firstPremium = new PremiumDto();
        firstPremium.setActive(false);
        firstPremium.setId(2L);

        secondPremium = new Premium();
        secondPremium.setId(2);
        secondPremium.setActive(true);
        secondPremium.setStartDate(LocalDateTime.of(2024, 10, 20, 15, 30));
        secondPremium.setEndDate(LocalDateTime.of(2024, 11, 20, 15, 30));
    }

    @Test
    void testSuccessfulGetActivePremium() {
        when(premiumRepository.getAllActivePremium())
                .thenReturn(new ArrayList<>(List.of(secondPremium)));

        List<PremiumDto> premiumDtos = premiumService.getActivePremium();

        verify(premiumRepository).getAllActivePremium();

        PremiumDto result = premiumDtos.get(0);
        assertEquals(premiumDtos.size(), 1);
        assertEquals(result.getId(), secondPremium.getId());
        assertEquals(result.getStartDate(), secondPremium.getStartDate());
        assertEquals(result.getEndDate(), secondPremium.getEndDate());
        assertEquals(result.isActive(), secondPremium.isActive());
    }

    @Test
    void testNullListForUpdatePremium(){
        assertThrows(IllegalArgumentException.class,
                ()-> premiumService.updatePremium(null));
    }

    @Test
    void testSuccessfulUpdatePremium() {
        when(premiumRepository.findById(2L)).thenReturn(Optional.of(secondPremium));

        List<PremiumDto> premiumDtos = premiumService.updatePremium(List.of(firstPremium));

        verify(premiumRepository).save(secondPremium);

        PremiumDto result = premiumDtos.get(0);
        assertEquals(premiumDtos.size(), 1);
        assertEquals(result.getId(), firstPremium.getId());
        assertEquals(result.isActive(), firstPremium.isActive());
    }
}