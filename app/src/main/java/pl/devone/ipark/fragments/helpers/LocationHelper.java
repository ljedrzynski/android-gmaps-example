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
}
