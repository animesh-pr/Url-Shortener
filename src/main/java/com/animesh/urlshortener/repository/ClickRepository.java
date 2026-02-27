package com.animesh.urlshortener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animesh.urlshortener.model.UrlClick;

public interface ClickRepository extends JpaRepository<UrlClick, Long> {

    List<UrlClick> findByShortCode(String shortCode);

    long countByShortCode(String shortCode);
}