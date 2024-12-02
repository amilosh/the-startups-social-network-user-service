package school.faang.user_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.events.BanUserEvent;
import school.faang.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BanUserListenerTest {
    @InjectMocks
    private BanUserListener banUserListener;
    @Mock
    private UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private Message message;
    @Test
    void onMessageValidJsonCallsBanUser () throws JsonProcessingException {
        long userId = 1L;
        BanUserEvent banUserEvent = new BanUserEvent();
        banUserEvent.setUserId(userId);
        String jsonMessage = objectMapper.writeValueAsString(banUserEvent);
        when(message.getBody()).thenReturn(jsonMessage.getBytes());

        banUserListener.onMessage(message, null);

        verify(userService, times(1)).banUser(banUserEvent);
    }
}