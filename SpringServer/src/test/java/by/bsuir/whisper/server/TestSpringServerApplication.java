package by.bsuir.whisper.server;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

//@TestConfiguration(proxyBeanMethods = false)
public class TestSpringServerApplication {

    //    @Bean
//    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
	return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

//    public static void main(String[] args) {
//	SpringApplication.from(SpringServerApplication::main).with(TestSpringServerApplication.class).run(args);
//    }

}
