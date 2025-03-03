package org.example.securetracks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync

public class SecuretracksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuretracksApplication.class, args);
	}

}
