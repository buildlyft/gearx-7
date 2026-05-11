package com.gearx7.app.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationIQResponseDTO {

    @JsonProperty("display_name")
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "LocationIQResponseDTO{" + "displayName='" + displayName + '\'' + '}';
    }
}
