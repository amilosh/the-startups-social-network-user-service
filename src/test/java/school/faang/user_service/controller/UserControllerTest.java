package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String csvContent;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        csvContent = "firstName,lastName,email,phone,city,state,country\n"
                + "John,Doe,johndoe@example.com,123456789,New York,NY,USA";
        file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
    }

    @Test
    void getUserWhenUserExistsShouldReturnUser() throws Exception {
        long userId = 1L;
        UserDto dto = new UserDto();
        dto.setId(userId);

        when(userService.findUserDtoById(userId)).thenReturn(dto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).findUserDtoById(userId);
    }

    @Test
    void testUploadToCsvSuccess() throws Exception {
        ProcessResultDto mockResult = new ProcessResultDto(1, List.of());
        when(userService.importUsersFromCsv(any(InputStream.class))).thenReturn(mockResult);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.сountSuccessfullySavedUsers").value(1))
                .andExpect(jsonPath("$.errors").isEmpty());
    }
}
