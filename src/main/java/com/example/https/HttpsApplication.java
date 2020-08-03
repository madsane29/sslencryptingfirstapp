package com.example.https;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEncryptableProperties
@EnableSwagger2
public class HttpsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpsApplication.class, args);

	}
}
