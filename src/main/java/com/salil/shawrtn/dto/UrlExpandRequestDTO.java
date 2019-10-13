package com.salil.shawrtn.dto;

import javax.validation.constraints.NotNull;

public class UrlExpandRequestDTO {

    @NotNull(message = "Short URL can not be empty")
    private String shortUrl;

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
