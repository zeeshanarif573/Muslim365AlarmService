package com.example.muhammadzeeshan.muslim360withservice.JsonHandler;

import android.content.Context;

import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Muhammad Zeeshan on 3/21/2018.
 */

public class BackgroundList {

    Context context;
    ArrayList<Timings> azaanTimingsList = new ArrayList();
    String json;
    DatabaseHelper databaseHelper;

    public BackgroundList(Context context) {
        this.context = context;
    }

    public ArrayList<Timings> getJson() {

        databaseHelper = new DatabaseHelper(context);
        json = loadJSONFromAsset();

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject getData = jsonArray.getJSONObject(i);
                JSONObject getTimings = getData.getJSONObject("timings");

                Timings azanTimings = new Timings(i + 1, getTimings.getString("Fajr"), getTimings.getString("Sunrise"),
                        getTimings.getString("Dhuhr"), getTimings.getString("Asr"), getTimings.getString("Sunset"),
                        getTimings.getString("Maghrib"), getTimings.getString("Isha"), getTimings.getString("Imsak"),
                        getTimings.getString("Midnight"));

                azaanTimingsList.add(azanTimings);
            }

            databaseHelper.insertIntoAzanTable(azaanTimingsList);
            DatabaseUtils.peekAllDataFromTimimgs(context);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return azaanTimingsList;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("AdhanTimings.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
