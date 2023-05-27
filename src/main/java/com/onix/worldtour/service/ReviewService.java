package com.onix.worldtour.service;

import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.util.ValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ReviewService {

    private static final String API_ENDPOINT = "https://www.googleapis.com/youtube/v3/videos";
    private static final String API_KEY = "AIzaSyBT9Od9wP69wIFmk3k74bY4T6I8TvjYSDU";

    private final RestTemplate restTemplate;

    public ReviewService() {
        this.restTemplate = new RestTemplate();
    }

    public Object getVideoData(String url) {
        log.info("ReviewService::getVideoData execution started");
        Object data = null;
        try {
            log.debug("ReviewService::getVideoData url {}", url);
            String videoId = extractVideoId(url);
            if (videoId == null) {
                return null;
            }

            String apiUrl = buildApiUrl(videoId);
            data = restTemplate.getForObject(apiUrl, Object.class);
            log.debug("ReviewService::getVideoData received response {}", ValueMapper.jsonAsString(data));
        } catch (Exception e) {
            log.error("ReviewService::getVideoData error {}", e.getMessage());
            throw exception(EntityType.REVIEW, ExceptionType.ENTITY_EXCEPTION, url);
        }

        log.info("ReviewService::getVideoData execution completed");
        return data;
    }

    private String buildApiUrl(String videoId) {
        String apiUrl = String.format("%s?part=statistics,snippet&id=%s&key=%s", API_ENDPOINT, videoId, API_KEY);
        return apiUrl;
    }

    private String extractVideoId(String youtubeLink) {
        // Logic to extract the video ID from the provided YouTube link
        // You can use regular expressions or string manipulation to extract the video ID
        // Example implementation:
        if (youtubeLink.contains("?v=")) {
            int startIndex = youtubeLink.indexOf("?v=") + 3;
            int endIndex = youtubeLink.indexOf("&", startIndex);
            if (endIndex == -1) {
                endIndex = youtubeLink.length();
            }
            return youtubeLink.substring(startIndex, endIndex);
        } else if (youtubeLink.contains("youtu.be/")) {
            int startIndex = youtubeLink.indexOf("youtu.be/") + 9;
            int endIndex = youtubeLink.indexOf("?", startIndex);
            if (endIndex == -1) {
                endIndex = youtubeLink.length();
            }
            return youtubeLink.substring(startIndex, endIndex);
        } else {
            return null;
        }
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}
