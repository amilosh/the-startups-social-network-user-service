package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCityFilterTest {
    private final UserCityFilter userCityFilter = new UserCityFilter();
    private List<User> usersStream;

    @BeforeEach
    public void initFilter() {
        usersStream = List.of(
                User.builder()
                        .city("Berlin")
                        .build(),
                User.builder()
                        .city("Madrid")
                        .build(),
                User.builder()
                        .city("Moscow")
                        .build()
        );
    }

    @Test
    public void testReturnFalseIfFilterIsNotApplicable() {
        UserFilterDto filters = new UserFilterDto();

        boolean isApplicable = userCityFilter.isApplicable(filters);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnTrueIfFilterIsApplicable() {
        UserFilterDto filters = UserFilterDto.builder()
                .cityPattern("M")
                .build();

        boolean isApplicable = userCityFilter.isApplicable(filters);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .cityPattern("M")
                .build();
        List<User> expectedUsers = List.of(
                User.builder()
                        .city("Madrid")
                        .build(),
                User.builder()
                        .city("Moscow")
                        .build()
        );

        Stream<User> actualUsers = userCityFilter.apply(usersStream.stream(), filters);

        assertEquals(expectedUsers, actualUsers.toList());
    }
}


