//package school.faang.user_service.config.thread;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Configuration
//public class ThreadConfig {
//    @Value("${app.user-service.thread-amount}")
//    private int THREAD_POOL_SIZE;
//
//    @Bean(name = "taskExecutor")
//    public ExecutorService taskExecutor() {
//        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//    }
//}
