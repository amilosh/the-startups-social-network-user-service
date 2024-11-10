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

public class UserNameFilterTest {
    private final UserNameFilter userNameFilter = new UserNameFilter();
    private List<User> usersStream;

    @BeforeEach
    public void initFilter() {
        usersStream = List.of(
                User.builder()
                        .username("Richard Maclaners")
                        .build(),
                User.builder()
                        .username("Gray Forester")
                        .build(),
                User.builder()
                        .username("Johnson Richman")
                        .build()
        );
    }

    @Test
    public void testReturnFalseIfFilterIsNotApplicable() {
        UserFilterDto filters = new UserFilterDto();

        boolean isApplicable = userNameFilter.isApplicable(filters);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnTrueIfFilterIsApplicable() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePattern("Richard")
                .build();

        boolean isApplicable = userNameFilter.isApplicable(filters);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePattern("Rich")
                .build();
        List<User> expectedUsers = List.of(
                User.builder()
                        .username("Richard Maclaners")
                        .build(),
                User.builder()
                        .username("Johnson Richman")
                        .build()
        );

        Stream<User> actualUsers = userNameFilter.apply(usersStream.stream(), filters);

        assertEquals(expectedUsers, actualUsers.toList());
    }
}
