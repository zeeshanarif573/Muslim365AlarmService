package com.example.muhammadzeeshan.muslim360withservice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmSchedular;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File AzaanTimingFile = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/AdhanTimings.json");
        File ManualCorrectionFile = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/ManualCorrection.json");
        File DtsFile = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/DTS.json");
        File NotiTypeFile = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/NotiType.json");

        File FajarAzan = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/fajar_alarm.mp3");
        File DhuhrAzan = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/dhuhr_azan.mp3");
        File AsrAzan = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/beep.mp3");
        File MaghribAzan = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/engagetune.mp3");
        File IshaAzan = new File(Environment.getExternalStorageDirectory() + "/Azaan Ringtones/notification.mp3");


        if (AzaanTimingFile.exists() && ManualCorrectionFile.exists() && DtsFile.exists() && NotiTypeFile.exists() && FajarAzan.exists() && DhuhrAzan.exists() && AsrAzan.exists() && MaghribAzan.exists() && IshaAzan.exists()) {

            Intent intent = new Intent(this, AlarmSchedular.class);

            intent.putExtra("adhan_timing_json", Environment.getExternalStorageDirectory() + "/Azaan Ringtones/AdhanTimings.json");
            intent.putExtra("mc_timing_json", Environment.getExternalStorageDirectory() + "/Azaan Ringtones/ManualCorrection.json");
            intent.putExtra("dts_json", Environment.getExternalStorageDirectory() + "/Azaan Ringtones/DTS.json");
            intent.putExtra("notitype_json", Environment.getExternalStorageDirectory() + "/Azaan Ringtones/NotiType.json");

            startService(intent);

        } else {
            Toast.makeText(this, "File not Exist..", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, AlarmSchedular.class));
    }
}