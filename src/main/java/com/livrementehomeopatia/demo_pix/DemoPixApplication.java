package com.livrementehomeopatia.demo_pix;

import com.livrementehomeopatia.demo_pix.pix.PixConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableConfigurationProperties(PixConfig.class)
@SpringBootApplication
public class DemoPixApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoPixApplication.class, args);
	}

}
