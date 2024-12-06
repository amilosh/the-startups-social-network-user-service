package school.faang.user_service.message.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.message.event.UsersBanEvent;
import school.faang.user_service.exceptions.MessageMappingException;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersBanListenerTest {

    @InjectMocks
    private UsersBanListener usersBanListener;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private Message message;

    @Test
    public void testOnMessage() throws IOException {
        // arrange
        List<Long> userIdsToBan = List.of(1L, 2L);
        UsersBanEvent usersBanEvent = new UsersBanEvent(userIdsToBan);
        byte[] messageBody = "{\"userIdsToBan\":[1,2]}".getBytes();

        when(message.getBody()).thenReturn(messageBody);
        when(objectMapper.readValue(messageBody, UsersBanEvent.class)).thenReturn(usersBanEvent);

        // act
        usersBanListener.onMessage(message, new byte[]{});

        // assert
        verify(userService).banUsers(userIdsToBan);
    }

    @Test
    public void testOnMessageThrowsMessageMappingException() throws IOException {
        // arrange
        List<Long> userIdsToBan = List.of(1L, 2L);
        byte[] messageBody = "{\"userIdsToBan\":[1,2]}".getBytes();

        when(message.getBody()).thenReturn(messageBody);
        doThrow(IOException.class)
                .when(objectMapper)
                .readValue(messageBody, UsersBanEvent.class);

        // act and assert
        assertThrows(MessageMappingException.class,
                () -> usersBanListener.onMessage(message, new byte[]{}));
    }
}
