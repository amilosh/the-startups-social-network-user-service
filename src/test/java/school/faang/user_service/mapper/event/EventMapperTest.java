package school.faang.user_service.mapper.event;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.dto.promotion.PromotedEventResponseDto;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.premium.PremiumFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getEventPromotion;

public class EventMapperTest {
    private static final long USER_ID = 1;
    private static final long EVENT_ID = 1;
    private static final String TITLE = "test title";
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;

    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
    private Event event;
    private User user;

    @BeforeEach
    public void setUp() {
        user = createUser(1L, "Misha");

        Skill skill1 = createSkill(1L, "Java");
        Skill skill2 = createSkill(2L, "Spring");
        List<Skill> relatedSkills = Arrays.asList(skill1, skill2);

        event = createEvent(100L, "Test Event", user, relatedSkills, "New York", 50);
    }

    @Test
    public void toDto_ShouldMapEventToEventDto() {
        EventDto actualDto = eventMapper.toDto(event);

        EventDto expectedDto = EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .ownerId(event.getOwner().getId())
                .description(event.getDescription())
                .relatedSkillsIds(getSkillsIds(event.getRelatedSkills()))
                .location(event.getLocation())
                .type(event.getType())
                .status(event.getStatus())
                .maxAttendees(event.getMaxAttendees())
                .build();

        Assertions.assertThat(actualDto)
                .usingRecursiveComparison()
                .ignoringFields("relatedSkillsIds")
                .isEqualTo(expectedDto);
    }

    @Test
    public void toDtoList_ShouldMapEventListToEventDtoList() {
        List<Event> events = Arrays.asList(event);

        List<EventDto> actualDtos = eventMapper.toDto(events);

        List<EventDto> expectedDtos = events.stream()
                .map(event -> EventDto.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .startDate(event.getStartDate())
                        .endDate(event.getEndDate())
                        .ownerId(event.getOwner().getId())
                        .description(event.getDescription())
                        .relatedSkillsIds(getSkillsIds(event.getRelatedSkills()))
                        .location(event.getLocation())
                        .type(event.getType())
                        .status(event.getStatus())
                        .maxAttendees(event.getMaxAttendees())
                        .build())
                .collect(Collectors.toList());

        Assertions.assertThat(actualDtos)
                .usingRecursiveComparison()
                .ignoringFields("relatedSkillsIds")
                .isEqualTo(expectedDtos);
    }

    @Test
    public void toEventWithSubscribersDto_ShouldMapEventToEventWithSubscribersDto() {
        int subscribersCount = 100;

        EventWithSubscribersDto actualDto = eventMapper.toEventWithSubscribersDto(event, subscribersCount);

        EventWithSubscribersDto expectedDto = EventWithSubscribersDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .ownerId(event.getOwner().getId())
                .description(event.getDescription())
                .relatedSkillsIds(getSkillsIds(event.getRelatedSkills()))
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .subscribersCount(subscribersCount)
                .type(event.getType())
                .status(event.getStatus())
                .build();

        Assertions.assertThat(actualDto)
                .usingRecursiveComparison()
                .ignoringFields("relatedSkillsIds")
                .isEqualTo(expectedDto);
    }

    @Test
    public void toFilteredEventsDto_ShouldMapEventListToFilteredEventsDto() {
        List<Event> events = Arrays.asList(event);

        List<EventDto> actualDtos = eventMapper.toFilteredEventsDto(events);

        List<EventDto> expectedDtos = events.stream()
                .map(event -> EventDto.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .startDate(event.getStartDate())
                        .endDate(event.getEndDate())
                        .ownerId(event.getOwner().getId())
                        .description(event.getDescription())
                        .relatedSkillsIds(event.getRelatedSkills().stream().map(Skill::getId).collect(Collectors.toList()))
                        .location(event.getLocation())
                        .type(event.getType())
                        .status(event.getStatus())
                        .maxAttendees(event.getMaxAttendees())
                        .build())
                .collect(Collectors.toList());

        Assertions.assertThat(actualDtos)
                .usingRecursiveComparison()
                .ignoringFields("relatedSkillsIds", "ownerUsername")
                .isEqualTo(expectedDtos);
    }

    @Test
    @DisplayName("Test converting event to response event")
    void testToDto() {
        User user = getUser(USER_ID);
        EventPromotion eventPromotion = getEventPromotion(TARIFF, TARIFF.getNumberOfViews());
        Event event = getEvent(EVENT_ID, TITLE, user, List.of(eventPromotion));
        var responseDto = new PromotedEventResponseDto(EVENT_ID, TITLE, USER_ID, TARIFF.toString(),
                TARIFF.getNumberOfViews(), null);

        assertThat(eventMapper.toPromotedEventResponseDto(event)).isEqualTo(responseDto);
    }

    private Event createEvent(Long id, String title, User owner, List<Skill> relatedSkills, String location, int maxAttendees) {
        return Event.builder()
                .id(id)
                .title(title)
                .owner(owner)
                .relatedSkills(relatedSkills)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .location(location)
                .maxAttendees(maxAttendees)
                .type(EventType.WEBINAR)
                .status(EventStatus.PLANNED)
                .build();
    }

    private User createUser(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .build();
    }

    private Skill createSkill(Long id, String title) {
        return Skill.builder()
                .id(id)
                .title(title)
                .build();
    }

    private List<Long> getSkillsIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}