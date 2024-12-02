package school.faang.user_service.service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventTitleFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventTitleFilterTests {
    @InjectMocks
    private EventTitleFilter eventTitleFilter;
    @Mock
    private EventFilterDto filterPatterns;
    @Mock
    private Event event;

    @Test
    public void testIsApplicableWithTitlePattern() {
        when(filterPatterns.getTitlePattern()).thenReturn("TestTitle");
        assertTrue(eventTitleFilter.isApplicable(filterPatterns));
    }

    @Test
    void testIsApplicableWithoutTitlePattern() {
        when(filterPatterns.getTitlePattern()).thenReturn(null);
        assertFalse(eventTitleFilter.isApplicable(filterPatterns));

        when(filterPatterns.getTitlePattern()).thenReturn("");
        assertFalse(eventTitleFilter.isApplicable(filterPatterns));

        when(filterPatterns.getTitlePattern()).thenReturn("   ");
        assertFalse(eventTitleFilter.isApplicable(filterPatterns));
    }

    @Test
    void testApplyForMatchingFilterPattern() {
        when(event.getTitle()).thenReturn("TestTitle");
        when(filterPatterns.getTitlePattern()).thenReturn("TestTitle");
        assertTrue(eventTitleFilter.apply(event, filterPatterns));
    }

    @Test
    void testApplyForNonMatchingFilterPattern() {
        when(event.getTitle()).thenReturn("TestTitle1");
        when(filterPatterns.getTitlePattern()).thenReturn("TestTitle2");
        assertFalse(eventTitleFilter.apply(event, filterPatterns));
    }

    @Test
    public void testApplyWithNullEventTitle() {
        when(event.getTitle()).thenReturn(null);
        assertFalse(eventTitleFilter.apply(event, filterPatterns));
    }
}
