package school.faang.user_service.config.openApi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "User Service API",
                version = "1.0.0",
                description = "This service is responsible for managing users, their skills, goals, subscriptions, events, and mentorships.",
                contact = @Contact(
                        name = "BobrAll",
                        email = "BobrAll@example.com"
                )
        ),
        servers = {
                @Server(url = "/api/v1")
        }
)
public class OpenApiConfig {

}