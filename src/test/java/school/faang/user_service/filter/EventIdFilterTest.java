package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventIdFilterTest {

   private final EventIdFilter eventIdFilter = new EventIdFilter();

   @Test
   public void testEventIdNotNull() {
      EventFilterDto eventFilterDto = EventFilterDto.builder()
              .id(1L)
              .build();

      boolean result = eventIdFilter.isApplicable(eventFilterDto);
      assertTrue(result);
   }

   @Test
   public void testEventIdIsNull() {
      EventFilterDto eventFilterDto = EventFilterDto.builder()
              .id(null)
              .build();

      boolean result = eventIdFilter.isApplicable(eventFilterDto);
      assertFalse(result);
   }

   @Test
   public void applyEventIdTest() {
      EventFilterDto filter = EventFilterDto.builder()
              .id(1L)
              .build();

      Event event1 = Event.builder()
              .id(2L)
              .build();

      Event event2 = Event.builder()
              .id(3L)
              .build();

      Stream<Event> events = Stream.of(event1, event2);
      Stream<Event> resultStream = eventIdFilter.apply(events, filter);

      List<Event> resultList = resultStream.toList();

      int expected = 0;
      int resultListSize = resultList.size();

      assertEquals(expected, resultListSize);
   }
}
