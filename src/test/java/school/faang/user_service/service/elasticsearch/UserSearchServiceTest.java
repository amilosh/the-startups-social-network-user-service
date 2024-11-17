package school.faang.user_service.service.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.document.UserDocument;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSearchServiceTest {

    @InjectMocks
    private UserSearchService userSearchService;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    private SearchResponse<Object> searchResponse;
    private UserDocument userDocument;

    private String query;
    private Integer page;
    private Integer size;

    @BeforeEach
    public void setUp() {
        userDocument = new UserDocument();
        userDocument.setUsername("name");
        userDocument.setSkills(List.of("java"));

        query = "java";
        page = 0;
        size = 10;

        Hit<Object> hit = new Hit.Builder<>()
                .index("index")
                .id("id")
                .source(userDocument)
                .build();

        HitsMetadata<Object> hits = new HitsMetadata.Builder<>()
                .hits(List.of(hit, hit))
                .build();

        searchResponse = new SearchResponse.Builder<>()
                .took(1L)
                .timedOut(false)
                .shards(s -> s
                        .failed(0)
                        .successful(0)
                        .total(0)
                )
                .hits(hits)
                .build();
    }

    @Test
    public void testSearchUsers() throws IOException {
        when(elasticsearchClient.search((SearchRequest) any(), any())).thenReturn(searchResponse);

        List<UserDocument> result = userSearchService.searchUsers(query, page, size);

        assertEquals(userDocument, result.get(0));
        assertEquals(userDocument, result.get(1));
    }
}
