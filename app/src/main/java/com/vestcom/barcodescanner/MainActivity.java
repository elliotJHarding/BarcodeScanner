package com.vestcom.barcodescanner;

import android.media.AudioManager;
import android.nfc.Tag;
import android.os.Bundle;

import com.extbcr.scannersdk.BarcodeData;
import com.extbcr.scannersdk.BarcodeManager;
import com.extbcr.scannersdk.EventListener;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.util.Log;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ScannerSDK-MainActivity";

    // UI Elements
    private TextView scanResult;
    private TextView scanData;
    private TextView scanInstructions;
    private TextView scanStatus;
    private TextView scanTimeout;
    private ProgressBar scanProgress;
    private ImageView imageSuccess;
    private ImageView imageFailure;

    // Scanner Handlers
    private BarcodeManager barcodeManager;
    private EventListener eventListener;
    private boolean serverConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init UI Elements
        scanResult = (TextView) findViewById(R.id.scan_result_text);
        scanInstructions = (TextView) findViewById(R.id.scan_instructions);
        scanStatus = (TextView) findViewById(R.id.scan_status);
        scanTimeout = (TextView) findViewById(R.id.scan_timeout_text);
        scanData = (TextView) findViewById(R.id.scan_data_text);

        scanProgress = (ProgressBar) findViewById(R.id.progressBar);
        imageSuccess = (ImageView) findViewById(R.id.imageSuccess);
        imageFailure = (ImageView) findViewById(R.id.imageFail);

        // Init Scanner Handlers
        scan_setup();

        // Set action for FAB
        FloatingActionButton fab = findViewById(R.id.scan_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                if (serverConnect) {
                    barcodeManager.startDecode();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to ScannerServer
        barcodeManager.init();

        barcodeManager.stopDecode(); // prevents scan failing to fire without timeout

    }

    private void scan_setup() {
        barcodeManager = new BarcodeManager(this);

        eventListener = new EventListener() {
            @Override
            public void onReadData(BarcodeData barcodeData) {

                scanProgress.setVisibility(View.GONE);
                scanStatus.setVisibility(View.GONE);

                int id = barcodeData.getCodeID();
                String data = barcodeData.getText();

                scanData.setText("Type: " + id + "\nData: " + data);
                barcodeManager.stopDecode();

                scanResult.setVisibility(View.VISIBLE);
                scanData.setVisibility(View.VISIBLE);
                imageSuccess.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTimeout() {

                scanProgress.setVisibility(View.GONE);
                scanStatus.setVisibility(View.GONE);

                scanTimeout.setVisibility(View.VISIBLE);
                imageFailure.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStart() {

                scanTimeout.setVisibility(View.GONE);
                scanInstructions.setVisibility(View.GONE);
                scanResult.setVisibility(View.GONE);
                scanData.setVisibility(View.GONE);

                imageSuccess.setVisibility(View.GONE);
                imageFailure.setVisibility(View.GONE);

                scanStatus.setVisibility(View.VISIBLE);
                scanProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStop() {
                scanProgress.setVisibility(View.GONE);
                scanStatus.setVisibility(View.GONE);
            }

            @Override
            public void onConnect() {
                serverConnect = true;
                Log.i(TAG, "connect");
            }

            @Override
            public void onDisconnect() {
                serverConnect = false;
                Log.i(TAG, "disconnect");
            }
        };
        barcodeManager.addListener(eventListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
