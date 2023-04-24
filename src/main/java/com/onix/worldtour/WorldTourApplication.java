package com.onix.worldtour;

import com.onix.worldtour.dto.response.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableCaching
@RestController
@RequestMapping("/")
public class WorldTourApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorldTourApplication.class, args);
	}

	@GetMapping
	public Response<Object> greeting() {
		return Response.ok().setPayload("Welcome to World Tour!");
	}
}
