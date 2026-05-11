package com.gearx7.app.service;

import com.gearx7.app.service.dto.LocationIQResponseDTO;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LocationIQService {

    private final Logger log = LoggerFactory.getLogger(LocationIQService.class);

    private final WebClient webClient;

    @Value("${locationiq.api-key}")
    private String apiKey;

    public LocationIQService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public String getAddress(Double lat, Double lon) {
        try {
            LocationIQResponseDTO response = webClient
                .get()
                .uri(uriBuilder ->
                    uriBuilder
                        .scheme("https")
                        .host("us1.locationiq.com")
                        .path("/v1/reverse")
                        .queryParam("key", apiKey)
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("format", "json")
                        .build()
                )
                .retrieve()
                .bodyToMono(LocationIQResponseDTO.class)
                .timeout(Duration.ofSeconds(5))
                .block();

            return response != null ? response.getDisplayName() : null;
        } catch (Exception ex) {
            log.error("Failed to fetch address from LocationIQ", ex);

            return "Address not available";
        }
    }
}
