package school.faang.user_service.config.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    @Value("${spring.elasticsearch.uris}")
    private String uris;

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        HttpHost host = HttpHost.create(uris);
        RestClient restClient = RestClient.builder(
                new HttpHost(host)).build();

        return new ElasticsearchClient(new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        ));
    }
}
