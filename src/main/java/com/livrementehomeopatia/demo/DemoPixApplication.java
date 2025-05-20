package com.livrementehomeopatia.demo;

import com.livrementehomeopatia.demo.pix.PixConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@EnableConfigurationProperties(PixConfig.class)
@SpringBootApplication
public class DemoPixApplication {

    public static void main(String[] args) throws IOException {
        Path certPath = Paths.get("C:\\Users\\sergi\\src\\repos\\projetos\\api-pix\\.cert\\producao-765450-livrementehomeopatia.p12");
        System.out.println("Certificado existe? " + Files.exists(certPath));
        System.out.println("Tamanho: " + Files.size(certPath) + " bytes");
        SpringApplication.run(DemoPixApplication.class, args);
    }

}
