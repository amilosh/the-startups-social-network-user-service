package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "diceBearClient", url = "https://api.dicebear.com/6.x/avataaars/svg")
public interface DiceBearClient {

    @GetMapping
    String getRandomAvatar(@RequestParam("seed") String seed);
}