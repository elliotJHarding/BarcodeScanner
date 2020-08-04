package com.vestcom.barcodescanner;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ScannerSDK-MainActivity";

    private TextView scanResult;
    private TextView scanData;
    private TextView scanInstructions;
    private TextView scanStatus;
    private TextView scanTimeout;
    private ProgressBar scanProgress;

    private BarcodeManager barcodeManager;
    private EventListener eventListener;
    private boolean serverConnnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(TAG, "oncreate Thread ID: " + Thread.currentThread().getId());

        scanResult = (TextView) findViewById(R.id.scan_result_text);
        scanInstructions = (TextView) findViewById(R.id.scan_instructions);
        scanStatus = (TextView) findViewById(R.id.scan_status);
        scanTimeout = (TextView) findViewById(R.id.scan_timeout_text);

        scanProgress = (ProgressBar) findViewById(R.id.progressBar);

        scan_setup();

        FloatingActionButton fab = findViewById(R.id.scan_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeManager.startDecode();
            }
        });

    }

    private void scan_setup() {
        barcodeManager = new BarcodeManager(this);
        barcodeManager.init();

        eventListener = new EventListener() {
            @Override
            public void onReadData(BarcodeData barcodeData) {

                scanProgress.setVisibility(View.GONE);
                scanStatus.setVisibility(View.GONE);

                scanResult.setVisibility(View.VISIBLE);
                scanData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTimeout() {

                scanProgress.setVisibility(View.GONE);
                scanStatus.setVisibility(View.GONE);

                scanTimeout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStart() {
                scanTimeout.setVisibility(View.GONE);
                scanInstructions.setVisibility(View.GONE);
                scanResult.setVisibility(View.GONE);

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
                serverConnnect = true;
            }

            @Override
            public void onDisconnect() {
                serverConnnect = false;
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
