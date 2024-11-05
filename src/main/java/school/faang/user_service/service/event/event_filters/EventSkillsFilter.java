package school.faang.user_service.service.event.event_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventSkillsFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getRelatedSkills() != null && !filters.getRelatedSkills().isEmpty();
    }

    @Override
    public List<Event> apply(List<Event> events, EventFilterDto filters) {
        Set<Long> requiredSkillIds = filters.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .collect(Collectors.toSet());

        return events.stream().filter(event -> {
                    Set<Long> eventSkillIds = event.getRelatedSkills().stream()
                            .map(Skill::getId)
                            .collect(Collectors.toSet());
                    return eventSkillIds.stream().anyMatch(requiredSkillIds::contains);
                })
                .toList();
    }
}