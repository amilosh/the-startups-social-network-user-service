package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public abstract class UserFilterTest {
    protected UserFilterDto filter;
    protected final User user = Mockito.mock(User.class);
    protected final User user1 = Mockito.mock(User.class);

    @BeforeEach
    public void init() {
        filter = Mockito.mock(UserFilterDto.class);
    }

    public void IsApplicableCheck(UserFilter userFilter, String pattern, String value) {
        when(pattern).thenReturn(value);
        assertTrue(userFilter.isApplicable(filter), "Success");
    }

    public void IsNotApplicableCheck(UserFilter userFilter, String pattern, String value) {
        when(pattern).thenReturn(value);
        assertFalse(userFilter.isApplicable(filter), "Failed");
    }
}
