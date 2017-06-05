package pl.devone.ipark.services.parkingspace;

import android.content.Context;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import pl.devone.ipark.R;
import pl.devone.ipark.activities.helpers.CommonHelper;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.callbacks.AsyncTaskCallback;
import pl.devone.ipark.services.http.RestClient;
import pl.devone.ipark.services.parkingspace.callbacks.ParkingSpaceFetchCallback;

/**
 * Created by ljedrzynski on 22.05.2017.
 */

public class ParkingSpaceManager {

    private static Gson gson = new Gson();

    public static void createParkingSpace(final Context context, final ParkingSpace parkingSpace, final AsyncTaskCallback callback) {
        String jsonString = new Gson().toJson(parkingSpace);
        byte[] utf8JsonString = new byte[0];
        try {
            utf8JsonString = jsonString.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            RestClient.post(context, context.getString(R.string.parking_spaces_url), new String(utf8JsonString), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onSuccess();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    callback.onFailure();
                }
            });
        } catch (Exception exc) {
            CommonHelper.reportError(context, R.string.error_reported_info, exc);
        }
    }

    public static void updateParkingSpace(final Context context, final ParkingSpace parkingSpace, final AsyncTaskCallback callback) {
        try {
            RestClient.put(context, context.getString(R.string.parking_spaces_url) + "/" + parkingSpace.getId(), gson.toJson(parkingSpace), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onSuccess();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    callback.onFailure();
                }
            });
        } catch (Exception exc) {
            CommonHelper.reportError(context, R.string.error_reported_info, exc);
        }
    }

    public static void getParkingSpaces(final Context context, final Location location, final double radius, final ParkingSpaceFetchCallback callback) {
        try {
            RestClient.get(context, context.getString(R.string.parking_spaces_url), new RequestParams(new HashMap<String, String>() {{
                put("lng", String.valueOf(location.getLongitude()));
                put("lat", String.valueOf(location.getLatitude()));
                put("rad", String.valueOf(radius));
            }}), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Type collectionType = new TypeToken<List<ParkingSpace>>() {
                    }.getType();
                    List<ParkingSpace> parkingSpaces = gson.fromJson(response.toString(), collectionType);

                    if (parkingSpaces == null || parkingSpaces.size() == 0) {
                        callback.onFailure();
                    }

                    callback.onSuccess(parkingSpaces);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    callback.onFailure();
                }

            });
        } catch (Exception exc) {
            CommonHelper.reportError(context, R.string.error_reported_info, exc);
        }
    }

    public static void getUserParkingSpaces(final Context context, final ParkingSpaceFetchCallback callback) {
        try {
            RestClient.get(context, context.getString(R.string.parking_spaces_url), new RequestParams(new HashMap<String, String>() {{
                put("usr", String.valueOf(CommonHelper.getUser(context).getId()));
            }}), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Type collectionType = new TypeToken<List<ParkingSpace>>() {
                    }.getType();
                    List<ParkingSpace> parkingSpaces = gson.fromJson(response.toString(), collectionType);

                    if (parkingSpaces == null || parkingSpaces.size() == 0) {
                        callback.onFailure();
                    }

                    callback.onSuccess(parkingSpaces);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    callback.onFailure();
                }

            });
        } catch (Exception exc) {
            CommonHelper.reportError(context, R.string.error_reported_info, exc);
        }
    }
}
