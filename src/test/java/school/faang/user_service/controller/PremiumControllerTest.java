package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumController premiumController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(premiumController).build();
    }


    @Test
    void testBuyPremiumShouldReturnNotFoundWhenInvalidInput() throws Exception {
        mockMvc.perform(post("/user/-1/days/30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/user/1/days/-30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuyPremium() throws Exception {
        long userId = 1L;
        int days = 30;
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUsername("Test User");

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);

        PremiumDto mockPremiumDto = new PremiumDto();
        mockPremiumDto.setUserDto(userDto);
        mockPremiumDto.setStartDate(startDate);
        mockPremiumDto.setEndDate(endDate);

        when(premiumService.buyPremium(userId, premiumPeriod)).thenReturn(mockPremiumDto);

        mockMvc.perform(post("/premiums/user/{userId}/days/{days}", userId, days)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDto.id").value(userId))
                .andExpect(jsonPath("$.userDto.username").value("Test User"))
                .andExpect(jsonPath("$.startDate[0]").value(startDate.getYear()))
                .andExpect(jsonPath("$.startDate[1]").value(startDate.getMonthValue()))
                .andExpect(jsonPath("$.startDate[2]").value(startDate.getDayOfMonth()))
                .andExpect(jsonPath("$.endDate[0]").value(endDate.getYear()))
                .andExpect(jsonPath("$.endDate[1]").value(endDate.getMonthValue()))
                .andExpect(jsonPath("$.endDate[2]").value(endDate.getDayOfMonth()));
        verify(premiumService, times(1)).buyPremium(userId, premiumPeriod);
    }
}
