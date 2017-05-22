package pl.devone.ipark.services.parkingspace;

import android.content.Context;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import pl.devone.ipark.R;
import pl.devone.ipark.activities.helpers.ActivityHelper;
import pl.devone.ipark.models.ParkingSpace;
import pl.devone.ipark.services.callback.AsyncTaskCallback;
import pl.devone.ipark.services.http.RestClient;

/**
 * Created by ljedrzynski on 22.05.2017.
 */

public class ParkingSpaceManager {

    private static Gson gson = new Gson();

    public static void createParkingSpace(final Context context, final ParkingSpace parkingSpace, final AsyncTaskCallback callback) {
        try {
            parkingSpace.setReporterId(ActivityHelper.getUser(context).getId());
            RestClient.post(context, context.getString(R.string.parking_spaces_url), gson.toJson(parkingSpace), new JsonHttpResponseHandler() {
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
            ActivityHelper.reportError(context, R.string.error_reported_info, exc);
        }
    }

    public static void updateParkingSpace(final Context context, final ParkingSpace parkingSpace, final AsyncTaskCallback callback) {
        try {
            RestClient.put(context, context.getString(R.string.parking_spaces_url), gson.toJson(parkingSpace), new JsonHttpResponseHandler() {
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
            ActivityHelper.reportError(context, R.string.error_reported_info, exc);
        }
    }
}
