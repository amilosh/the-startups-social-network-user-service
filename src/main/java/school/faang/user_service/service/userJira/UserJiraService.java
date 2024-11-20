package school.faang.user_service.service.userJira;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.userJira.UserJira;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.userJira.UserJiraRepository;

@Service
@RequiredArgsConstructor
public class UserJiraService {

    private final UserJiraRepository userJiraRepository;

    @Transactional
    public UserJira saveOrUpdate(UserJira userJira) {
        return userJiraRepository.save(userJira);
    }

    @Transactional
    public UserJira getByUserIdAndJiraDomain(long userId, String jiraDomain) {
        return userJiraRepository.findByUserIdAndJiraDomain(userId, jiraDomain)
                .orElseThrow(() ->
                        new EntityNotFoundException("User Jira account information not found for given user ID %d and Jira domain %s"
                                .formatted(userId, jiraDomain))
                );
    }
}
