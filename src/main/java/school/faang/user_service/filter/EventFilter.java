package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class EventFilter {
    public List<Event> filterEvents(List<Event> events, EventFilterDto filter) {
        return events.stream()
                .filter(event -> filter.getId() == null || event.getId() == filter.getId())
                .filter(event -> filter.getTitle() == null || event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()))
                .filter(event -> filter.getDescription() == null || event.getDescription().toLowerCase().contains(filter.getDescription().toLowerCase()))
                .filter(event -> filter.getEventType() == null || event.getType() == filter.getEventType())
                .filter(event -> filter.getOwnerId() == null || (event.getOwner() != null && Objects.equals(event.getOwner().getId(), filter.getOwnerId())))
                .filter(event -> filter.getSkillIds() == null || event.getRelatedSkills().stream()
                        .anyMatch(skill -> filter.getSkillIds().contains(skill.getId())))
                .filter(event -> filter.getStartDateFrom() == null || (event.getStartDate() != null && !event.getStartDate().isBefore(filter.getStartDateFrom())))
                .filter(event -> filter.getEndDateFrom() == null || (event.getEndDate() != null && !event.getEndDate().isBefore(filter.getEndDateFrom())))
                .filter(event -> filter.getLocation() == null || event.getLocation().toLowerCase().contains(filter.getLocation().toLowerCase()))
                .filter(event -> filter.getMaxAttendees() == null || event.getMaxAttendees() <= filter.getMaxAttendees())
                .filter(event -> filter.getMinRating() == null || event.getRatings().stream()
                        .mapToDouble(Rating::getRate)
                        .average()
                        .orElse(0.0) >= filter.getMinRating())
                .filter(event -> filter.getStatus() == null || event.getStatus() == filter.getStatus())
                .collect(Collectors.toList());
    }
}
