package school.faang.user_service.entity.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "users")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDocument {
    private Long id;
    private String username;
    private String aboutMe;
    private String country;
    private String city;
    private Integer experience;
    private List<String> skills;
    private Integer searchScore;
}
