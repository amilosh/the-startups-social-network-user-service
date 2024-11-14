package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Component
public class EventSkillsFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        List<SkillDto> relatedSkills = filters.getRelatedSkillsPattern();
        return relatedSkills != null && !relatedSkills.isEmpty();
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto filters) {
        events.filter(event -> {
            HashSet<Skill> skills = new HashSet<>(event.getRelatedSkills());
            return skills.containsAll(filters.getRelatedSkillsPattern());
        });
    }
}
