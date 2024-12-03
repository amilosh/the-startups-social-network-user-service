package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {
    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private CountryService countryService;

    private String countryName;
    private Country existingCountry;
    private Country newCountry;

    @BeforeEach
    public void setUp() {
        countryName = "Country1";
        existingCountry = new Country();
        newCountry = new Country();
        newCountry.setTitle(countryName);
    }

    @Test
    void testFindOrCreateCountryWhenCountryExists() {
        when(countryRepository.findByTitle(countryName)).thenReturn(Optional.of(existingCountry));

        Country result = countryService.findOrCreateCountry(countryName);

        assertNotNull(result);
        assertEquals(existingCountry, result);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void testFindOrCreateCountry_WhenCountryDoesNotExist() {

        Country newCountry = new Country();
        newCountry.setTitle(countryName);

        when(countryRepository.findByTitle(countryName)).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(newCountry);

        Country result = countryService.findOrCreateCountry(countryName);


        assertNotNull(result);
        assertEquals(newCountry, result);
        verify(countryRepository).save(newCountry);
    }
}

