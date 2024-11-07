package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserNameFilterTest extends UserFilterTest {
    private final UserNameFilter userNameFilter = new UserNameFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(userNameFilter, filter.getNamePattern(), "Ivan");
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(userNameFilter, filter.getNamePattern(), null);
    }

    @Test
    public void testApplyWithMatching() {
        when(user.getUsername()).thenReturn("Ivan");
        when(user1.getUsername()).thenReturn("Kostya");
        when(filter.getNamePattern()).thenReturn("Ivan");
        Stream<User> users = Stream.of(user, user1);
        Stream<User> filteredUsers = userNameFilter.apply(users, filter);

        assertEquals(1, filteredUsers.count());
    }

    @Test
    public void testApplyWithNoMatching() {
        when(user.getUsername()).thenReturn("Ivan");
        when(user1.getUsername()).thenReturn("Kostya");
        when(filter.getNamePattern()).thenReturn("Kristina");
        Stream<User> users = Stream.of(user, user1);
        Stream<User> filteredUsers = userNameFilter.apply(users, filter);

        assertEquals(0, filteredUsers.count());
    }
}
