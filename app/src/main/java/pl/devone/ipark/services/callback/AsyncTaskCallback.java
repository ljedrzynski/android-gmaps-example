package pl.devone.ipark.services.callback;

/**
 * Created by ljedrzynski on 17.05.2017.
 */

public interface AsyncTaskCallback {

    void onSuccess();

    void onFailure();

    void onError(String error);
}
