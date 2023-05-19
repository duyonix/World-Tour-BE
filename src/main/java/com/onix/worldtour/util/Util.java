package com.onix.worldtour.util;

import com.onix.worldtour.model.Coordinate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class Util {
    public static double getDistance(Coordinate x, Coordinate y) {
        return Math.sqrt(Math.pow(x.getLattitude() - y.getLattitude(), 2) + Math.pow(x.getLongitude() - y.getLongitude(), 2));
    }

    public static LocalDateTime convertTimestampToDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp * 1000), TimeZone.getDefault().toZoneId());
    }
}
