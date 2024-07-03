package vn.edu.fpt.thesis_test.actions;

import static androidx.core.content.ContextCompat.getSystemService;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Path;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class Action {
    private static final String TAG = Action.class.getSimpleName();
    public static void clickAction(float x, float y, int duration, int tries, AccessibilityService accessibilityService){
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, duration));
        boolean result = accessibilityService.dispatchGesture(builder.build(), new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e(TAG, "Gesture completed");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG, "Gesture cancelled");
            }

        },null);
        Log.e(TAG, "Gesture Result: " + result);
    }
    public static void swipeAction(){

    }

}
