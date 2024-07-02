package vn.edu.fpt.thesis_test.receiver;

import android.content.Context;
import android.content.Intent;

import vn.edu.fpt.thesis_test.MainActivity;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent restartIntent = new Intent(context, MainActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(restartIntent);
    }
}
