package school.faang.user_service.service.storage;

import org.springframework.core.io.Resource;

public interface StorageService {
    void uploadFile(String fileName, byte[] content, String contentType) throws Exception;

    void deleteFile(String fileName) throws Exception;

    Resource downloadFile(String fileName) throws Exception;

    boolean fileExists(String fileName) throws Exception;
}
