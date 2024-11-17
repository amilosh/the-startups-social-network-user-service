package school.faang.user_service.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.document.UserDocument;

@Repository
public interface UserElasticsearchRepository extends ElasticsearchRepository<UserDocument, Long> {
}
