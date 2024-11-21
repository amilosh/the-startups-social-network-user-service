package school.faang.user_service.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import school.faang.user_service.entity.document.UserDocument;

public interface UserElasticsearchRepository extends ElasticsearchRepository<UserDocument, Long> {
}
