package pl.devone.ipark.services.activity;


import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivityRecognitionProvider extends IntentService {

    private final IBinder mBinder = new ActivityRecognitionProvider.ActivityRecognitionBinder();
    private Set<ActivityRecognitionProvider.ActivityRecognitionListener> mActivityRecognitionListeners = new HashSet<>();

    public ActivityRecognitionProvider() {
        super("ActivityRecognitionProvider");
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    class ActivityRecognitionBinder extends Binder {
        ActivityRecognitionProvider getService() {
            return ActivityRecognitionProvider.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.d(this.getClass().getSimpleName() + " has started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void registerListener(ActivityRecognitionListener activityRecognitionListener) {
        if (!mActivityRecognitionListeners.contains(activityRecognitionListener)) {
            mActivityRecognitionListeners.add(activityRecognitionListener);
        }
    }

    public void removeListener(ActivityRecognitionListener activityRecognitionListener) {
        mActivityRecognitionListeners.remove(activityRecognitionListener);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            for (ActivityRecognitionListener activityRecognitionListener : mActivityRecognitionListeners) {
                activityRecognitionListener.onActivityDetected(result.getMostProbableActivity());
            }
        }
    }

    interface ActivityRecognitionListener {

        void onActivityDetected(DetectedActivity activity);
    }
}
