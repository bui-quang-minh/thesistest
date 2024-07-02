package vn.edu.fpt.thesis_test.actions;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;



public class ClickAction extends AccessibilityService {
    private static ClickAction instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static ClickAction getInstance() {
        return instance;
    }
    public void click(float x, float y) {
        Path clickPath = new Path();
        clickPath.moveTo(x, y);
        GestureDescription.StrokeDescription strokeDescription = new GestureDescription.StrokeDescription(clickPath, 0, 100);
        GestureDescription gestureDescription = new GestureDescription.Builder().addStroke(strokeDescription).build();
        dispatchGesture(gestureDescription, null, null);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}
