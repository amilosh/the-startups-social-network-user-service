package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public Country findOrCreateCountry(String countryName) {
        return countryRepository.findByTitle(countryName)
                .orElseGet(() -> {
                    Country newCountry = Country.builder()
                            .title(countryName)
                            .build();
                    newCountry.setTitle(countryName);
                    countryRepository.save(newCountry);
                    return newCountry;
                });
    }
}
