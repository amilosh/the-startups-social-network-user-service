package school.faang.user_service.service.storage;

public interface StorageService {
    void uploadFile(String fileName, byte[] content, String contentType);

    void deleteFile(String fileName);

    boolean ifFileExists(String fileName) throws Exception;
}
