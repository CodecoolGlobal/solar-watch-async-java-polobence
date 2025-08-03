package com.codecool.solarwatch.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class AppConfiguration {

    private static final String SUNRISE_SUNSET_API_URL = "https://api.sunrise-sunset.org";
    private static final int TIMEOUT_SECONDS = 10;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(TIMEOUT_SECONDS));

        return WebClient.builder()
                .baseUrl(SUNRISE_SUNSET_API_URL)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
