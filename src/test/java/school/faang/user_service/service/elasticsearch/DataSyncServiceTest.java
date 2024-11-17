package school.faang.user_service.service.elasticsearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.elasticsearch.UserElasticsearchRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataSyncServiceTest {

    @InjectMocks
    private DataSyncService dataSyncService;

    @Mock
    private UserElasticsearchRepository userElasticsearchRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Jedis jedis;

    List<User> users;

    @BeforeEach
    public void setUp() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        users = List.of(user1, user2);
    }

    @Test
    public void testSyncData() {
        String value = "1";

        when(userRepository.findAll()).thenReturn(users);
        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.get("1")).thenReturn(value);
        when(jedis.get("2")).thenReturn(null);

        dataSyncService.syncData();

        verify(userElasticsearchRepository).saveAll(any());
    }
}
