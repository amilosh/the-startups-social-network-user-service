package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapstruct.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    private User requester;
    private User receiver;
    private MentorshipRequest mentorshipRequest;
    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    public void setUp() {
        requester = new User();
        requester.setId(1L);
        requester.setSentMentorshipRequests(new ArrayList<>());
        requester.setMentors(new ArrayList<>());

        receiver = new User();
        receiver.setId(2L);

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setDescription("Test Description");
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
        mentorshipRequestDto.setDescription("Test Description");
    }

    @Test
    public void testRequestMentorship_Success() {

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setDescription("Test Description");
        mentorshipRequest.setCreatedAt(LocalDateTime.now());
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(mentorshipRequestRepository.save(any(MentorshipRequest.class))).thenReturn(mentorshipRequest);
        when(mentorshipRequestMapper.mapToDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        when(mentorshipRequestMapper.mapToEntity(mentorshipRequestDto)).thenReturn(mentorshipRequest);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        assertNotNull(result);
        assertEquals(mentorshipRequestDto, result);
        verify(mentorshipRequestRepository, times(1)).save(any(MentorshipRequest.class));
    }

    @Test
    public void testRequestMentorship_SelfRequest_ThrowsException() {
        mentorshipRequestDto.setReceiverId(1L);

        assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
    }

    @Test
    public void testGetRequests_ByStatus() {


        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest));
        when(mentorshipRequestMapper.mapToDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mentorshipRequestDto, result.get(0));
    }

    @Test
    public void testAcceptMentorship_Success() {
        mentorshipRequest.setDescription("Sample description");
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(userRepository.save(requester)).thenReturn(requester);

        MentorshipRequestDto dto = new MentorshipRequestDto(
                1L,
                mentorshipRequest.getRequester().getId(),
                mentorshipRequest.getReceiver().getId(),
                mentorshipRequest.getDescription(),
                RequestStatus.ACCEPTED,
                null
        );

        when(mentorshipRequestMapper.mapToDto(mentorshipRequest)).thenReturn(dto);

        MentorshipRequestDto result = mentorshipRequestService.acceptMentorship(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
        assertTrue(requester.getMentors().contains(receiver));
    }

    @Test
    public void testAcceptMentorship_AlreadyMentor_ThrowsException() {
        requester.getMentors().add(receiver);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.acceptMentorship(1L);
        });
    }

    @Test
    public void testRejectRequest_Success() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Test Reason");

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestMapper.mapToDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.rejectRequest(1L, rejectionDto);

        assertNotNull(result);
        assertEquals(mentorshipRequestDto, result);
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("Test Reason", mentorshipRequest.getRejectionReason());
    }
}
