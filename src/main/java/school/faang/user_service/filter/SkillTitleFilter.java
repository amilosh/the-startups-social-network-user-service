package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class SkillTitleFilter implements Filter<RecommendationRequest, RequestFilterDto> {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getSkillTitles() != null && !filter.getSkillTitles().isEmpty();
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> stream, RequestFilterDto filter) {
        List<String> skillTitles = filter.getSkillTitles();
        return stream.filter(request ->
                request.getSkills() != null &&
                        request.getSkills().stream()
                                .anyMatch(skillRequest -> skillTitles.contains(skillRequest.getSkill().getTitle()))
        );
    }
}
