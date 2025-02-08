package iscae.mr.jwt_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(iscae.mr.jwt_spring_boot.JwtConfig.class)
@SpringBootApplication
public class JwtSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtSpringBootApplication.class, args);
	}

}
