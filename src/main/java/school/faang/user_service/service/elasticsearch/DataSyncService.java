package school.faang.user_service.service.elasticsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import school.faang.user_service.entity.document.UserDocument;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.elasticsearch.UserElasticsearchRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSyncService {
    private final UserElasticsearchRepository userElasticsearchRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JedisPool jedisPool;

    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    public void syncData() {
        log.info("Starting data synchronization from PostgreSQL to Elasticsearch...");

        Stream<UserDocument> usersStream = userRepository.findAll().stream().map(userMapper::userToUserDocument);
        try (Jedis jedis = jedisPool.getResource()) {

            List<UserDocument> users = usersStream.peek(
                    user -> user.setSearchScore(jedisGetOrDefault(jedis, user.getId(), 0))).toList();

            // заполнение скиллов для тестирования поиска по ним
            List<List<String>> skills = List.of(
                    List.of("java", "TypeScript", "C++"),
                    List.of("python", "Java"),
                    List.of("C++"),
                    List.of("Go", "Kotlin"),
                    List.of("JavaScript"),
                    List.of("C#"),
                    List.of("TypeScript", "Java", "Go"),
                    List.of("java"),
                    List.of("javaScript"),
                    List.of("HTML", "css", "javaScript")
            );
            for (int i = 0; i < users.size(); i++) {
                users.get(i).setSkills(skills.get(i));
            }

            userElasticsearchRepository.saveAll(users);
        }

        log.info("Data synchronization completed from PostgreSQL to Elasticsearch");
    }

    private int jedisGetOrDefault(Jedis jedis, long key, int defaultValue) {
          String value = jedis.get(String.valueOf(key));
          return value != null ? Integer.parseInt(value) : defaultValue;
    }
}
