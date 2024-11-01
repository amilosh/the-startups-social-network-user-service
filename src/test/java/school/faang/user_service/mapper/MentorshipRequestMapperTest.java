package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.TestDataCreator;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;

class MentorshipRequestMapperTest {

    private MentorshipRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MentorshipRequestMapper.class);
    }

    @Test
    void testToDto() {
        User requester = TestDataCreator.createUser(1L);
        User receiver = TestDataCreator.createUser(2L);
        MentorshipRequest request = TestDataCreator.createMentorshipRequest(1L, requester, receiver,
                ACCEPTED, "Help me with java!");

        MentorshipRequestDto dto = mapper.toDto(request);

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getRequester().getId(), dto.getRequesterId());
        assertEquals(request.getReceiver().getId(), dto.getReceiverId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getStatus(), dto.getStatus());
    }

    @Test
    void toEntity() {
        MentorshipRequestDto dto = TestDataCreator.createMentorshipRequestDto(1L, 1L, 2L, ACCEPTED, "H");

        MentorshipRequest request = mapper.toEntity(dto);

        assertEquals(dto.getId(), request.getId());
        assertEquals(dto.getStatus(), request.getStatus());
        assertEquals(dto.getDescription(), request.getDescription());
        assertNull(request.getRequester());
        assertNull(request.getReceiver());
    }
}