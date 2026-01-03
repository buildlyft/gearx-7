package com.gearx7.app.config;

import com.cloudinary.Cloudinary;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryConfig {

    private String cloud_name;

    private String api_key;

    private String api_secret;

    public void setCloud_name(String cloud_name) {
        this.cloud_name = cloud_name;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public void setApi_secret(String api_secret) {
        this.api_secret = api_secret;
    }

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of("cloud_name", cloud_name, "api_key", api_key, "api_secret", api_secret));
    }
}
