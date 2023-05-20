package com.onix.worldtour.util;

import com.onix.worldtour.model.Coordinate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class Util {
    private static final double EARTH_RADIUS = 6371.0; // Earth's mean radius in kilometers
    public static double getDistance(Coordinate x, Coordinate y) {
        double lat1 = Math.toRadians(x.getLattitude());
        double lon1 = Math.toRadians(x.getLongitude());
        double lat2 = Math.toRadians(y.getLattitude());
        double lon2 = Math.toRadians(y.getLongitude());

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;

        return distance;
    }

    public static LocalDateTime convertTimestampToDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp * 1000), TimeZone.getDefault().toZoneId());
    }
}
