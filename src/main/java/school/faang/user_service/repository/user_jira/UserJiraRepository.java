package school.faang.user_service.repository.user_jira;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.userJira.UserJira;

import java.util.Optional;

@Repository
public interface UserJiraRepository extends JpaRepository<UserJira, Long> {

    Optional<UserJira> findByUserIdAndJiraDomain(long userId, String jiraDomain);
}
