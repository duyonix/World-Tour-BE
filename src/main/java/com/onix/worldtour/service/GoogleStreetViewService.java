package com.onix.worldtour.service;

import com.onix.worldtour.controller.data.StreetViewMetadata;
import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.model.Coordinate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GoogleStreetViewService {
    private static final String API_ENDPOINT = "https://maps.googleapis.com/maps/api/streetview/metadata";
    private static final String API_KEY = "AIzaSyB0pAAfd-SgsJm0w0hvzZfg90qfXoPN9bw";

    private final RestTemplate restTemplate;

    public GoogleStreetViewService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean hasStreetView(Coordinate coordinate) {
        log.info("GoogleStreetViewService::checkHasStreetView execution started");
        boolean hasStreetView = false;

        try {
            log.debug("GoogleStreetViewService::checkHasStreetView coordinate {}", coordinate);
            String apiUrl = buildApiUrl(coordinate);

            StreetViewMetadata streetViewMetadata = restTemplate.getForObject(apiUrl, StreetViewMetadata.class);
            if (streetViewMetadata != null) {
                hasStreetView = streetViewMetadata.getStatus().equals("OK");
            }
            log.debug("GoogleStreetViewService::checkHasStreetView received response {}", hasStreetView);
        } catch (Exception e) {
            log.error("GoogleStreetViewService::checkHasStreetView error {}", e.getMessage());
            throw exception(EntityType.STREET_VIEW, ExceptionType.ENTITY_EXCEPTION, coordinate.toString());
        }

        log.info("GoogleStreetViewService::checkHasStreetView execution completed");
        return hasStreetView;
    }

    private String buildApiUrl(Coordinate coordinate) {
        return String.format("%s?location=%s,%s&key=%s", API_ENDPOINT, coordinate.getLattitude(), coordinate.getLongitude(), API_KEY);
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}
