package com.example.dell.timelapsearpit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "CameraActivity";
    private MediaRecorderWrapper mediaRecorderWrapper;
    Button captureButton;
    final public static double DEFAULT_FRAME_RATE = 1;
    private double frameRate;
    File outputMediaFile;
int a=30;
    private Spinner spinner1;

    EditText actualFrameSecEditText;
    PermissionHandler permissionHandler;
    String permissions[] = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.newdesign);
        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

         String s=parent.getItemAtPosition(position).toString();
                if (s.equals("Fast"))
                {

                    a=6;
                }
                else if(s.equals("Normal"))
                {
                    a=30;
                }
                else {
                    a=90;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        permissionHandler = new PermissionHandler(this);
        permissionHandler.requestPermission(permissions);

        // Add a listener to the Capture button
        captureButton = (Button) findViewById(R.id.button);

        //maxFrameSecTextView.setText(Integer.toString(getMaxCaptureRate(mCamera)));

    }

    @Override
    public void onResume() {
        super.onResume();

        if( permissionHandler.hasRights() ) {
            initMediaRecorderWrapperIfNull();
            mediaRecorderWrapper.startPreview();
        }
        outputMediaFile = getOutputMediaFile();

        captureButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if( permissionHandler.hasRights() ) {

                            if (mediaRecorderWrapper.isRecording()) {
                                mediaRecorderWrapper.stopRecording();  // stop the recording
                                captureButton.setBackground(getResources().getDrawable(R.drawable.play));
                                // inform the user that recording has stopped

                                outputMediaFile = getOutputMediaFile();
                                Toast.makeText(MainActivity.this, "Saved in "+outputMediaFile.toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                try {



                                    frameRate = Double.valueOf(a);
                                    mediaRecorderWrapper.setFrameRateIfPossible(frameRate);
                                    mediaRecorderWrapper.startRecording(outputMediaFile.toString());
                                    if (mediaRecorderWrapper.isRecording()) {
                                        captureButton.setBackground(getResources().getDrawable(R.drawable.stop));
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "Invalid frame/sec.");
                                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid frame/sec.", Toast.LENGTH_SHORT);
                                    toast.show();
                                    return;
                                }
                            }
                        }
                    }
                });

    }

    private File getOutputMediaFile() {


        File mediaStorageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID_" + timeStamp + ".mp4");

        return mediaFile;
    }

    private int getMaxCaptureRate(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        int fpsRange[] = new int[2];

        parameters.getPreviewFpsRange(fpsRange);
        return fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]/1000;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if( permissionHandler.hasRights() && mediaRecorderWrapper != null ) {
            mediaRecorderWrapper.stopPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHandler.onRequestPermissionsResult( requestCode, permissions, grantResults);
        Log.i(TAG,"permissionHandler.hasRights():" + permissionHandler.hasRights());

        if( permissionHandler.hasRights() ) {
            initMediaRecorderWrapperIfNull();
        }
    }
    private void initMediaRecorderWrapperIfNull() {
        if( mediaRecorderWrapper == null ) {
            mediaRecorderWrapper = new MediaRecorderWrapper(this, R.id.camera_preview);
            mediaRecorderWrapper.setTimelapse(true);
        }
    }

}
