package vn.edu.fpt.thesis_test.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;


import com.google.gson.Gson;

import vn.edu.fpt.thesis_test.R;
import vn.edu.fpt.thesis_test.actions.Action;
import vn.edu.fpt.thesis_test.activity.ScreenCapturePermissionActivity;
import vn.edu.fpt.thesis_test.model.Step;

public class AutomaticService extends AccessibilityService {
    private static final int REQUEST_CODE = 100;
    private static final String TAG = "OverlayServices";
    private WindowManager windowManager;
    private View overlayView;
    private Step[] steps;
    private MediaProjectionManager mProjectionManager;
    private BroadcastReceiver resultReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        System.loadLibrary("opencv_java4");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        info.notificationTimeout = 100;
        this.setServiceInfo(info);
        Log.e(TAG, "jhgfUKGKH<>LKH>KH<>J<HK:");
        String json = intent.getExtras().get("Json").toString();
        steps = bindStep(json);
        Log.e(TAG, "Steps: " + steps[0].getActionType());
        showOverlay();
        super.onCreate();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    private void showOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        overlayView = inflater.inflate(R.layout.overlay_layout, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        windowManager.addView(overlayView, params);

        ImageButton closeButton = overlayView.findViewById(R.id.stop_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOverlay();
            }
        });
        ImageButton playButton = overlayView.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ImageButton cameraButton = overlayView.findViewById(R.id.camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProjection();
            }
        });
    }

    private void run() throws InterruptedException {
        Action action = new Action();
        for (int i = 0; i < steps.length; i++) {
            int finalI = i;
            if (steps[finalI].getActionType().equals("click")) {
                try {
                    synchronized (steps[i]) {
                        action.clickAction(steps[finalI].getOn(), steps[finalI].getDuration(), steps[finalI].getTries(), AutomaticService.this);
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        }
    }

    private void removeOverlay() {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }

    private Step[] bindStep(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Step[].class);

    }
    private void startProjection() {
        Intent intent = new Intent(this, ScreenCapturePermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void stopProjection() {
        startService(vn.edu.fpt.thesis_test.services.ScreenCaptureService.getStopIntent(this));
    }
}
