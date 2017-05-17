package pl.devone.ipark.authentication.callback;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public interface AuthTaskCallback {


    void onSuccess();

    void onFailure();

    void onError(String error);
}
