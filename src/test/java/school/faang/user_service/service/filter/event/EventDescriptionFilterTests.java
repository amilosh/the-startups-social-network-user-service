package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventDescriptionFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventDescriptionFilterTests {
    @InjectMocks
    private EventDescriptionFilter eventDescriptionFilter;
    @Mock
    private EventFilterDto filterPatterns;
    @Mock
    private Event event;

    @Test
    public void testIsApplicableWithDescriptionPattern() {
        when(filterPatterns.getDescriptionPattern()).thenReturn("Descr");
        assertTrue(eventDescriptionFilter.isApplicable(filterPatterns));
    }

    @Test
    void testIsApplicableWithoutDescriptionPattern() {
        when(filterPatterns.getDescriptionPattern()).thenReturn(null);
        assertFalse(eventDescriptionFilter.isApplicable(filterPatterns));

        when(filterPatterns.getDescriptionPattern()).thenReturn("");
        assertFalse(eventDescriptionFilter.isApplicable(filterPatterns));

        when(filterPatterns.getDescriptionPattern()).thenReturn("   ");
        assertFalse(eventDescriptionFilter.isApplicable(filterPatterns));
    }

    @Test
    void testApplyForMatchingFilterPattern() {
        when(event.getDescription()).thenReturn("TestTitle");
        when(filterPatterns.getDescriptionPattern()).thenReturn("TestTitle");
        assertTrue(eventDescriptionFilter.apply(event, filterPatterns));
    }

    @Test
    void testApplyForContainingFilterPattern() {
        when(event.getDescription()).thenReturn("TestTitle1");
        when(filterPatterns.getDescriptionPattern()).thenReturn("TestTitle2");
        assertFalse(eventDescriptionFilter.apply(event, filterPatterns));
    }

    @Test
    void testApplyForNonContainingFilterPattern() {
        when(event.getDescription()).thenReturn("Diff");
        when(filterPatterns.getDescriptionPattern()).thenReturn("Test");
        assertFalse(eventDescriptionFilter.apply(event, filterPatterns));
    }

    @Test
    public void testApplyWithNullEventDescription() {
        when(event.getDescription()).thenReturn(null);
        assertFalse(eventDescriptionFilter.apply(event, filterPatterns));
    }
}
