package school.faang.user_service.service.test;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.eventService.EventParticipationService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    // Тест для получения списка участников
    @Test
    public void testGetListOfParticipants() {
        long eventId = 1L;
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(new UserDto(1L, "testUser1", "testUser1@mail.ru"));
        userDtos.add(new UserDto(2L, "testUser2", "testUser2@mail.ru"));

        // Мокаем метод getListOfParticipants
        Mockito.when(eventParticipationService.getListOfParticipants(eventId)).thenReturn(userDtos);

        // Вызываем метод контроллера
        ResponseEntity<List<UserDto>> responseEntity = eventParticipationController.getParticipants(eventId);

        // Проверяем статус и содержимое ответа
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(2, responseEntity.getBody().size());
        assertEquals("testUser1", responseEntity.getBody().get(0).getUsername());
        assertEquals("testUser2", responseEntity.getBody().get(1).getUsername());
    }

    @Test
    public void testGetListOfParticipants_NoParticipants() {
        long eventId = 1L;
        List<UserDto> userDtos = new ArrayList<>(); // Пустой список

        Mockito.when(eventParticipationService.getListOfParticipants(eventId)).thenReturn(userDtos);

        ResponseEntity<List<UserDto>> responseEntity = eventParticipationController.getParticipants(eventId);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(0, responseEntity.getBody().size());
    }

}
