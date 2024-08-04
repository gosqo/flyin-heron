package com.vong.manidues;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ManiDues {
    public static void main(String[] args) {
        SpringApplication.run(ManiDues.class, args);
    }
}
