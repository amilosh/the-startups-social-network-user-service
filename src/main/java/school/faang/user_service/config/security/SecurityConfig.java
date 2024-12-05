package school.faang.user_service.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        SecurityContextRepository repo = new HttpSessionSecurityContextRepository();

        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/users/google/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .httpBasic()
                .and()
                .oauth2Login()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().migrateSession()
                .and()
                .securityContext(context -> context.securityContextRepository(repo));

        return http.build();
    }
}
