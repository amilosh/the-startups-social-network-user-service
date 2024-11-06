package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventSkillsIdFilterTest {

   private final EventSkillsIdFilter eventSkillsIdFilter = new EventSkillsIdFilter();

    @Test
    public void testEventSkillIdNotNull() {
        Skill skill1 = new Skill();
        skill1.setId(1L);

        Skill skill2 = new Skill();
        skill2.setId(2L);


        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .skillIds(List.of(skill1.getId(), skill2.getId()))
                .build();

        boolean result = eventSkillsIdFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void testEventSkillIdIsNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .skillIds(null)
                .build();

        boolean result = eventSkillsIdFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void applySkillIdsFilterTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .skillIds(List.of(1L, 2L))
                .build();

        Skill skill1 = new Skill();
        skill1.setId(1L);

        Skill skill2 = new Skill();
        skill2.setId(2L);

        Skill skill3 = new Skill();
        skill3.setId(3L);

        Event event1 = Event.builder()
                .relatedSkills(List.of(skill1, skill2))
                .build();

        Event event2 = Event.builder()
                .relatedSkills(List.of(skill3))
                .build();

        Event event3 = Event.builder()
                .relatedSkills(List.of(skill1, skill3))
                .build();

        Stream<Event> events = Stream.of(event1, event2, event3);

        Stream<Event> filteredEvents = eventSkillsIdFilter.apply(events, eventFilterDto);

        List<Event> resultList = filteredEvents.toList();

        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(event1));
        assertTrue(resultList.contains(event3));
    }
}
