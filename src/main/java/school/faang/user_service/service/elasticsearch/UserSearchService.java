package school.faang.user_service.service.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.document.UserDocument;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchService {
    private final ElasticsearchClient elasticsearchClient;

    public List<UserDocument> searchUsers(String query, Integer page, Integer size) {

        SearchRequest.Builder searchRequest = new SearchRequest.Builder().index("users");
        boolean isNumber = isNumber(query);


        if (query == null || query.isBlank()) {
            searchRequest.query(q -> q
                    .matchAll(m -> m)
            );
        } else if (isNumber) {
            searchRequest.query(q -> q
                    .term(t -> t
                            .field("experience")
                            .value(Integer.parseInt(query))
                    )
            );
        } else {
            searchRequest.query(q -> q
                    .bool(b -> b
                            .should(s -> s
                                    .multiMatch(m -> m
                                            .query(query)
                                            .fields("username^2", "aboutMe^1.5", "country^1.2", "city")
                                            .fuzziness("AUTO")
                                            .prefixLength(2)
                                            .tieBreaker(0.3)
                                    )
                            )
                            .should(s -> s
                                    .match(m -> m
                                            .query(query)
                                            .field("skills")
                                            .fuzziness("AUTO")
                                            .prefixLength(2)
                                    )
                            )
                    )
            );
        }
        searchRequest.sort(s -> s
                        .field(f -> f
                                .field("searchScore")
                                .order(SortOrder.Desc)
                        )
                )
                .from(page * size)
                .size(size);

        try {
            SearchResponse<UserDocument> searchResponse = elasticsearchClient.search(searchRequest.build(), UserDocument.class);

            return searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean isNumber(String query) {
        try {
            Integer.parseInt(query);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
