package school.faang.user_service.scheduled;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.elasticsearch.UserElasticsearchRepository;

import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchDataSyncServiceTest {

    @InjectMocks
    private ElasticSearchDataSyncService dataSyncService;

    @Mock
    private UserElasticsearchRepository userElasticsearchRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Object> typedQuery;

    @BeforeEach
    public void setUp() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

    }

    @Test
    public void testSyncData() {
        when(entityManager.createQuery(any(), any())).thenReturn(typedQuery);
        when(typedQuery.setHint(any(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>());

        dataSyncService.syncData();

        verify(userElasticsearchRepository).saveAll(any());
    }
}
