package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RecommendationRequestMapperTest {

    private RecommendationRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RecommendationRequestMapper.class);
    }

    @Test
    void testToEntity_Success() {
        RecommendationRequestDto dto = RecommendationRequestDto.builder()
                .id(1L)
                .message("Test message")
                .status(RequestStatus.PENDING)
                .requesterId(2L)
                .receiverId(3L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .rejectionReason("No reason")
                .skillIdentifiers(Arrays.asList(10L, 20L))
                .build();

        RecommendationRequest entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getMessage(), entity.getMessage());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
        assertEquals(dto.getRejectionReason(), entity.getRejectionReason());

        assertNull(entity.getSkills());
        assertNull(entity.getRequester());
        assertNull(entity.getReceiver());
        assertNull(entity.getRecommendation());
    }

    @Test
    void testToDto_Success() {
        User requester = User.builder()
                .id(2L)
                .build();

        User receiver = User.builder()
                .id(3L)
                .build();

        Skill skill1 = Skill.builder()
                .id(10L)
                .build();

        Skill skill2 = Skill.builder()
                .id(20L)
                .build();

        SkillRequest skillRequest1 = SkillRequest.builder()
                .skill(skill1)
                .build();

        SkillRequest skillRequest2 = SkillRequest.builder()
                .skill(skill2)
                .build();

        RecommendationRequest entity = RecommendationRequest.builder()
                .id(1L)
                .message("Test message")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .rejectionReason("No reason")
                .requester(requester)
                .receiver(receiver)
                .skills(Arrays.asList(skillRequest1, skillRequest2))
                .build();

        RecommendationRequestDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getMessage(), dto.getMessage());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
        assertEquals(entity.getRejectionReason(), dto.getRejectionReason());
        assertEquals(entity.getRequester().getId(), dto.getRequesterId());
        assertEquals(entity.getReceiver().getId(), dto.getReceiverId());
        assertEquals(Arrays.asList(10L, 20L), dto.getSkillIdentifiers());
    }

    @Test
    void testMapSkills_NullOrEmpty() {
        List<Long> resultNull = mapper.mapSkills(null);
        assertNull(resultNull);

        List<Long> resultEmpty = mapper.mapSkills(Arrays.asList());
        assertNull(resultEmpty);
    }
}
