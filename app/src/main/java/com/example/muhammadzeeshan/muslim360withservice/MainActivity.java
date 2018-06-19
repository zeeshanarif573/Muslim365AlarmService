package com.example.muhammadzeeshan.muslim360withservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmSchedular;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    File AzaanTimingFile, NotiTypeFile, ManualCorrectionFile, DtsFile;
    GetContext getContext;
    Button btn, loadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn);
        loadBtn = findViewById(R.id.load);

        getContext = new GetContext(this);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AzaanTimingFile = new File("/storage/emulated/0/AdhanTime.json");
                ManualCorrectionFile = new File("/storage/emulated/0/ManualCorrection.json");
                DtsFile = new File("/storage/emulated/0/DTS.json");
                NotiTypeFile = new File("/storage/emulated/0/Notitype.json");

                getContext.cancelAlarm();

                if (AzaanTimingFile.exists() && ManualCorrectionFile.exists() && DtsFile.exists() && NotiTypeFile.exists()) {

                    Intent intent = new Intent(MainActivity.this, AlarmSchedular.class);
                    intent.putExtra("adhan_timing_json", "/storage/emulated/0/AdhanTime.json");
                    intent.putExtra("mc_timing_json", "/storage/emulated/0/ManualCorrection.json");
                    intent.putExtra("dts_json", "/storage/emulated/0/DTS.json");
                    intent.putExtra("notitype_json", "/storage/emulated/0/Notitype.json");

                    startService(intent);

                } else {
                    Toast.makeText(MainActivity.this, "File not Exist..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.e("file Read1",loadManualCorrectionJSON());
                    Log.e("file Read2",loadJSONFromAzanTimings());
                    Log.e("file Read3",loadNotiTypeJSON());
                    Log.e("file Read4",loadDTSJSON());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Load JSON's...............................................
    private String loadJSONFromAzanTimings() throws IOException {
        String json = null;
        File file = new File(String.valueOf(AzaanTimingFile));
        FileInputStream is = new FileInputStream(file);
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            json = null;
        } finally {
            is.close();
            return json;
        }
    }

    private String loadManualCorrectionJSON() throws IOException {
        String json = null;
        File file = new File(String.valueOf(ManualCorrectionFile));
        FileInputStream is = new FileInputStream(file);
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            json = null;
        } finally {
            is.close();
            return json;
        }
    }

    private String loadDTSJSON() throws IOException {
        String json = null;
        File file = new File(String.valueOf(DtsFile));
        FileInputStream is = new FileInputStream(file);
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            json = null;
        } finally {
            is.close();
            return json;
        }
    }

    private String loadNotiTypeJSON() throws IOException {
        String json = null;
        File file = new File(String.valueOf(NotiTypeFile));
        FileInputStream is = new FileInputStream(file);
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            json = null;
        } finally {
            is.close();
            return json;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, AlarmSchedular.class));
    }

}