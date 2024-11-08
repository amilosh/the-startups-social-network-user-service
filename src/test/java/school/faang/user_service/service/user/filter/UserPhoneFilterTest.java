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

public class UserPhoneFilterTest {
    private final UserPhoneFilter userPhoneFilter = new UserPhoneFilter();
    private List<User> usersStream;

    @BeforeEach
    public void initFilter() {
        usersStream = List.of(
                User.builder()
                        .phone("+7-922-111-05-00")
                        .build(),
                User.builder()
                        .phone("+7-800-555-35-35")
                        .build(),
                User.builder()
                        .phone("+7-987-636-28-19")
                        .build()
        );
    }

    @Test
    public void testReturnFalseIfFilterIsNotApplicable() {
        UserFilterDto filters = new UserFilterDto();

        boolean isApplicable = userPhoneFilter.isApplicable(filters);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnTrueIfFilterIsApplicable() {
        UserFilterDto filters = UserFilterDto.builder()
                .phonePattern("+7")
                .build();

        boolean isApplicable = userPhoneFilter.isApplicable(filters);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFilteredUserList() {
        UserFilterDto filters = UserFilterDto.builder()
                .phonePattern("+7")
                .build();
        List<User> expectedUsers = List.of(
                User.builder()
                        .phone("+7-922-111-05-00")
                        .build(),
                User.builder()
                        .phone("+7-800-555-35-35")
                        .build(),
                User.builder()
                        .phone("+7-987-636-28-19")
                        .build()
        );

        Stream<User> actualUsers = userPhoneFilter.apply(usersStream.stream(), filters);

        assertEquals(expectedUsers, actualUsers.toList());
    }
}



