package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    public void testCreate() {
        SkillDto skillDto = SkillDto.builder().build();
        SkillDto expectedSkillDto = SkillDto.builder().build();

        when(skillService.create(any(SkillDto.class))).thenReturn(expectedSkillDto);

        SkillDto result = skillController.create(skillDto);

        assertEquals(expectedSkillDto, result);
        assertNotNull(result);

        verify(skillService, times(1)).create(any(SkillDto.class));
    }

    @Test
    public void testGetUserSkills() {
        SkillDto skillDto = SkillDto.builder().build();
        when(skillService.getUserSkills(anyLong())).thenReturn(List.of(skillDto));

        List<SkillDto> result = skillController.getUserSkills(1L);

        verify(skillService, times(1)).getUserSkills(anyLong());
        assertEquals(List.of(skillDto), result);
    }

    @Test
    public void testGetOfferedSkills() {
        SkillDto skillDto = SkillDto.builder().build();
        when(skillService.getOfferedSkills(anyLong())).thenReturn(List.of(skillDto));

        List<SkillDto> result = skillController.getOfferedSkills(1L);

        verify(skillService, times(1)).getOfferedSkills(anyLong());
        assertEquals(List.of(skillDto), result);
    }
}