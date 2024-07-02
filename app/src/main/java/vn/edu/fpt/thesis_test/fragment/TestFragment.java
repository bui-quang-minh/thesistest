package vn.edu.fpt.thesis_test.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import vn.edu.fpt.thesis_test.R;
import vn.edu.fpt.thesis_test.activity.ScreenCapturePermissionActivity;
import vn.edu.fpt.thesis_test.services.AutomaticService;
import vn.edu.fpt.thesis_test.services.ScreenCaptureService;

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private Button initiateOverlayButton;
    private Context context;
    private com.google.android.material.textfield.TextInputEditText inputEditText;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        inputEditText = view.findViewById(R.id.inputEditText);
        // Inflate the layout for this fragment
        return view;
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
        Intent intent = new Intent(context, ScreenCapturePermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("json", inputEditText.getText()) ;
        startActivity(intent);
    }
}