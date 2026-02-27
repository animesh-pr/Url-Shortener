package com.animesh.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animesh.urlshortener.dto.UrlRequestDto;
import com.animesh.urlshortener.dto.UrlResponseDto;
import com.animesh.urlshortener.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public UrlResponseDto shortenUrl(@Valid @RequestBody UrlRequestDto request) {
        return urlService.createShortUrl(request);
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {
        return urlService.handleRedirect(shortCode, request);
    }

    @GetMapping("/analytics/{shortCode}")
    public long analytics(@PathVariable String shortCode) {
        return urlService.getAnalytics(shortCode);
    }
}