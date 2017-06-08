package pl.devone.ipark.services.activity;


import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivityRecognitionProvider extends IntentService {

    private ActivityRecognitionHandler mActivityRecognitionHandler;

    public ActivityRecognitionProvider() {
        super("ActivityRecognitionProvider");
    }

    public void onCreate() {
        mActivityRecognitionHandler = new ActivityRecognitionHandler();
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(ActivityRecognitionHandler.ACTIVITY_RECOGNITION_MSG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mActivityRecognitionHandler, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleActivityResult(result.getProbableActivities());
        }
    }

    private void handleActivityResult(List<DetectedActivity> detectedActivities) {
        for (DetectedActivity activity : detectedActivities) {
            sendBroadcast(new Intent()
                    .setAction(ActivityRecognitionHandler.ACTIVITY_RECOGNITION_MSG)
                    .addCategory(Intent.CATEGORY_DEFAULT)
                    .putExtra(ActivityRecognitionHandler.ACTIVITY_DETECTED, activity.getType())
                    .putExtra(ActivityRecognitionHandler.ACTIVITY_CONFIDENCE, activity.getConfidence()));
        }
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(mActivityRecognitionHandler);
        super.onDestroy();
    }

}
