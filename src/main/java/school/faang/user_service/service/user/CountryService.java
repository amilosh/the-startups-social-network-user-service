package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Country getOrCreateCountry(String title) {
        return countryRepository.findByTitle(title)
                .orElseGet(() -> {
                    Country country = new Country();
                    country.setTitle(title);
                    return countryRepository.save(country);
                });
    }

}
