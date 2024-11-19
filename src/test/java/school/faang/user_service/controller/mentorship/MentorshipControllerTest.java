package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {
    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @Test
    void testGetMentees() {
        UserDto userDto = UserDto.builder().build();
        when(mentorshipService.getMentees(anyLong())).thenReturn(List.of(userDto));

        List<UserDto> result = mentorshipController.getMentees(1L);

        verify(mentorshipService, times(1)).getMentees(anyLong());
        assertEquals(List.of(userDto), result);
    }

    @Test
    void testGetMentors() {
        UserDto userDto = UserDto.builder().build();
        when(mentorshipService.getMentors(anyLong())).thenReturn(List.of(userDto));

        List<UserDto> result = mentorshipController.getMentors(1L);

        verify(mentorshipService, times(1)).getMentors(anyLong());
        assertEquals(List.of(userDto), result);
    }

    @Test
    void testDeleteMentee() {
        mentorshipController.deleteMentee(1L, 2L);

        verify(mentorshipService, times(1)).deleteMentee(1L, 2L);
    }

    @Test
    void testDeleteMentor() {
        mentorshipController.deleteMentor(1L, 2L);

        verify(mentorshipService, times(1)).deleteMentor(1L, 2L);
    }
}