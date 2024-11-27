package school.faang.user_service.controller.skill;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @InjectMocks
    private SkillController controller;

    @Mock
    private SkillService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private SkillDto skillDto;
    private final long skillId = 1L;
    private final long userId = 10L;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        skillDto = new SkillDto();
        skillDto.setId(skillId);
        skillDto.setTitle("test");
    }

    @Test
    public void testCreateSkill() throws Exception {
        when(service.create(skillDto)).thenReturn(skillDto);

        mockMvc.perform(post("/api/v1/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillDto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("title").value("test"));
    }

    @Test
    public void testGetUserSkills() throws Exception {
        when(service.getUserSkills(userId)).thenReturn(Collections.singletonList(skillDto));

        mockMvc.perform(get("/api/v1/skills/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillDto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("test"));
    }

    @Test
    public void testGetOfferedSkills() throws Exception {
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
        skillCandidateDto.setSkill(skillDto);
        skillCandidateDto.setOffersAmount(20L);
        when(service.getOfferedSkills(userId)).thenReturn(Collections.singletonList(skillCandidateDto));

        mockMvc.perform(get("/api/v1/skills/users/{userId}/offered-skills", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillDto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].skill.id").value("1"))
                .andExpect(jsonPath("$[0].skill.title").value("test"))
                .andExpect(jsonPath("$[0].offersAmount").value("20"));
    }

    @Test
    public void testAcquireSkillFromOffers() throws Exception {
        when(service.acquireSkillFromOffers(skillId, userId)).thenReturn(skillDto);

        mockMvc.perform(post("/api/v1/skills/users/{userId}/acquire/{skillId}", userId, skillId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillDto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("title").value("test"));
    }
}
