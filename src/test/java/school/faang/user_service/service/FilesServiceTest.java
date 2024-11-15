package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.FileDto;
import school.faang.user_service.entity.Files;
import school.faang.user_service.mapper.FileMapper;
import school.faang.user_service.repository.FilesRepository;
import school.faang.user_service.validator.FileValidator;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilesServiceTest {
    @Mock
    private FileValidator fileValidator;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MinioService minioService;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private FilesRepository fileRepository;

    @InjectMocks
    private FilesService filesService;

    @Test
    void createAvatarShouldReturnFileDto() {
        Long userId = 1L;
        String generatedSvg = "<svg>example</svg>";
        byte[] svgContent = generatedSvg.getBytes();
        String minioFileUrl = "http://minio.local/avatars/avatar-66b44634-57c3-43f9-a4c3-6dd510e491a0.svg";

        Files fileEntity = Files.builder()
                .id(1L)
                .userId(userId)
                .fileUrl(minioFileUrl)
                .build();

        FileDto expectedFileDto = new FileDto();
        expectedFileDto.setId(1L);
        expectedFileDto.setFileUrl(minioFileUrl);
        expectedFileDto.setUserId(userId);
        expectedFileDto.setUploadDate(fileEntity.getUploadDate());

        doNothing().when(fileValidator).validateFileExistence(userId);
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(generatedSvg);
        when(minioService.uploadSvgFileToMinio(anyString(), eq(svgContent))).thenReturn(minioFileUrl);
        when(fileRepository.save(any(Files.class))).thenReturn(fileEntity);
        when(fileMapper.toDto(any(Files.class))).thenReturn(expectedFileDto);

        FileDto actualFileDto = filesService.createAvatar(userId);

        verify(fileValidator, times(1)).validateFileExistence(userId);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
        verify(minioService, times(1)).uploadSvgFileToMinio(anyString(), eq(svgContent));
        verify(fileMapper, times(1)).toDto(fileEntity);
        assertEquals(expectedFileDto, actualFileDto);
    }

    @Test
    void getFileByUserIdShouldReturnFileDto() {
        Long userId = 1L;
        Files fileEntity = Files.builder()
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

        FileDto actualFileDto = filesService.getFileByUserId(userId);

        verify(fileRepository, times(1)).findByUserId(userId);
        verify(fileMapper, times(1)).toDto(fileEntity);
        assertEquals(expectedFileDto, actualFileDto);
    }
}