package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @Test
    void getOrCreateCountry_NotExists(){
        Mockito.lenient().when(countryRepository.existsByTitle(Mockito.anyString())).thenReturn(false);
        Mockito.lenient().when(countryRepository.save(Mockito.any(Country.class))).thenReturn(getCountry());
        assertEquals(getCountry(), countryService.getOrCreateCountry("Test"));
    }

    @Test
    void getOrCreateCountry_Exists(){
        Mockito.lenient().when(countryRepository.existsByTitle(Mockito.anyString())).thenReturn(true);
        Mockito.lenient().when(countryRepository.getByTitle(Mockito.anyString())).thenReturn(getCountry());
        assertEquals(getCountry(), countryService.getOrCreateCountry("Test"));
    }

    private Country getCountry(){
        return Country.builder().title("Test").build();
    }
}