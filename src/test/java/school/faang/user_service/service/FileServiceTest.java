package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.dto.FileDto;
import school.faang.user_service.entity.File;
import school.faang.user_service.mapper.FileMapper;
import school.faang.user_service.repository.FileRepository;
import school.faang.user_service.validator.FileValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @Mock
    private FileValidator fileValidator;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MinioService minioService;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private DiceBearClient diceBearClient;

    @InjectMocks
    private FileService fileService;

    @Test
    void generateAndStoreAvatarShouldGenerateAndStoreAvatarSuccessfully() {
        Long userId = 1L;
        String avatarSvg = "<svg>example</svg>";
        String fileUrl = "https://minio.example.com/bucket/avatar-1-test-seed.svg";

        File savedFile = File.builder().userId(userId).fileUrl(fileUrl).build();
        FileDto expectedDto = new FileDto();
        expectedDto.setFileUrl(savedFile.getFileUrl());
        expectedDto.setUserId(savedFile.getUserId());

        doNothing().when(fileValidator).validateFileExistence(userId);
        when(diceBearClient.getRandomAvatar(anyString())).thenReturn(avatarSvg);
        when(minioService.uploadSvgFileToMinio(anyString(), any(byte[].class))).thenReturn(fileUrl);
        when(fileRepository.save(any(File.class))).thenReturn(savedFile);
        when(fileMapper.toDto(savedFile)).thenReturn(expectedDto);

        FileDto result = fileService.generateAndStoreAvatar(userId);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(fileValidator, times(1)).validateFileExistence(userId);
        verify(diceBearClient, times(1)).getRandomAvatar(anyString());
        verify(minioService, times(1)).uploadSvgFileToMinio(anyString(), any(byte[].class));
        verify(fileRepository, times(1)).save(any(File.class));
        verify(fileMapper, times(1)).toDto(savedFile);
    }

    @Test
    void getFileByUserIdShouldReturnFileDto() {
        Long userId = 1L;
        File fileEntity = File.builder()
                .id(1L)
                .userId(userId)
                .fileUrl("http://example.com/file.svg")
                .build();

        FileDto expectedFileDto = new FileDto();
        expectedFileDto.setId(1L);
        expectedFileDto.setFileUrl("http://example.com/file.svg");
        expectedFileDto.setUserId(userId);

        when(fileRepository.findByUserId(userId)).thenReturn(fileEntity);
        when(fileMapper.toDto(fileEntity)).thenReturn(expectedFileDto);

        FileDto actualFileDto = fileService.getFileByUserId(userId);

        verify(fileRepository, times(1)).findByUserId(userId);
        verify(fileMapper, times(1)).toDto(fileEntity);
        assertEquals(expectedFileDto, actualFileDto);
    }
}