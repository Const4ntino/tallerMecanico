package com.example.gestion.taller_mecanico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class TallerMecanicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TallerMecanicoApplication.class, args);
	}

}