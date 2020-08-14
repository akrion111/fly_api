package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LotApiApplication {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		return builder.build();
	}

	public static void main(String[] args)
	{
		System.out.println("no witam!");
		SpringApplication.run(LotApiApplication.class, args);
	}

}
