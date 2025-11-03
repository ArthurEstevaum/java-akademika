package com.coda_fofos.java_akademika;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // <-- 1. Adicione este import

@SpringBootApplication
@EntityScan("enities") // <-- 2. Adicione esta linha
public class JavaAkademikaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaAkademikaApplication.class, args);
    }

}