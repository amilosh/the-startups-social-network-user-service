package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserPhoneFilterTest extends UserFilterTest {
    private final UserPhoneFilter userPhoneFilter = new UserPhoneFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(userPhoneFilter, filter.getPhonePattern(), "Ivan");
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(userPhoneFilter, filter.getPhonePattern(), null);
    }

    @Test
    public void testApplyWithMatching() {
        when(user.getPhone()).thenReturn("89123698");
        when(user1.getPhone()).thenReturn("25252525");
        when(filter.getPhonePattern()).thenReturn("89123698");
        Stream<User> users = Stream.of(user, user1);
        Stream<User> filteredUsers = userPhoneFilter.apply(users, filter);

        assertEquals(1, filteredUsers.count());
    }

    @Test
    public void testApplyWithNoMatching() {
        when(user.getPhone()).thenReturn("89123698");
        when(user1.getPhone()).thenReturn("25252525");
        when(filter.getPhonePattern()).thenReturn("363636366");
        Stream<User> users = Stream.of(user, user1);
        Stream<User> filteredUsers = userPhoneFilter.apply(users, filter);

        assertEquals(0, filteredUsers.count());
    }
}
