package school.faang.user_service.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ImageProcessingException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ImageUtils {

    public InputStream bufferedImageToInputStream(BufferedImage image, MultipartFile file) {
        log.debug("Trying to get BufferedImage InputStream");

        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, extension, outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new ImageProcessingException("An error occurred when converting resized image to an InputStream");
        }
    }

    public BufferedImage resizeImage(BufferedImage bufferedImage, int maxSideSize) {
        log.debug("Trying to resize image");
        try {
            return Thumbnails.of(bufferedImage)
                    .size(maxSideSize, maxSideSize)
                    .asBufferedImage();
        } catch (IOException ex) {
            throw new ImageProcessingException("An error occurred when converting MultiPartFile to BufferedImage");
        }
    }

    public BufferedImage convertMultiPartFileToBufferedImage(MultipartFile file) {
        log.debug("Trying to convert multipart file to buffered image");
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException ex) {
            throw new ImageProcessingException("An error occurred when converting MultiPartFile to BufferedImage");
        }
    }

}
