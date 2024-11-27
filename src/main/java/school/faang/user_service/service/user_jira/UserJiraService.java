package school.faang.user_service.service.user_jira;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.userJira.UserJira;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.user_jira.UserJiraRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserJiraService {

    private final UserJiraRepository userJiraRepository;

    @Transactional
    public UserJira saveOrUpdate(UserJira userJira) {
        Optional<UserJira> existingUserJiraOptional = userJiraRepository
                .findByUserIdAndJiraDomain(userJira.getUser().getId(), userJira.getJiraDomain());
        if (existingUserJiraOptional.isPresent()) {
            UserJira existingUserJira = existingUserJiraOptional.get();
            existingUserJira.setJiraAccountId(userJira.getJiraAccountId());
            existingUserJira.setJiraEmail(userJira.getJiraEmail());
            existingUserJira.setJiraToken(userJira.getJiraToken());
            return userJiraRepository.save(existingUserJira);
        }
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
