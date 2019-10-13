package com.salil.shawrtn.dto;

import javax.validation.constraints.NotNull;

public class UrlShortenRequestDTO {

    @NotNull(message = "Long URL can not be empty")
    private String longUrl;


    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
