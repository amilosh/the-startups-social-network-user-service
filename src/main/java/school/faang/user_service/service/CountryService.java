package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Country getCountryOrCreateByName(User user) {
        String countryTitle = user.getCountry().getTitle();
        return countryRepository.findByTitleIgnoreCase(countryTitle)
                .orElseGet(() -> countryRepository.save(Country.builder()
                        .title(countryTitle)
                        .residents(List.of(user))
                        .build()));
    }
}
