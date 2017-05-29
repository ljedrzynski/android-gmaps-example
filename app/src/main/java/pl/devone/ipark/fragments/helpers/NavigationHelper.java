package pl.devone.ipark.fragments.helpers;

import android.graphics.Color;
import android.location.Location;

import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.Constants;
import com.mapbox.services.android.navigation.v5.MapboxNavigation;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;

import java.util.ArrayList;
import java.util.List;

import pl.devone.ipark.fragments.MapBoxFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by ljedrzynski on 26.05.2017.
 */

public class NavigationHelper {

    public static Polyline drawRoute(MapboxMap mapboxMap, Polyline routeLine, DirectionsRoute route) {
        List<Position> positions = LineString.fromPolyline(route.getGeometry(), Constants.PRECISION_6).getCoordinates();
        List<LatLng> latLngs = new ArrayList<>();

        for (Position position : positions) {
            latLngs.add(new LatLng(position.getLatitude(), position.getLongitude()));
        }

        if (routeLine != null) {
            mapboxMap.removePolyline(routeLine);
        }

        return mapboxMap.addPolyline(new PolylineOptions()
                .addAll(latLngs)
                .color(Color.parseColor("#56b881"))
                .width(5f));
    }

    public static void calculateRoute(MapboxMap mapboxMap, MapboxNavigation navigation,
                                      Position destination, final MapBoxFragment.NavigationActionCallback callback) {
        Location userLocation = mapboxMap.getMyLocation();
        if (userLocation == null) {
            Timber.d("calculateRoute: User location is null, therefore, origin can't be set.");
            return;
        }
        Position origin = LocationHelper.locationToPosition(userLocation);

        navigation.getRoute(origin, destination, new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                DirectionsRoute route = response.body().getRoutes().get(0);
                callback.onRouteReady(route);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("onFailure: navigation.getRoute()", throwable);
            }
        });
    }
}
