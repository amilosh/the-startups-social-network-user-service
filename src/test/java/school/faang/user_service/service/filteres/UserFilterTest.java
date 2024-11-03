package school.faang.user_service.service.filteres;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.UserFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserFilterTest {
    @Test
    void testAllMatches() {
        UserFilter userFilter1 = (user, filter) -> true;
        UserFilter userFilter2 = (user, filter) -> true;

        assertTrue(UserFilter.applyAllFilters(List.of(userFilter1, userFilter2), new User(), new UserFilterDto()));
    }

    @Test
    void testNoMatches() {
        UserFilter userFilter1 = (user, filter) -> true;
        UserFilter userFilter2 = (user, filter) -> false;

        assertFalse(UserFilter.applyAllFilters(List.of(userFilter1, userFilter2), new User(), new UserFilterDto()));
    }

}
