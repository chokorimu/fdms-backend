package fdms;

import fdms.data.Database;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FdmsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdmsApiApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData() {
        return args -> Database.seed();
    }
}
