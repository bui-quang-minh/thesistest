package vn.edu.fpt.thesis_test.services;

import static android.app.PendingIntent.getActivity;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.activity.ComponentActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import vn.edu.fpt.thesis_test.MainActivity;
import vn.edu.fpt.thesis_test.R;
import vn.edu.fpt.thesis_test.actions.ClickAction;
import vn.edu.fpt.thesis_test.activity.ScreenCapturePermissionActivity;
import vn.edu.fpt.thesis_test.helper.FileUtil;
import vn.edu.fpt.thesis_test.helper.ScreenshotUtil;

public class OverlayServices extends AccessibilityService {

    private static final String TAG = "OverlayServices";
    private static final int REQUEST_CODE = 100;
    private Handler handler;
    private WindowManager windowManager;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private WindowManager.LayoutParams layoutParams;
    private ImageView screenshotImageView;
    private View overlayView;
    @Override
    protected void onServiceConnected() {

            System.loadLibrary("opencv_java4");

        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        info.notificationTimeout = 100;
        this.setServiceInfo(info);
        Log.e(TAG, "onServiceConnected:");
        showOverlay();
    }
    @Override
    protected boolean onGesture(int gestureId) {
        // Handle custom gestures if needed
        return super.onGesture(gestureId);
    }

    private void showOverlay() {
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
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
                screenshot(overlayView);
            }
        });

        screenshotImageView = overlayView.findViewById(R.id.screenshot_image_view);
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG, "onAccessibilityEvent: " + "event start");
        Log.e(TAG, "onAccessibilityEvent: Open App: " + event.getText());
        Log.e(TAG, "onAccessibilityEvent: Event Detail: " + event.toString() );
        String s = event.getText().toString().trim();
        String gmail = "[Gmail]";
       if (s.equals(gmail)){
           Log.e(TAG, "onAccessibilityEvent: Gmail reached" );


           new CountDownTimer(5000, 1000) {
               public void onFinish() {
                   Log.d(TAG, "CountDownTimer finished, dispatching gesture");
                           GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                           Path path = new Path();
                           path.moveTo(550, 1075); // Coordinates (930, 1460)
                           gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));

                           boolean result = dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                               @Override
                               public void onCompleted(GestureDescription gestureDescription) {
                                   super.onCompleted(gestureDescription);
                                   Log.d(TAG, "Gesture completed");
                               }

                               @Override
                               public void onCancelled(GestureDescription gestureDescription) {
                                   super.onCancelled(gestureDescription);
                                   Log.d(TAG, "Gesture cancelled");
                               }
                           }, null);

                           Log.d(TAG, "Java DispatchGesture: Result = " + result);
                       }

               public void onTick(long millisUntilFinished) {
                   Log.d(TAG, "Countdown: " + millisUntilFinished / 1000);
               }
           }.start();
       }
    }
    private void removeOverlay() {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: Started");
        showOverlay();
        if (intent != null) {
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            Intent data = intent.getParcelableExtra("data");
            if (resultCode == Activity.RESULT_OK && data != null) {
                setupMediaProjection(resultCode, data);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onInterrupt() {

    }

    // KHONG DUNG DUOC DAU HUHU
    protected void screenshot(View view) {
        Date date = new Date();

        // Here we are initialising the format of our image name
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
        try {
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC4);
            Utils.bitmapToMat(bitmap, mat);
            Log.e(TAG, "screenshot: Mat:"+mat );
            Bitmap templateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crop3);
            Mat templateMat = new Mat(templateBitmap.getHeight(), templateBitmap.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(templateBitmap, templateMat);
            Log.e(TAG, "screenshot: templateMat:"+templateMat );
            Mat result = new Mat();
            int resultCols = mat.cols();
            int resultRows = mat.rows();
            result.create(resultRows, resultCols, CvType.CV_32FC1);

            // Use the TM_CCORR_NORMED method for template matching
            Imgproc.matchTemplate(mat, templateMat, result, Imgproc.TM_CCOEFF_NORMED);

            // Find the location of the best match
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            Point matchLoc;
            if (Imgproc.TM_CCOEFF_NORMED == Imgproc.TM_SQDIFF || Imgproc.TM_CCOEFF_NORMED == Imgproc.TM_SQDIFF_NORMED) {
                matchLoc = mmr.minLoc;
            } else {
                matchLoc = mmr.maxLoc;
            }

            // Draw a rectangle around the detected region
            Imgproc.rectangle(mat, matchLoc, new Point(matchLoc.x + templateMat.cols(), matchLoc.y + templateMat.rows()), new Scalar(255, 0, 0), 2);

            // Convert Mat back to Bitmap
            Bitmap outputBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, outputBitmap);

            Log.e(TAG, "screenshot: " + matchLoc.x + " " + matchLoc.y + " " + templateMat.cols() + " " + templateMat.rows());
            // Display the processed image
            screenshotImageView.setImageBitmap(outputBitmap);
            screenshotImageView.setVisibility(View.VISIBLE);

        } catch (Exception io) {
            io.printStackTrace();
        }
    }

    private void startProjection() {
        Intent intent = new Intent(this, ScreenCapturePermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setupMediaProjection(int resultCode, Intent data) {
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        setupVirtualDisplay();
    }

    private void setupVirtualDisplay() {
        HandlerThread handlerThread = new HandlerThread("ScreenCapture");
        handlerThread.start();
        Log.e(TAG, "setupVirtualDisplay: "+ "Virtual Display Started"+handlerThread.getLooper());
        handler = new Handler(handlerThread.getLooper());

        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.RGB_565, 2);
        virtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture",
                1080, 1920, getResources().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, handler);

        imageReader.setOnImageAvailableListener(imageReader -> {
            Image image = null;
            try {
                image = imageReader.acquireLatestImage();
                if (image != null) {
                    processImage(image);
                }
            } finally {
                if (image != null) {
                    image.close();
                }
            }
        }, handler);
    }

    private void processImage(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * image.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.d("Screenshot", encodedImage);

        runOnUiThread(() -> {
            screenshotImageView.setImageBitmap(bitmap);
            screenshotImageView.setVisibility(View.VISIBLE);
        });
    }
    private void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

}