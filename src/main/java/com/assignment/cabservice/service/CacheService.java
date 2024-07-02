package com.assignment.cabservice.service;// Service to access cache
import com.assignment.cabservice.config.CacheConfig;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CacheService {

    @Autowired
    private CacheConfig caffeineCache;

    public Map<String, Object> getAllCacheEntries() {
        Map<String, Object> entries = new HashMap<>();
        caffeineCache.caffeineCache().asMap().forEach((key, value) -> entries.put(key, value));
        return entries;
    }
}