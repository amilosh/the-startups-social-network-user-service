package school.faang.user_service.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.skill.SkillAcquireDtoNullObjectValidationException;
import school.faang.user_service.exception.skill.SkillDtoFieldConstraintValidationException;
import school.faang.user_service.exception.skill.SkillDtoNullObjectValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validation.skill.SkillAcquireDtoValidation;
import school.faang.user_service.validation.skill.SkillDtoValidation;
import school.faang.user_service.validation.skill.SkillValidation;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillController skillController;

    @MockBean
    private SkillService skillService;

    @MockBean
    private SkillDtoValidation skillDtoValidation;

    @MockBean
    private SkillAcquireDtoValidation skillAcquireDtoValidation;

    @MockBean
    private SkillValidation skillValidation;

    @MockBean
    private SkillMapper skillMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SkillDto emptySkillDto = new SkillDto();;
    private SkillAcquireDto emptySkillAcquireDto = new SkillAcquireDto();
    private SkillDto skillDtoWithoutTitle = new SkillDto(null, "");
    private SkillDto skillDtoExceedingSizeTitle = new SkillDto(null, "a".repeat(SkillDtoValidation.MAX_TITLE_LENGTH + 1));;
    private SkillDto SkillDtoJava = new SkillDto(null, "Test");

    @Test
    public void testCreateEmptySkill() throws Exception {
        doThrow(new SkillDtoNullObjectValidationException("Объект skillDto не может быть пустым!"))
            .when(skillDtoValidation).validate(emptySkillDto);

        mockMvc.perform(post("/api/skills/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptySkillDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Объект skillDto не может быть пустым!"));

        verify(skillDtoValidation, times(1)).validate(emptySkillDto);
        verify(skillService, never()).create(any(SkillDto.class));
    }

    @Test
    public void testAcquireNullSkillFromOffers() throws Exception {
        doThrow(new SkillAcquireDtoNullObjectValidationException("Объект skillAcquireDto не может быть пустым!"))
            .when(skillAcquireDtoValidation).validate(emptySkillAcquireDto);

        mockMvc.perform(post("/api/skills/acquireSkillFromOffers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptySkillAcquireDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Объект skillAcquireDto не может быть пустым!"));

        verify(skillAcquireDtoValidation, times(1)).validate(emptySkillAcquireDto);
        verify(skillService, never()).acquireSkillFromOffers(emptySkillAcquireDto);
    }

    @Test
    public void testCreateSkillWithoutTitle() throws Exception {
        doThrow(new SkillDtoNullObjectValidationException("Объект skillDto не может быть пустым!"))
            .when(skillDtoValidation).validate(skillDtoWithoutTitle);

        mockMvc.perform(post("/api/skills/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skillDtoWithoutTitle)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Объект skillDto не может быть пустым!"));

        verify(skillDtoValidation, times(1)).validate(skillDtoWithoutTitle);
        verify(skillService, never()).create(any(SkillDto.class));
    }

    @Test
    public void testCreateSkillExceedingSizeTitle() throws Exception {
        doThrow(new SkillDtoFieldConstraintValidationException("Наименование навыка превышает допустимое кол-во символов!"))
            .when(skillDtoValidation).validate(skillDtoExceedingSizeTitle);

        mockMvc.perform(post("/api/skills/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skillDtoExceedingSizeTitle)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Наименование навыка превышает допустимое кол-во символов!"));

        verify(skillDtoValidation, times(1)).validate(skillDtoExceedingSizeTitle);
        verify(skillService, never()).create(any(SkillDto.class));
    }

    @Test
    public void testGetOfferedSkillsInvalidSkillDto() throws Exception {
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillDtoWithoutTitle, null);

        doThrow(new SkillDtoFieldConstraintValidationException("Наименование навыка не может быть пустым!"))
            .when(skillDtoValidation).validate(any(SkillDto.class));

        mockMvc.perform(post("/api/skills/offeredSkills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skillCandidateDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Наименование навыка не может быть пустым!"));

        verify(skillDtoValidation, times(1)).validate(any(SkillDto.class));
        verify(skillService, never()).getOfferedSkills(any(SkillCandidateDto.class));
    }

    @Test
    public void testCreateSkillValid() throws Exception {
        doNothing().when(skillDtoValidation).validate(any(SkillDto.class));
        doNothing().when(skillValidation).validateDuplicate(any());
        when(skillService.create(any(SkillDto.class))).thenReturn(SkillDtoJava);

        mockMvc.perform(post("/api/skills/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(SkillDtoJava)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Test")));

        verify(skillDtoValidation, times(1)).validate(any(SkillDto.class));
        verify(skillService, times(1)).create(any(SkillDto.class));
    }

    @Test
    public void testAcquireSkillFromOffersNullSkillId() throws Exception {
        SkillAcquireDto skillAcquireDto = new SkillAcquireDto(null, 1L);

        doThrow(new SkillAcquireDtoNullObjectValidationException("Объект skillId не может быть пустым!"))
            .when(skillAcquireDtoValidation).validate(skillAcquireDto);

        mockMvc.perform(post("/api/skills/acquireSkillFromOffers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skillAcquireDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Объект skillId не может быть пустым!"));

        verify(skillAcquireDtoValidation, times(1)).validate(skillAcquireDto);
        verify(skillService, never()).acquireSkillFromOffers(any(SkillAcquireDto.class));
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