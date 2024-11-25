package school.faang.user_service.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private static final Long EVENT_ID_ONE = 1L;
    private static final String EVENT_NAME = "Event Name";
    private static final String EVENT_DESCRIPTION = "Event Description";
    private static final String EVENT_LOCATION = "Event Location";
    private static final LocalDateTime EVENT_START_DATE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime EVENT_END_DATE = LocalDateTime.now().plusDays(2);
    private static final Long OWNER_ID = 101L;
    private static final int MAX_ATTENDEES = 5;

    private SkillDto skillDto;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        skillDto = new SkillDto();
        skillDto.setId(201L);
        skillDto.setTitle("Skill Name");
    }

    @Test
    public void testCreate() throws Exception {
        EventDto eventDto = new EventDto();
        eventDto.setId(EVENT_ID_ONE);
        eventDto.setRelatedSkills(List.of(skillDto));
        eventDto.setTitle(EVENT_NAME);
        eventDto.setOwnerId(OWNER_ID);
        eventDto.setStartDate(EVENT_START_DATE);
        eventDto.setDescription(EVENT_DESCRIPTION);
        eventDto.setLocation(EVENT_LOCATION);
        eventDto.setType(EventType.MEETING);
        eventDto.setStatus(EventStatus.IN_PROGRESS);
        eventDto.setEndDate(EVENT_END_DATE);
        eventDto.setMaxAttendees(MAX_ATTENDEES);

        String eventJson = objectMapper.writeValueAsString(eventDto);

        when(eventService.create(ArgumentMatchers.any(EventDto.class))).thenReturn(eventDto);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENT_ID_ONE))
                .andExpect(jsonPath("$.title").value(EVENT_NAME))
                .andExpect(jsonPath("$.description").value(EVENT_DESCRIPTION))
                .andExpect(jsonPath("$.ownerId").value(OWNER_ID))
                .andExpect(jsonPath("$.relatedSkills[0].id").value(skillDto.getId()))
                .andExpect(jsonPath("$.relatedSkills[0].title").value(skillDto.getTitle()));

        verify(eventService).create(ArgumentMatchers.any(EventDto.class));
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testGet() throws Exception {
        EventDto eventDto = new EventDto();
        eventDto.setId(EVENT_ID_ONE);
        eventDto.setTitle(EVENT_NAME);
        eventDto.setOwnerId(OWNER_ID);

        when(eventService.get(EVENT_ID_ONE)).thenReturn(eventDto);

        mockMvc.perform(get("/api/v1/events")
                        .param("eventId", String.valueOf(EVENT_ID_ONE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENT_ID_ONE))
                .andExpect(jsonPath("$.title").value(EVENT_NAME));

        verify(eventService).get(EVENT_ID_ONE);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testGetByFilter() throws Exception {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setLocation(EVENT_LOCATION);
        filterDto.setStartDate(EVENT_START_DATE);

        EventDto eventDto = new EventDto();
        eventDto.setId(EVENT_ID_ONE);
        eventDto.setRelatedSkills(List.of(skillDto));
        eventDto.setTitle(EVENT_NAME);
        eventDto.setOwnerId(OWNER_ID);
        eventDto.setStartDate(EVENT_START_DATE);
        eventDto.setDescription(EVENT_DESCRIPTION);
        eventDto.setLocation(EVENT_LOCATION);
        eventDto.setType(EventType.MEETING);
        eventDto.setStatus(EventStatus.IN_PROGRESS);
        eventDto.setEndDate(EVENT_END_DATE);
        eventDto.setMaxAttendees(MAX_ATTENDEES);
        List<EventDto> events = List.of(eventDto);

        when(eventService.getByFilter(ArgumentMatchers.any(EventFilterDto.class))).thenReturn(events);

        mockMvc.perform(get("/api/v1/events/filter")
                        .param("location", EVENT_LOCATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EVENT_ID_ONE))
                .andExpect(jsonPath("$[0].title").value(EVENT_NAME));

        verify(eventService).getByFilter(ArgumentMatchers.any(EventFilterDto.class));
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/events/{eventId}", EVENT_ID_ONE))
                .andExpect(status().isOk());

        verify(eventService).delete(EVENT_ID_ONE);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testUpdate() throws Exception {
        EventDto eventDtoUpdate = new EventDto();
        eventDtoUpdate.setId(EVENT_ID_ONE);
        eventDtoUpdate.setRelatedSkills(List.of(skillDto));
        eventDtoUpdate.setTitle("Some New Title");
        eventDtoUpdate.setOwnerId(OWNER_ID);
        eventDtoUpdate.setStartDate(EVENT_START_DATE);
        eventDtoUpdate.setDescription(EVENT_DESCRIPTION);
        eventDtoUpdate.setLocation(EVENT_LOCATION);
        eventDtoUpdate.setType(EventType.MEETING);
        eventDtoUpdate.setStatus(EventStatus.IN_PROGRESS);
        eventDtoUpdate.setEndDate(EVENT_END_DATE);
        eventDtoUpdate.setMaxAttendees(MAX_ATTENDEES);

        when(eventService.update(ArgumentMatchers.any(EventDto.class))).thenReturn(eventDtoUpdate);

        String eventJson = objectMapper.writeValueAsString(eventDtoUpdate);

        mockMvc.perform(put("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENT_ID_ONE))
                .andExpect(jsonPath("$.title").value(eventDtoUpdate.getTitle()));

        verify(eventService).update(ArgumentMatchers.any(EventDto.class));
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testGetOwnedEvents() throws Exception {
        EventDto eventDto = new EventDto();
        eventDto.setId(EVENT_ID_ONE);
        eventDto.setTitle(EVENT_NAME);
        List<EventDto> events = List.of(eventDto);

        when(eventService.getOwnedEvents(OWNER_ID)).thenReturn(events);

        mockMvc.perform(get("/api/v1/events/owner/{ownerId}", OWNER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EVENT_ID_ONE))
                .andExpect(jsonPath("$[0].title").value(EVENT_NAME));

        verify(eventService).getOwnedEvents(OWNER_ID);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testGetParticipatedEvents() throws Exception {
        EventDto eventDto = new EventDto();
        eventDto.setId(EVENT_ID_ONE);
        eventDto.setTitle(EVENT_NAME);
        List<EventDto> events = List.of(eventDto);

        when(eventService.getParticipatedEvents(OWNER_ID)).thenReturn(events);

        mockMvc.perform(get("/api/v1/events/participant/{participantId}", OWNER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EVENT_ID_ONE))
                .andExpect(jsonPath("$[0].title").value(EVENT_NAME));

        verify(eventService).getParticipatedEvents(OWNER_ID);
        verifyNoMoreInteractions(eventService);
    }
}