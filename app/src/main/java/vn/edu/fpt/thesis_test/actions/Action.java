package vn.edu.fpt.thesis_test.actions;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.Log;

public class Action {

    private final String TAG = Action.class.getSimpleName();
    public Path imageToCoordinate(String on){
        Path path = new Path();
        return path;
    }
    public void clickAction(String on, int duration, int tries, AccessibilityService accessibilityService){
        Path path = imageToCoordinate(on);
        path.moveTo(550, 1075);
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
    }

    public static boolean waitAction(int duration){

        return true;
    }
    public static void swipeAction(){

    }
}
