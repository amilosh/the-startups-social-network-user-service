package school.faang.user_service.service.goal;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;

    @Mock
    private UserService userService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private List<GoalFilter> goalFilters;

    @Spy
    private GoalMapper goalMapper;

    @Mock
    private GoalValidator goalValidation;
}
