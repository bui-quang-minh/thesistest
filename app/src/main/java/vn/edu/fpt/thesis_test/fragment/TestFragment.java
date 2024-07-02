package vn.edu.fpt.thesis_test.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import vn.edu.fpt.thesis_test.R;
import vn.edu.fpt.thesis_test.services.OverlayServices;

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private Button initiateOverlayButton;
    private Context context;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    private void onViewStart() {
        initiateOverlayButton = getActivity().findViewById(R.id.initiateOverlayButton);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewStart();
        eventHandling();
    }

    private void eventHandling() {
        initiateOverlayButton.setOnClickListener(this::initiateOverlay);
    }

    private void initiateOverlay(View view) {
        context.startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));
        Log.e(TAG, "initiateOverlay: " + "Overlay initiated" + " " + context.toString());
    }
}