package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.dto.FileDto;
import school.faang.user_service.entity.File;
import school.faang.user_service.mapper.FileMapper;
import school.faang.user_service.repository.FileRepository;
import school.faang.user_service.validator.FileValidator;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final MinioService minioService;
    private final DiceBearClient diceBearClient;
    private final FileMapper fileMapper;
    private final FileValidator fileValidator;

    public FileDto generateAndStoreAvatar(Long userId) {
        fileValidator.validateFileExistence(userId);

        String seed = UUID.randomUUID().toString();
        String avatarSvg = diceBearClient.getRandomAvatar(seed);

        String fileName = "avatar-" + userId + "-" + seed + ".svg";
        byte[] svgContent = avatarSvg.getBytes();
        String fileUrl = minioService.uploadSvgFileToMinio(fileName, svgContent);

        return fileMapper.toDto(saveFile(userId, fileUrl));
    }

    public FileDto getFileByUserId(Long userId) {
        return fileMapper.toDto(fileRepository.findByUserId(userId));
    }

    private File saveFile(Long userId, String fileUrl) {
        File file = File
                .builder()
                .userId(userId)
                .fileUrl(fileUrl)
                .uploadDate(LocalDateTime.now())
                .build();
        return fileRepository.save(file);
    }
}
