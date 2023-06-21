package com.onix.worldtour.util;

import com.onix.worldtour.model.Coordinate;
import com.onix.worldtour.model.PanoramaType;

public class Util {
    private static final double EARTH_RADIUS = 6371.0; // Earth's mean radius in kilometers

    // https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
    public static double getDistance(Coordinate x, Coordinate y) {
        double lat1 = Math.toRadians(x.getLattitude());
        double lon1 = Math.toRadians(x.getLongitude());
        double lat2 = Math.toRadians(y.getLattitude());
        double lon2 = Math.toRadians(y.getLongitude());

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double distance = EARTH_RADIUS * c;

        return distance;
    }

    public static double calculateLatitudeDelta(double distance) {
        return Math.toDegrees(distance / EARTH_RADIUS);
    }

    public static double calculateLongitudeDelta(double distance, double latitude) {
        return Math.toDegrees(distance / (EARTH_RADIUS * Math.cos(Math.toRadians(latitude))));
    }

    public static PanoramaType getPanoramaType(String panorama) {
        if (panorama == null) {
            return null;
        }

        String lowercasePanorama = panorama.toLowerCase();
        if (isImage(lowercasePanorama)) {
            return PanoramaType.IMAGE;
        } else if (isVideo(lowercasePanorama)) {
            return PanoramaType.VIDEO;
        } else {
            return null;
        }
    }

    private static boolean isImage(String panorama) {
        return panorama.endsWith(".jpg") || panorama.endsWith(".jpeg") || panorama.endsWith(".png") || panorama.endsWith(".gif");
    }

    private static boolean isVideo(String panorama) {
        return panorama.endsWith(".mp4") || panorama.endsWith(".avi") || panorama.endsWith(".mov") || panorama.endsWith(".wmv");
    }
}
