package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.UserService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUserById(){
        userController.getUserById(Mockito.anyLong());
        Mockito.verify(userService).getUserDtoById(Mockito.anyLong());
    }

    @Test
    void getUsersByIds(){
        userController.getUsersByIds(List.of());
        Mockito.verify(userService).getUsersByIds(Mockito.anyList());
    }

    @Test
    void uploadCsvUsers(){
        userController.uploadCsvUsers(Mockito.mock(MultipartFile.class));
        Mockito.verify(userService).uploadCsvUsers(Mockito.any(MultipartFile.class));
    }
}