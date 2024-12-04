package school.faang.user_service.controller.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.service.MentorshipService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    private long userId = 1L;
    private long deleteUserId = 2L;
    Long menteeFirstId = 2L;
    Long menteeSecondId = 3L;
    Long menteeThirdId = 4L;
    Long mentorFirstId = 5L;
    Long mentorSecondId = 6L;
    Long mentorThirdId = 7L;
    private UserDto userDto;
    private UserDto userDtoFirst;
    private UserDto userDtoSecond;
    private List<UserDto> userDtos;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        userDto = mockUserDto();
        userDtoFirst = mockUser1();
        userDtoSecond = mockUser2();
        userDtos = Arrays.asList(userDtoFirst, userDtoSecond);
    }

    @Test
    @DisplayName("Get mentees success")
    void testGetMenteesSuccess() throws Exception {
        when(mentorshipService.getMentees(userId)).thenReturn(userDtos);

        mockMvc.perform(get("/mentorship/users/{userId}/mentees", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userDtoFirst.getId()));

        verify(mentorshipService, times(1)).getMentees(userId);
    }

    @Test
    @DisplayName("Get mentees failure - User not found")
    void testGetMenteesFail() throws Exception {
        doThrow(new EntityNotFoundException("User with ID " + userId + " not found"))
                .when(mentorshipService).getMentees(userId);

        mockMvc.perform(get("/mentorship/users/{userId}/mentees", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with ID " + userId + " not found"));
        verify(mentorshipService, times(1)).getMentees(userId);
    }

    @Test
    @DisplayName("Get mentors success")
    void testGetMentorsSuccess() throws Exception {
        when(mentorshipService.getMentors(userId)).thenReturn(userDtos);

        mockMvc.perform(get("/mentorship/users/{userId}/mentors", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userDtoFirst.getId()));

        verify(mentorshipService, times(1)).getMentors(userId);
    }

    @Test
    @DisplayName("Get mentors failure - User not found")
    void testGetMentorsFail() throws Exception {
        when(mentorshipService.getMentors(userId))
                .thenThrow(new EntityNotFoundException("User with ID " + userId + " not found"));

        mockMvc.perform(get("/mentorship/users/{userId}/mentors", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with ID " + userId + " not found"));

        verify(mentorshipService, times(1)).getMentors(userId);
    }

    @Test
    @DisplayName("Delete Mentee success")
    void testDeleteMenteeSuccess() throws Exception {
        doNothing().when(mentorshipService).deleteMentee(userId, deleteUserId);

        mockMvc.perform(delete("/mentorship/mentors/{mentorId}/mentees/{menteeId}", userId, deleteUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(mentorshipService, times(1)).deleteMentee(userId, deleteUserId);
    }

    @Test
    @DisplayName("Delete Mentee when mentor not found")
    void testDeleteMenteeWhenMentorNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User with ID " + deleteUserId + " not found"))
                .when(mentorshipService)
                .deleteMentee(userId, deleteUserId);

        mockMvc.perform(delete("/mentorship/mentors/{mentorId}/mentees/{menteeId}", userId, deleteUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(mentorshipService).deleteMentee(userId, deleteUserId);
    }

    @Test
    @DisplayName("Delete Mentor success")
    void testDeleteMentorSuccess() throws Exception {
        doNothing().when(mentorshipService).deleteMentor(userId, deleteUserId);

        mockMvc.perform(delete("/mentorship/mentees/{menteeId}/mentors/{mentorId}", userId, deleteUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(mentorshipService, times(1)).deleteMentor(userId, deleteUserId);
    }

    @Test
    @DisplayName("Delete Mentor when mentor not found")
    void testDeleteMentorWhenMentorNotFound() throws Exception {
        String expectedErrorMessage = "User with ID " + deleteUserId + " not found";
        doThrow(new EntityNotFoundException(expectedErrorMessage))
                .when(mentorshipService).deleteMentor(userId, deleteUserId);

        mockMvc.perform(delete("/mentorship/mentees/{menteeId}/mentors/{mentorId}", userId, deleteUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedErrorMessage));

        verify(mentorshipService, times(1)).deleteMentor(userId, deleteUserId);
    }

    private UserDto mockUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("User1")
                .menteesId(List.of(menteeFirstId, menteeSecondId, menteeThirdId))
                .mentorsId(List.of(mentorFirstId, mentorSecondId, mentorThirdId))
                .build();
    }

    private UserDto mockUser1() {
        return UserDto.builder()
                .id(2L)
                .username("Mentee1")
                .menteesId(List.of(menteeFirstId, menteeSecondId, menteeThirdId))
                .mentorsId(List.of(mentorFirstId, mentorSecondId, mentorThirdId))
                .build();
    }

    private UserDto mockUser2() {
        return UserDto.builder()
                .id(3L)
                .username("Mentee1")
                .menteesId(List.of(menteeFirstId, menteeSecondId, menteeThirdId))
                .mentorsId(List.of(mentorFirstId, mentorSecondId, mentorThirdId))
                .build();
    }
}
