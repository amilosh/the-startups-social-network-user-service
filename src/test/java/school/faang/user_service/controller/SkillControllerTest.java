package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validation.skill.SkillValidation;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private SkillDto skillDto;
    private List<SkillDto> skillList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(skillController).build();
        objectMapper = new ObjectMapper();

        skillDto = SkillDto.builder()
                .id(1L)
                .title("Java")
                .build();

        skillList = List.of(
                skillDto,
                SkillDto.builder()
                        .id(2L)
                        .title("Spring")
                        .build()
        );
    }

    @Test
    void testCreateSkill() throws Exception {
        when(skillService.create(skillDto)).thenReturn(skillDto);

        mockMvc.perform(post("/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(skillDto.getId()))
                .andExpect(jsonPath("$.title").value(skillDto.getTitle()));

        verify(skillService, times(1)).create(skillDto);
    }

    @Test
    void testGetUserSkills() throws Exception {
        long userId = 1L;
        when(skillService.getUserSkills(userId)).thenReturn(skillList);

        mockMvc.perform(get("/skills/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(skillList.get(0).getId()))
                .andExpect(jsonPath("$[0].title").value(skillList.get(0).getTitle()))
                .andExpect(jsonPath("$[1].id").value(skillList.get(1).getId()))
                .andExpect(jsonPath("$[1].title").value(skillList.get(1).getTitle()));

        verify(skillService, times(1)).getUserSkills(userId);
    }

    @Test
    void testGetOfferedSkills() throws Exception {
        long userId = 1L;
        when(skillService.getOfferedSkills(userId)).thenReturn(skillList);

        mockMvc.perform(get("/skills/users/{userId}/offers", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(skillList.get(0).getId()))
                .andExpect(jsonPath("$[0].title").value(skillList.get(0).getTitle()))
                .andExpect(jsonPath("$[1].id").value(skillList.get(1).getId()))
                .andExpect(jsonPath("$[1].title").value(skillList.get(1).getTitle()));

        verify(skillService, times(1)).getOfferedSkills(userId);
    }
}
