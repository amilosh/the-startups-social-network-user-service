package school.faang.user_service.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import school.faang.user_service.entity.User;

public interface ElasticsearchUserRepository extends ElasticsearchRepository<User, Long> {
}
