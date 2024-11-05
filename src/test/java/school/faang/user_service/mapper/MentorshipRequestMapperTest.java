package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MentorshipRequestMapperTest {

    private MentorshipRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MentorshipRequestMapper.class);
    }

    @Test
    void testToDto() {
        User requester = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        MentorshipRequest request = MentorshipRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.ACCEPTED)
                .description("Description")
                .build();

        MentorshipRequestDto dto = mapper.toDto(request);

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getRequester().getId(), dto.getRequesterId());
        assertEquals(request.getReceiver().getId(), dto.getReceiverId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getStatus(), dto.getStatus());
    }

    @Test
    void toEntity() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .description("Description")
                .status(RequestStatus.PENDING)
                .build();

        MentorshipRequest request = mapper.toEntity(dto);

        assertEquals(dto.getId(), request.getId());
        assertEquals(dto.getStatus(), request.getStatus());
        assertEquals(dto.getDescription(), request.getDescription());
        assertNull(request.getRequester());
        assertNull(request.getReceiver());
    }
}