package by.bsuir.whisper.server;

import by.bsuir.whisper.server.context.CatchThrowsBeanPostProcessor;
import by.bsuir.whisper.server.context.PasswordGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class SpringServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringServerApplication.class, args);
    }

    @Bean
    public CatchThrowsBeanPostProcessor beanPostProcessor() {
        return new CatchThrowsBeanPostProcessor();
    }

    @Bean
    public Function<String, PasswordGenerator> generator() {
        return (password) -> new PasswordGenerator() {
            @Override
            public String getSalt() {
                return "123";//or any random string
            }

            @Override
            public String getHash() {
                return "123";
            }
        };
    }
}
