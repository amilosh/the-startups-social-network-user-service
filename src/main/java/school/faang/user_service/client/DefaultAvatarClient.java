package school.faang.user_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "default-avatar-client", url = "${default-avatar-client.url}/${default-avatar-client.ver}")
public interface DefaultAvatarClient {

    @GetMapping("/{styleName}/{format}")
    byte[] getAvatar(@PathVariable("styleName") String styleName, @PathVariable("format") String format,
                     @RequestParam List<String> backgroundColor);
}
