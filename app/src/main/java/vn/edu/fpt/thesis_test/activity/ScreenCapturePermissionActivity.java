package vn.edu.fpt.thesis_test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import vn.edu.fpt.thesis_test.services.ScreenCaptureService;

public class ScreenCapturePermissionActivity extends Activity {

    private static final int REQUEST_CODE = 100;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startProjection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = vn.edu.fpt.thesis_test.services.ScreenCaptureService.getStartIntent(this, resultCode, data);
                startService(intent);
                finish();
            }

        }
    }
    private void startProjection() {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        startService(vn.edu.fpt.thesis_test.services.ScreenCaptureService.getStopIntent(this));
    }
}
