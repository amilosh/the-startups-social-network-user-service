package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.FileDto;
import school.faang.user_service.entity.Files;
import school.faang.user_service.mapper.FileMapper;
import school.faang.user_service.repository.FilesRepository;
import school.faang.user_service.validator.FileValidator;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FilesService {
    private static final String DICEBEAR_API_URL = "https://api.dicebear.com/6.x/avataaars/svg";

    private final FilesRepository fileRepository;
    private final MinioService minioService;
    private final RestTemplate restTemplate;
    private final FileMapper fileMapper;
    private final FileValidator fileValidator;

    public FileDto createAvatar(Long userId) {
        fileValidator.validateFileExistence(userId);

        String fileUrl = generateRandomAvatar();
        String fileName = "avatar-" + UUID.randomUUID() + ".svg";

        byte[] svgContent = restTemplate.getForObject(fileUrl, String.class).getBytes();
        String minioFileUrl = minioService.uploadSvgFileToMinio(fileName, svgContent);

        return fileMapper.toDto(saveFile(userId, minioFileUrl));
    }

    public FileDto getFileByUserId(Long userId) {
        return fileMapper.toDto(fileRepository.findByUserId(userId));
    }

    private Files saveFile(Long userId, String fileUrl) {
        Files file = Files
                .builder()
                .userId(userId)
                .fileUrl(fileUrl)
                .uploadDate(LocalDateTime.now())
                .build();
        return fileRepository.save(file);
    }

    private String generateRandomAvatar() {
        return DICEBEAR_API_URL + "?seed=" + System.nanoTime();
    }
}
