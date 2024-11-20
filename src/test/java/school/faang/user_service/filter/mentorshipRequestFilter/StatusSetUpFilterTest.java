package school.faang.user_service.filter.mentorshipRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusSetUpFilterTest extends SetUpFilterTest {
    private StatusFilter statusFilter;

    @BeforeEach
    void setUp() {
        super.setUp(); // вызывает инициализацию из BaseFilterTest
        statusFilter = new StatusFilter();
    }

    @Test
    void testStatusFilterIsApplicable() {
        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testStatusFilterIsNotApplicable() {
        filterDto.setStatus(null);
        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testStatusFilter() {
        statusFilter.apply(requestStream, filterDto).forEach(request ->
                assertEquals(request.getStatus(), filterDto.getStatus()));
    }
}