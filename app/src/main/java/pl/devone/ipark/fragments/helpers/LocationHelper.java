package pl.devone.ipark.fragments.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.models.Position;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

    public static String simpleGeocodeReverse(Context context, Location location) {
        String result = "";
        List<Address> addresses = geocodeReverse(context, location);
        if (addresses.size() == 1) {
            result = getSimpleAddress(addresses.get(0));
        }
        return result;
    }

    public static List<Address> geocodeReverse(Context context, Location location) {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return addresses;
    }

    public static String getSimpleAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i);
            sb.append(':');
            String line = address.getAddressLine(i);
            if (line == null) {
                sb.append("null");
            } else {
                sb.append('\"');
                sb.append(line);
                sb.append('\"');
            }
        }
        return sb.toString();
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
