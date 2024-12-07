package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.service.country.CountryService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    private Country country;

    @BeforeEach
    public void setUp() {
        country = new Country();
        country.setId(1L);
        country.setTitle("Test Country");
    }

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

    @Test
    public void getCountry_ShouldReturnCountry_WhenCountryExists() {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));

        Country result = countryService.getCountryById(1L);

        assertEquals(country, result);

        verify(countryRepository).findById(1L);
    }

    @Test
    public void getCountryShouldThrowEntityNotFoundExceptionWhenCountryDoesNotExist() {
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                ()->countryService.getCountryById(1));

        verify(countryRepository).findById(1L);
    }
}