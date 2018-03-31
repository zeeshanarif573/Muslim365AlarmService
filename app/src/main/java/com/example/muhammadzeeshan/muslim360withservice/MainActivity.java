package com.example.muhammadzeeshan.muslim360withservice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmSchedular;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String folder = Environment.getExternalStorageDirectory() + "/Azaan Ringtones/beep.mp3";
        File targetRingtone = new File(folder);
        Log.e("tone", targetRingtone.getAbsolutePath());

        Intent intent = new Intent(this, AlarmSchedular.class);
        intent.putExtra("adhan_timing_json", Environment.getExternalStorageDirectory() + "/Azaan Ringtones/AdhanTimings.json");
        intent.putExtra("mc_timing_json", Environment.getExternalStorageDirectory() + "/Azaan Ringtones/ManualCorrection.json");
        intent.putExtra("dts_json", Environment.getExternalStorageDirectory() + "/Azaan Ringtones/DTS.json");
        intent.putExtra("notitype_json", Environment.getExternalStorageDirectory() + "/Azaan Ringtones/NotiType.json");
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, AlarmSchedular.class));
    }
}
