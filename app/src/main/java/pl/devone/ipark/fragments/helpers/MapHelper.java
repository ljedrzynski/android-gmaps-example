package pl.devone.ipark.fragments.helpers;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ljedrzynski on 26.05.2017.
 */

public class MapHelper {

    public static void moveCamera(MapboxMap map, LatLng latLng, double zoom, double bearing, double tilt, int duration) {
        map.easeCamera(CameraUpdateFactory.newCameraPosition(
                (new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(zoom)
                        .bearing(bearing)
                        .tilt(tilt)
                        .build())), duration);
    }

    public static void moveCameraToBounds(MapboxMap mapboxMap, int padding, int duration, List<LatLng> positions) {
        moveCameraToBounds(mapboxMap, padding, duration, new HashSet<>(positions));
    }

    public static void moveCameraToBounds(MapboxMap mapboxMap, int padding, int duration, Set<LatLng> positions) {
        if (positions == null) {
            throw new RuntimeException("Positions cannot be null! Fatal error!");
        }
        if (mapboxMap == null) {
            throw new RuntimeException("MapBoxMap cannot be null. Fatal error!");
        }
        if (positions.size() == 1) {
            MapHelper.moveCamera(mapboxMap, positions.iterator().next(), 16, mapboxMap.getCameraPosition().bearing, 45, 1000);
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng position : positions) {
            builder.include(position);
        }

        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding), duration);
    }

    public static void moveCameraToBounds(MapboxMap mapboxMap, int padding, int duration, LatLng... positions) {
        moveCameraToBounds(mapboxMap, padding, duration, new HashSet<>(Arrays.asList(positions)));
    }
}
