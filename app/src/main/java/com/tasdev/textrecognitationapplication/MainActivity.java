package com.tasdev.textrecognitationapplication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    //var init

    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;
    final int Id = 1001;
    private WebView webView;
    private RelativeLayout relativeLayout;

    private ArrayList<String> arrayList;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        surfaceView = findViewById(R.id.surface_view);
        textView = findViewById(R.id.textview);
        webView = findViewById(R.id.webview);
        arrayList = new ArrayList<String>();
        relativeLayout = findViewById(R.id.view);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://v-tube.xyz/watch/glinta_1nOQKZMLrGoDPYY.html");

        if (!textRecognizer.isOperational()) {

            Log.d("tag", "Dependency not working");
        } else {


            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1200, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
        }

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
               if(checkLocationPermission()) {
                   try {
                       cameraSource.start(surfaceView.getHolder());
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                cameraSource.stop();

            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> iteam = detections.getDetectedItems();

                if (iteam.size() != 0) {

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < iteam.size(); i++) {

                                TextBlock item = iteam.get(i);
                                Log.d("item", item.getValue());
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                                String data = item.getValue();
                                arrayList.add(item.getValue());

                            }


                            String value = stringBuilder.toString();
                            textView.setText(stringBuilder.toString());

                            if (value.startsWith("Techno")) {

                                Log.d("test", stringBuilder.toString());
                                relativeLayout.setVisibility(View.GONE);
                                webView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }
 @Override
    public void onBackPressed() {
        webView.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);

    }



    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please Share/on Your Camera")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_LOCATION);

                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }

            return false;
        } else {

            return true;

        }
    }
}
