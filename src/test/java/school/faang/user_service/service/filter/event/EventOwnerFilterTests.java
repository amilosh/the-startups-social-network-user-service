package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventOwnerFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventOwnerFilterTests {
    @InjectMocks
    private EventOwnerFilter eventOwnerFilter;
    @Mock
    private EventFilterDto filterPatterns;
    @Mock
    private Event event;
    @Mock
    private User owner;
    private final Long ownerId = 33L;

    @Test
    public void testIsApplicableWithDescriptionPattern() {
        when(filterPatterns.getOwnerIdPattern()).thenReturn(ownerId);
        assertTrue(eventOwnerFilter.isApplicable(filterPatterns));
    }

    @Test
    void testIsApplicableWithoutOwnerPattern() {
        when(filterPatterns.getOwnerIdPattern()).thenReturn(null);
        assertFalse(eventOwnerFilter.isApplicable(filterPatterns));
    }

    @Test
    void testApplyForMatchingFilterPattern() {
        when(event.getOwner()).thenReturn(owner);
        when(event.getOwner().getId()).thenReturn(ownerId);
        when(filterPatterns.getOwnerIdPattern()).thenReturn(ownerId);
        assertTrue(eventOwnerFilter.apply(event, filterPatterns));
    }

    @Test
    void testApplyForNonMatchingFilterPattern() {
        when(event.getOwner()).thenReturn(owner);
        when(event.getOwner().getId()).thenReturn(ownerId);
        when(filterPatterns.getOwnerIdPattern()).thenReturn(34L);
        assertFalse(eventOwnerFilter.apply(event, filterPatterns));
    }

    @Test
    public void testApplyWithNullEventOwner() {
        when(event.getOwner()).thenReturn(null);
        assertFalse(eventOwnerFilter.apply(event, filterPatterns));
    }
}
