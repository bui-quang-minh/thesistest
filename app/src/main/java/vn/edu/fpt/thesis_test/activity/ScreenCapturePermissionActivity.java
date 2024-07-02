package vn.edu.fpt.thesis_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

public class ScreenCapturePermissionActivity extends Activity {

    private static final int REQUEST_CODE = 100;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(screenCaptureIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("resultCode", resultCode);
            resultIntent.putExtra("data", data);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
