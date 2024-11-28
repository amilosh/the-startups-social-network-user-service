package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserCountryFilter implements Filter<User, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getCountry() != null && !filter.getCountry().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        String countryPattern = filter.getCountry().toLowerCase();
        return users.filter(user -> user.getCountry() != null &&
                user.getCountry().getTitle() != null &&
                user.getCountry().getTitle().toLowerCase().contains(countryPattern));
    }
}
