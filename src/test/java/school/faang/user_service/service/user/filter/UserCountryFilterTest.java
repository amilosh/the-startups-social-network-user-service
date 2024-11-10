package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCountryFilterTest {
    private final UserCountryFilter userCountryFilter = new UserCountryFilter();
    private List<User> usersStream;

    @BeforeEach
    public void initFilter() {
        Country germany = new Country();
        germany.setTitle("Germany");
        Country nigeria = new Country();
        nigeria.setTitle("Nigeria");
        Country argentina = new Country();
        argentina.setTitle("Argentina");
        usersStream = List.of(
                User.builder()
                        .country(germany)
                        .build(),
                User.builder()
                        .country(nigeria)
                        .build(),
                User.builder()
                        .country(argentina)
                        .build()
        );
    }

    @Test
    public void testReturnFalseIfFilterIsNotApplicable() {
        UserFilterDto filters = new UserFilterDto();

        boolean isApplicable = userCountryFilter.isApplicable(filters);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnTrueIfFilterIsApplicable() {
        UserFilterDto filters = UserFilterDto.builder()
                .countryPattern("GE")
                .build();

        boolean isApplicable = userCountryFilter.isApplicable(filters);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .countryPattern("GE")
                .build();
        List<User> expectedUsers = usersStream.stream().toList();

        Stream<User> actualUsers = userCountryFilter.apply(usersStream.stream(), filters);

        assertEquals(expectedUsers, actualUsers.toList());
    }
}



