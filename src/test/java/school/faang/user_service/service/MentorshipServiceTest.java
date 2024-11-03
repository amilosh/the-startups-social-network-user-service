package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.exception.DataValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Test
    public void testGetMenteesWithNegativeId() {
        User user = prepareData(-1212, new ArrayList<>(), new ArrayList<>());
        UserDto userDto = userMapper.toDto(user);
        String expectedMessage = "User's id cannot be less than zero";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(userDto.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetMenteesWithNonExistentId() {
        User user = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        UserDto userDto = userMapper.toDto(user);
        String expectedMessage = "User not found";
        when(mentorshipRepository.findById(userDto.getId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(userDto.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetMenteesWithEmptyList() {
        User user = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        UserDto userDto = userMapper.toDto(user);
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        List<UserDto> mentees = mentorshipService.getMentees(userDto.getId());

        assertEquals(0, mentees.size());
    }

    @Test
    public void testGetMenteesWithFilledList() {
        User firstUser = prepareData(1, new ArrayList<>(), new ArrayList<>());
        User secondUser = prepareData(2, new ArrayList<>(), new ArrayList<>());
        User user = prepareData(1212, new ArrayList<>(List.of(firstUser, secondUser)), new ArrayList<>());
        UserDto userDto = userMapper.toDto(user);
        List<Long> expectedMentees = new ArrayList<>(List.of(1L, 2L));
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(mentorshipRepository.findAllById(userDto.getMenteeIds()))
                .thenReturn(new ArrayList<>(List.of(firstUser, secondUser)));

        List<UserDto> mentees = mentorshipService.getMentees(userDto.getId());

        assertEquals(expectedMentees.size(), mentees.size());
    }

    @Test
    public void testGetMentorsWithNegativeId() {
        User user = prepareData(-1212, new ArrayList<>(), new ArrayList<>());
        UserDto userDto = userMapper.toDto(user);
        String expectedMessage = "User's id cannot be less than zero";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(userDto.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetMentorsWithNonExistentId() {
        User user = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        UserDto userDto = userMapper.toDto(user);
        String expectedMessage = "User not found";
        when(mentorshipRepository.findById(userDto.getId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(userDto.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetMentorsWithEmptyList() {
        User user = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        UserDto userDto = userMapper.toDto(user);
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        List<UserDto> mentors = mentorshipService.getMentors(userDto.getId());

        assertEquals(0, mentors.size());
    }

    @Test
    public void testGetMentorsWithFilledList() {
        User firstUser = prepareData(1, new ArrayList<>(), new ArrayList<>());
        User secondUser = prepareData(2, new ArrayList<>(), new ArrayList<>());
        User user = prepareData(1212, new ArrayList<>(), new ArrayList<>(List.of(firstUser, secondUser)));
        UserDto userDto = userMapper.toDto(user);
        List<Long> expectedMentors = new ArrayList<>(List.of(1L, 2L));
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(mentorshipRepository.findAllById(userDto.getMentorIds()))
                .thenReturn(new ArrayList<>(List.of(firstUser, secondUser)));

        List<UserDto> mentors = mentorshipService.getMentors(userDto.getId());

        assertEquals(expectedMentors.size(), mentors.size());
    }

    @Test
    public void testDeleteMenteeWithNegativeMentorId() {
        User mentor = prepareData(-1212, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User's id cannot be less than zero";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testDeleteMenteeWithNonExistentMentorId() {
        User mentor = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1313, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User not found";
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testDeleteMenteeFromSingleValueList() {
        User mentee = prepareData(1313, new ArrayList<>(), new ArrayList<>());
        User mentor = prepareData(1212, new ArrayList<>(List.of(mentee)), new ArrayList<>());
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));

        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());
        List<User> mentees = mentor.getMentees();

        assertEquals(0, mentees.size());
        verify(mentorshipRepository, times(1)).save(mentor);
    }

    @Test
    public void testDeleteMenteeFromListWithMultipleValues() {
        User firstMentee = prepareData(1313, new ArrayList<>(), new ArrayList<>());
        User secondMentee = prepareData(1414, new ArrayList<>(), new ArrayList<>());
        User mentor = prepareData(1212, new ArrayList<>(List.of(firstMentee, secondMentee)), new ArrayList<>());
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));

        mentorshipService.deleteMentee(firstMentee.getId(), mentor.getId());
        List<User> mentees = mentor.getMentees();

        assertEquals(1, mentees.size());
        verify(mentorshipRepository, times(1)).save(mentor);
    }

    @Test
    public void testDeleteMentorWithNegativeMenteeId() {
        User mentor = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(-1212, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User's id cannot be less than zero";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testDeleteMentorWithNonExistentMenteeId() {
        User mentor = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1313, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User not found";
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testDeleteMentorFromSingleValueList() {
        User mentor = prepareData(1212, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1313, new ArrayList<>(), new ArrayList<>(List.of(mentor)));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());
        List<User> mentors = mentee.getMentors();

        assertEquals(0, mentors.size());
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    @Test
    public void testDeleteMentorFromListWithMultipleValues() {
        User firstMentor = prepareData(1313, new ArrayList<>(), new ArrayList<>());
        User secondMentor = prepareData(1414, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1212, new ArrayList<>(), new ArrayList<>(List.of(firstMentor, secondMentor)));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentor(mentee.getId(), secondMentor.getId());
        List<User> mentors = mentee.getMentors();

        assertEquals(1, mentors.size());
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    private User prepareData(long userId, List<User> mentees, List<User> mentors) {
        User user = new User();
        user.setId(userId);
        user.setMentees(mentees);
        user.setMentors(mentors);
        return user;
    }
}
