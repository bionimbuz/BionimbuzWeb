package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApplicationGCP {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationGCP.class, args);
    }
}
