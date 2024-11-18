package school.faang.user_service.filters.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserAboutFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAboutFilterTest {
    private final UserAboutFilter userAboutFilter = new UserAboutFilter();
    private List<User> usersStream;

    @BeforeEach
    public void initFilter() {
        usersStream = List.of(
                User.builder()
                        .aboutMe("Comic book fan.")
                        .build(),
                User.builder()
                        .aboutMe("I play hockey")
                        .build(),
                User.builder()
                        .aboutMe("I attend comic book conventions")
                        .build()
        );
    }

    @Test
    public void testReturnFalseIfFilterIsNotApplicable() {
        UserFilterDto filters = new UserFilterDto();

        boolean isApplicable = userAboutFilter.isApplicable(filters);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnTrueIfFilterIsApplicable() {
        UserFilterDto filters = UserFilterDto.builder()
                .aboutPattern("Comic")
                .build();

        boolean isApplicable = userAboutFilter.isApplicable(filters);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .aboutPattern("Comic")
                .build();
        List<User> expectedUsers = List.of(
                User.builder()
                        .aboutMe("Comic book fan.")
                        .build(),
                User.builder()
                        .aboutMe("I attend comic book conventions")
                        .build()
        );

        Stream<User> actualUsers = userAboutFilter.apply(usersStream.stream(), filters);

        assertEquals(expectedUsers, actualUsers.toList());
    }
}

