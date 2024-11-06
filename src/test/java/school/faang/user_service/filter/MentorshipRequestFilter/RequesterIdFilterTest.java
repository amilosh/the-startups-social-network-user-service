package school.faang.user_service.filter.MentorshipRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequesterIdFilterTest extends SetUpFilterTest {
    private RequesterIdFilter requesterIdFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        requesterIdFilter = new RequesterIdFilter();
    }

    @Test
    void testRequesterIdFilterIsApplicable() {
        assertTrue(requesterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testRequesterIdFilterIsNotApplicable() {
        filterDto.setRequesterId(null);
        assertFalse(requesterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testRequesterIdFilter() {
        requesterIdFilter.apply(requestStream, filterDto).forEach(request ->
                assertEquals(request.getRequester().getId(), filterDto.getRequesterId()));
    }
}