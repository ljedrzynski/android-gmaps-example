package pl.devone.ipark.fragments.helpers;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.models.Position;

/**
 * Created by ljedrzynski on 26.05.2017.
 */

public class LocationHelper {

    public static Position positionFromLatLng(LatLng latLng) {
        return Position.fromCoordinates(latLng.getLongitude(), latLng.getLatitude(), latLng.getAltitude());
    }

    public static Location locationFromLatLng(LatLng latLng) {
        Location location = new Location("");
        location.setLongitude(latLng.getLongitude());
        location.setLatitude(latLng.getLatitude());
        location.setAltitude(latLng.getAltitude());
        return location;
    }

    public static Position positionFromLocation(Location location) {
        return Position.fromCoordinates(location.getLongitude(), location.getLatitude(), location.getAltitude());
    }

    public static boolean isLocationInRadius(LatLng source, LatLng destination, double radius) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(destination.getLatitude() - source.getLatitude());
        double dLng = Math.toRadians(destination.getLongitude() - source.getLongitude());
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(source.getLatitude())) * Math.cos(Math.toRadians(destination.getLatitude()));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (earthRadius * c) <= radius;
    }
}
