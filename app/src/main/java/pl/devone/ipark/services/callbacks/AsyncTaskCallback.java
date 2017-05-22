package pl.devone.ipark.services.callbacks;

/**
 * Created by ljedrzynski on 22.05.2017.
 */

public interface AsyncTaskCallback {
    void onSuccess();

    void onFailure();

    void onError(String error);
}
