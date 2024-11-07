package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserEmailFilterTest extends UserFilterTest {
    private final UserEmailFilter userEmailFilter = new UserEmailFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(userEmailFilter, filter.getEmailPattern(), "test@mail.ru");
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(userEmailFilter, filter.getEmailPattern(), null);
    }

    @Test
    public void testApplyWithMatching() {
        when(user.getEmail()).thenReturn("test@mail.ru");
        when(user1.getEmail()).thenReturn("test1235@mail.ru");
        when(filter.getEmailPattern()).thenReturn("test@mail.ru");
        Stream<User> users = Stream.of(user, user1);
        Stream<User> filteredUsers = userEmailFilter.apply(users, filter);

        assertEquals(1, filteredUsers.count());
    }

    @Test
    public void testApplyWithNoMatching() {
        when(user.getEmail()).thenReturn("test@mail.ru");
        when(user1.getEmail()).thenReturn("test1235@mail.ru");
        when(filter.getEmailPattern()).thenReturn("1sfsf@mail.ru");
        Stream<User> users = Stream.of(user, user1);
        Stream<User> filteredUsers = userEmailFilter.apply(users, filter);

        assertEquals(0, filteredUsers.count());
    }
}
