package com.animesh.urlshortener.dto;

public class UrlResponseDto {

    private Long id;
    private String longUrl;
    private String shortCode;

    public UrlResponseDto(Long id, String longUrl, String shortCode) {
        this.id = id;
        this.longUrl = longUrl;
        this.shortCode = shortCode;
    }

    public Long getId() { return id; }
    public String getLongUrl() { return longUrl; }
    public String getShortCode() { return shortCode; }
}