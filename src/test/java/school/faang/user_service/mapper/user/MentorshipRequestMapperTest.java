package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MentorshipRequestMapperTest {

    private final MentorshipRequestMapper mapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Test
    public void testToDto() {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();

        User requester = new User();
        requester.setId(1L);
        mentorshipRequest.setRequester(requester);

        User receiver = new User();
        receiver.setId(2L);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setDescription("Description");

        MentorshipRequestDto dto = mapper.toDto(mentorshipRequest);

        assertThat(dto).isNotNull();
        assertThat(dto.requesterId()).isEqualTo(1L);
        assertThat(dto.receiverId()).isEqualTo(2L);
        assertThat(dto.description()).isEqualTo("Description");

    }
    @Test
    public void testToEntity() {
        MentorshipRequestDto mentorshipRequestDTO = new MentorshipRequestDto("Description", 1L, 2L);

        MentorshipRequest mentorshipRequest = mapper.toEntity(mentorshipRequestDTO);
        assertThat(mentorshipRequest).isNotNull();
        assertThat(mentorshipRequest.getRequester().getId()).isEqualTo(1L);
        assertThat(mentorshipRequest.getReceiver().getId()).isEqualTo(2L);
        assertThat(mentorshipRequest.getDescription()).isEqualTo("Description");
    }
}