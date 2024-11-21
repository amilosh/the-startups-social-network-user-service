package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public boolean isCountryExistsByTitle(String title) {
        return countryRepository.findByTitle(title)
                .stream()
                .anyMatch(country -> title.equalsIgnoreCase(country.getTitle()));
    }

    public Country getCountryByTitle(String title) {
        return countryRepository.findByTitle(title).orElseThrow(
                () -> new EntityNotFoundException("Entity not found with title : " + title));
    }

    public Country addNewCountry(String title) {
        Country country = new Country();
        country.setTitle(title);
        return countryRepository.save(country);
    }

}
