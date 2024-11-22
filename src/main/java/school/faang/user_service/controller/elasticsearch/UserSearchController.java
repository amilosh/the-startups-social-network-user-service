package school.faang.user_service.controller.elasticsearch;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.document.UserDocument;
import school.faang.user_service.service.elasticsearch.UserSearchService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserSearchController {
    private final UserSearchService userSearchService;

    @GetMapping("/search")
    public List<UserDocument> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userSearchService.searchUsers(query, page, size);
    }
}
