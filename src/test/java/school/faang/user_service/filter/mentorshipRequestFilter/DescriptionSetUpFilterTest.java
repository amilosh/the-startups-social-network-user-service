package school.faang.user_service.filter.mentorshipRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DescriptionSetUpFilterTest extends SetUpFilterTest {
    private DescriptionFilter descriptionFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        descriptionFilter = new DescriptionFilter();
    }

    @Test
    void testDescriptionFilterIsApplicable() {
        assertTrue(descriptionFilter.isApplicable(filterDto));
    }

    @Test
    void testDescriptionFilterIsNotApplicable() {
        filterDto.setDescriptionPattern(null);
        assertFalse(descriptionFilter.isApplicable(filterDto));
    }

    @Test
    void testDescriptionFilter() {
        descriptionFilter.apply(requestStream, filterDto).forEach(request ->
                assertTrue(request.getDescription().toLowerCase()
                        .contains(filterDto.getDescriptionPattern().toLowerCase())));
    }
}