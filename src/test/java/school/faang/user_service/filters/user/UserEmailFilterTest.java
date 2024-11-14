package school.faang.user_service.filters.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserEmailFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserEmailFilterTest {
    private final UserEmailFilter userEmailFilter = new UserEmailFilter();
    private List<User> usersStream;

    @BeforeEach
    public void initFilter() {
        usersStream = List.of(
                User.builder()
                        .email("yandex@gmail.com")
                        .build(),
                User.builder()
                        .email("dmtry@gmail.com")
                        .build(),
                User.builder()
                        .email("augsgipspb@mail.ru")
                        .build()
        );
    }

    @Test
    public void testReturnFalseIfFilterIsNotApplicable() {
        UserFilterDto filters = new UserFilterDto();

        boolean isApplicable = userEmailFilter.isApplicable(filters);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnTrueIfFilterIsApplicable() {
        UserFilterDto filters = UserFilterDto.builder()
                .emailPattern(".com")
                .build();

        boolean isApplicable = userEmailFilter.isApplicable(filters);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .emailPattern("com")
                .build();
        List<User> expectedUsers = List.of(
                User.builder()
                        .email("yandex@gmail.com")
                        .build(),
                User.builder()
                        .email("dmtry@gmail.com")
                        .build()
        );

        Stream<User> actualUsers = userEmailFilter.apply(usersStream.stream(), filters);

        assertEquals(expectedUsers, actualUsers.toList());
    }
}


