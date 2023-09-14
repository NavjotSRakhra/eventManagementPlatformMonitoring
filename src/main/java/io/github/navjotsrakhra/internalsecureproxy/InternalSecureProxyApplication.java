package io.github.navjotsrakhra.internalsecureproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InternalSecureProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternalSecureProxyApplication.class, args);
    }

}
