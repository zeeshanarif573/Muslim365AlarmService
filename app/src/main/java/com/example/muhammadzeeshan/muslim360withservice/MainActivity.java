package com.example.muhammadzeeshan.muslim360withservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmSchedular;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, AlarmSchedular.class);
        intent.putExtra("DataFilePath", "abc.json");
        intent.putExtra("DTSValue", "0");
        intent.putExtra("MCJson", "{\"Fajr\": \"05:53 (PKT)\",\"Dhuhr\": \"12:45 (PKT)\",\"Asr\": \"16:42 (PKT)\",\"Maghrib\": \"18:19 (PKT)\",\"Isha\": \"19:32 (PKT)\"}");
        intent.putExtra("NotitypeJson", "{\"Fajr\": \"1\",\"Dhuhr\": \"1\",\"Asr\": \"1\",\"Maghrib\": \"1\",\"Isha\": \"1\"}");
        intent.putExtra("TunePath", "xyz.json");
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, AlarmSchedular.class));
    }
}
