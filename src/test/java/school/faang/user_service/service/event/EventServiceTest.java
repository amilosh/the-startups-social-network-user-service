package school.faang.user_service.service.event;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.filters.*;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private EventMapperImpl eventMapper;

    @Mock
    private EventFilter eventFilterMock;
    @Mock
    private List<EventFilter> eventFilters;
    @Mock
    EventFilterDto eventFilterDto;
    @Captor
    private ArgumentCaptor<Long> argumentCaptor;
    @Captor
    private ArgumentCaptor<EventFilterDto> eventFilterCaptor;
    @Captor
    private ArgumentCaptor<EventDto> eventDtoCaptor;
    @Captor
    private ArgumentCaptor<Event> eventCaptor;
    @Mock
    EventDto eventDto;
    @Mock
    List<EventDto> eventDtos;

    @Mock
    List<Event> eventList;

    long eventId = 5;
    long userId = 4;
    @Mock
    Event event;

    @Mock
    SkillDto skillDto;

    @BeforeEach
    public void setupEvent() {
        event = mock(Event.class);
        event.setId(2);
        event.setTitle("EventTitle");
        event.setDescription("eventDescription");
    }

    @BeforeEach
    public void setupEventDto() {
        eventDto = mock(EventDto.class);
        eventDto.setTitle("title");
        eventDto.setDescription("description");
        eventDto.setId(1L);
        eventDto.setOwnerId(2L);
        eventDto.setStartDate(LocalDateTime.of(2023, 11, 21, 10, 30));
        eventDto.setRelatedSkills(new ArrayList<>());
    }

    @BeforeEach
    public void setupEventDtoList() {
        eventDtos = new ArrayList<>();
        EventDto firstEventDto = mock(EventDto.class);
        EventDto secondEventDto = mock(EventDto.class);
        firstEventDto.setTitle("firstTitle");
        secondEventDto.setTitle("secondTitle");
        eventDtos.add(firstEventDto);
        eventDtos.add(secondEventDto);
    }

    @BeforeEach
    public void setupEventList() {
        eventList = new ArrayList<>();
        Event firstEvent = mock(Event.class);
        Event secondEvent = mock(Event.class);
        firstEvent.setTitle("firstTitle");
        secondEvent.setTitle("secondTitle");
        eventList.add(firstEvent);
        eventList.add(secondEvent);

        eventFilterMock = Mockito.mock(EventFilter.class);
        EventOwnerIdFilter filterOwnerIdMock = Mockito.mock(EventOwnerIdFilter.class);
        EventStartDateFromFilter filterStartDateFromMock = Mockito.mock(EventStartDateFromFilter.class);
        EventStartDateToFilter filterStartDateToMock = Mockito.mock(EventStartDateToFilter.class);
        EventTitleFilter filterTitleMock = Mockito.mock(EventTitleFilter.class);
        eventFilters = List.of(filterOwnerIdMock, filterStartDateFromMock, filterStartDateToMock, filterTitleMock);
    }


    @Test
    public void testWithNoRequiredSkills() {
        List<Long> skillId = List.of(1L, 3L, 6L);
        List<SkillDto> relatedSkills = List.of(new SkillDto(1L, ""), new SkillDto(3L, ""), new SkillDto(6L, ""));
        when(eventDto.getRelatedSkills()).thenReturn(relatedSkills);
        when(skillRepository.findAllByUserId(eventDto.getOwnerId()))
                .thenReturn(new ArrayList<>());

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void testCreate() {
        EventDto result = eventService.create(eventDto);
        verify(eventMapper, times(1)).toEntity(eventDtoCaptor.capture());
        EventDto capturedEventDto = eventDtoCaptor.getValue();
        assertEquals(eventDto, capturedEventDto);
        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();
        assertEquals(eventMapper.toEntity(eventDto), capturedEvent);
    }


    @Test
    public void testGetEventWithNoEvent() {
        Optional<Event> eventOptional = Optional.empty();
        when(eventRepository.findById(eventId)).thenReturn(eventOptional);
        assertThrows(DataValidationException.class, () -> eventService.getEvent(eventId));
    }

    @Test
    public void testGetEventWithEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        EventDto result = eventService.getEvent(eventId);
        verify(eventRepository, times(1)).findById(argumentCaptor.capture());
        Long capturedEventId = argumentCaptor.getValue();
        verify(eventMapper, times(1)).toDto(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();

        assertEquals(eventId, capturedEventId);
        assertEquals(event, capturedEvent);
        assertEquals(eventMapper.toDto(event), result);
    }

    @Test
    public void testGetEventsByFilter() {
        when(eventRepository.findAll()).thenReturn(eventList);
        Stream<Event> eventsStream = eventList.stream();

        List<EventDto> gotEventDtoList = eventService.getEventsByFilter(eventFilterDto);
        verify(eventRepository, times(1)).findAll();
        assertEquals(eventMapper.toDtoList(eventList), gotEventDtoList);

    }

    @Test
    public void testDeleteEvent() {
        long result = eventService.deleteEvent(eventId);
        verify(eventRepository, times(1)).deleteById(argumentCaptor.capture());
        long capturedEventId = argumentCaptor.getValue();
        Assert.assertEquals(eventId, capturedEventId);
        Assert.assertEquals(eventId, result);

    }

    @Test
    public void testUpdateEvent() {
        when(eventRepository.save(eventMapper.toEntity(eventDto))).thenReturn(event);

        EventDto updatedEvent = eventService.updateEvent(eventDto);
        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();
        assertEquals(eventMapper.toEntity(eventDto), savedEvent);
        assertEquals(eventMapper.toDto(event), updatedEvent);
    }

    @Test
    public void testGetOwnedEvents() {
        when(eventRepository.findAllByUserId(userId)).thenReturn(eventList);

        List<EventDto> reslutList = eventService.getOwnedEvents(userId);
        verify(eventRepository, times(1)).findAllByUserId(argumentCaptor.capture());
        long capturedUserId = argumentCaptor.getValue();
        assertEquals(userId, capturedUserId);
        assertEquals(eventMapper.toDtoList(eventList), reslutList);
    }

    @Test
    public void testGetParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(eventList);

        List<EventDto> reslutList = eventService.getParticipatedEvents(userId);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(argumentCaptor.capture());
        long capturedUserId = argumentCaptor.getValue();
        assertEquals(userId, capturedUserId);
        assertEquals(eventMapper.toDtoList(eventList), reslutList);
    }
}
