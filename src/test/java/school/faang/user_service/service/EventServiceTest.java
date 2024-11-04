package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private  UserService userService;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private List<EventFilter> eventFilters;

    @InjectMocks
    private EventService eventService;

    @Test
    void testCreateIsCreate() {
        eventService.create(eventDto);
        //метод который проверяет, что какой то метод в моке нашего теста в самом деле был вызван
        Mockito.verify(eventRepository, Mockito.timeout(1)).save(event);
    }

    @Test
    void testGetEvent() {
    }

    @Test
    void testGetEventsByFilter() {
    }

    @Test
    void testDeleteEvent() {
    }

    @Test
    void testUpdateEvent() {
    }

    @Test
    void getOwnedEvents() {
    }

    @Test
    void getParticipatedEvents() {
    }
}