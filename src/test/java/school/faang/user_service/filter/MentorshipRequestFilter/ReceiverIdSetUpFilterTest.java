package school.faang.user_service.filter.MentorshipRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiverIdSetUpFilterTest extends SetUpFilterTest {
    ReceiverIdFilter receiverIdFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        receiverIdFilter = new ReceiverIdFilter();
    }

    @Test
    void testReceiverIdFilterIsApplicable() {
        assertTrue(receiverIdFilter.isApplicable(filterDto));
    }

    @Test
    void testReceiverIdFilterIsNotApplicable() {
        filterDto.setReceiverId(null);
        assertFalse(receiverIdFilter.isApplicable(filterDto));
    }

    @Test
    void testReceiverIdFilter() {
        receiverIdFilter.apply(requestStream, filterDto).forEach(request ->
                assertEquals(request.getReceiver().getId(), filterDto.getReceiverId()));
    }
}