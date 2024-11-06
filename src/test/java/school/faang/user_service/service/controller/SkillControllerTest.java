package school.faang.user_service.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validation.skill.SkillValidation;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class SkillControllerTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("postgres")
        .withUsername("user")
        .withPassword("password");

    @BeforeAll
    public static void setUp() {
        postgres.start();
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @AfterAll
    public static void tearDown() {
        postgres.stop();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillController skillController;

    @MockBean
    private SkillService skillService;

    @MockBean
    private SkillValidation skillValidation;

    @MockBean
    private SkillMapper skillMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createSkillWithoutTitle() throws Exception {
        SkillDto invalidSkillDto = new SkillDto(null, null);

        mockMvc.perform(post("/api/skills/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSkillDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("title name cannot be blank"));
    }

    @Test
    public void createValidSkill() throws Exception {
        SkillDto SkillDtoJava = new SkillDto(null, "Test");

        doNothing().when(skillValidation).validateDuplicate(any());
        when(skillService.create(any(SkillDto.class))).thenReturn(SkillDtoJava);

        mockMvc.perform(post("/api/skills/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(SkillDtoJava)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Test")));

        verify(skillService, times(1)).create(any(SkillDto.class));
    }

    @Test
    public void testGetUserSkillsValidUserId() throws Exception {
        List<SkillDto> skills = Arrays.asList(
            new SkillDto(1L, "Java"),
            new SkillDto(2L, "Python")
        );

        when(skillService.getUserSkills(1L)).thenReturn(skills);

        mockMvc.perform(get("/api/skills/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].title", is("Java")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].title", is("Python")));

        verify(skillService, times(1)).getUserSkills(1L);
    }
}