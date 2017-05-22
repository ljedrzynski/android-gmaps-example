package pl.devone.ipark.services.http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pl.devone.ipark.services.http.helpers.HttpHelper;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public class RestClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        client.get(context, HttpHelper.getApiAbsoluteUrl(context, url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    public static void post(Context context, String url, String entity, ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        client.post(context, HttpHelper.getApiAbsoluteUrl(context, url), new StringEntity(entity), "application/json", responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(url, params, responseHandler);
    }

    public static void put(Context context, String url, String entity, ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        client.put(context, HttpHelper.getApiAbsoluteUrl(context, url), new StringEntity(entity), "application/json", responseHandler);
    }

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(url, params, responseHandler);
    }

    public static void setAuthorizationHeader(String token) {
        client.addHeader("Authorization", token);
    }
}
