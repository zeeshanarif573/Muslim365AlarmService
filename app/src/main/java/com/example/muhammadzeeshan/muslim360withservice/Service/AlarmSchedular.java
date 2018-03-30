package com.example.muhammadzeeshan.muslim360withservice.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.Model.ManualCorrection;
import com.example.muhammadzeeshan.muslim360withservice.Model.NotiType;
import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodaysData;
import com.example.muhammadzeeshan.muslim360withservice.SharedPreferenceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/28/2018.
 */

public class AlarmSchedular extends Service {

    String DataFilePath, DtsValue, McJson, NotitypeJson, TunePath, DTSValue, TPath;
    DatabaseHelper databaseHelper;
    SharedPreferenceHandler sharedPreferenceHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("AlarmSchedular", "OnCreate");

        databaseHelper = new DatabaseHelper(this);
        sharedPreferenceHandler = new SharedPreferenceHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("AlarmSchedular", "onStartCommand");

        if (intent.getExtras() != null) {
            DataFilePath = intent.getStringExtra("DataFilePath");
            DtsValue = intent.getStringExtra("DTSValue");
            McJson = intent.getStringExtra("MCJson");
            NotitypeJson = intent.getStringExtra("NotitypeJson");
            TunePath = intent.getStringExtra("TunePath");
        }

        resetAllValuesAndData();

        sharedPreferenceHandler.setDtsValue(DtsValue);
        sharedPreferenceHandler.setTunePath(TunePath);

        Log.e("DTSValue", sharedPreferenceHandler.getDtsValue());
        Log.e("TunePath", sharedPreferenceHandler.getTunePath());

        loadAllTimingsJsonFileAndSaveIntoDatabase();
        //    getTodayDataFromMasterAndSaveInTodayTable();
        loadManualCorrectionJsonAndSaveIntoTable();
        loadDTSJsonAndSaveIntoTable();
        loadNotiTypeJsonAndSaveIntoTable();
        loadTunePathJsonAndSaveIntoTable();

        return super.onStartCommand(intent, flags, startId);
    }

    private void getTodayDataFromMasterAndSaveInTodayData() {

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);
        Log.e("Date..", strDate);

        //Get Only Current Date....................
        Calendar onlyDate = Calendar.getInstance();
        String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));

        List<TodaysData> getTodayDataList = databaseHelper.getTodaysData(date);

        if (getTodayDataList.size() > 0) {
            for (TodaysData todaysData : getTodayDataList) {

                String FajrTime = todaysData.getFajr();
                String DhuhrTime = todaysData.getDhuhr();
                String AsrTime = todaysData.getAsr();
                String MaghribTime = todaysData.getMaghrib();
                String IshaTime = todaysData.getIsha();

                Log.e("TodaysData", "Today's Date: " + todaysData.getDate());
                Log.e("TodaysData", "Fajar Time: " + todaysData.getFajr());
                Log.e("TodaysData", "Dhuhr Time: " + todaysData.getDhuhr());
                Log.e("TodaysData", "Asr Time: " + todaysData.getAsr());
                Log.e("TodaysData", "Maghrib Time: " + todaysData.getMaghrib());
                Log.e("TodaysData", "Isha Time: " + todaysData.getIsha());

                List<TodayTimings> todayTimingsList = new ArrayList<>();

                todayTimingsList.add(new TodayTimings(strDate, "Fajar", FajrTime));
                todayTimingsList.add(new TodayTimings(strDate, "Dhuhr", DhuhrTime));
                todayTimingsList.add(new TodayTimings(strDate, "Asr", AsrTime));
                todayTimingsList.add(new TodayTimings(strDate, "Maghrib", MaghribTime));
                todayTimingsList.add(new TodayTimings(strDate, "Isha", IshaTime));

                databaseHelper.insertIntoTodayTiming(todayTimingsList);
                DatabaseUtils.peekAllDataFromTodayTimimgs(this);
                Log.e("Retrieve...", "Data from Today Timing Retrieve Successfully...");
            }

        } else {
            Log.e("TodaysData", "Empty");
        }
    }

    private void loadTunePathJsonAndSaveIntoTable() {

        String jsonString = loadTunePathJSON();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject getData = jsonArray.getJSONObject(0);
            TPath = getData.getString("TPath");

            databaseHelper.insertIntoTunePath(TPath);
            DatabaseUtils.peekDataFromTunePath(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNotiTypeJsonAndSaveIntoTable() {

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

        ArrayList<NotiType> notiTypeList = new ArrayList();
        String jsonString = loadNotiTypeJSON();
        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject getData = jsonArray.getJSONObject(i);

                NotiType notiType = new NotiType(strDate, getData.getString("Azan"), getData.getString("Type"));
                notiTypeList.add(notiType);
            }

            if (notiTypeList.size() > 0) {
                databaseHelper.insertIntoNotiType(notiTypeList);
                DatabaseUtils.peekAllDataFromNotiType(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDTSJsonAndSaveIntoTable() {

        String jsonString = loadDTSJSON();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject getData = jsonArray.getJSONObject(0);
            DTSValue = getData.getString("Value");

            databaseHelper.insertIntoDTS(DTSValue);
            DatabaseUtils.peekDataFromDTS(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadManualCorrectionJsonAndSaveIntoTable() {

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

        ArrayList<ManualCorrection> manualCorrectionList = new ArrayList();
        String jsonString = loadManualCorrectionJSON();
        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject getData = jsonArray.getJSONObject(i);

                ManualCorrection manualCorrection = new ManualCorrection(strDate, getData.getString("Azan"), getData.getString("Timing"));
                manualCorrectionList.add(manualCorrection);
            }

            if (manualCorrectionList.size() > 0) {
                databaseHelper.insertIntoManualCorrection(manualCorrectionList);
                DatabaseUtils.peekAllDataFromManualCorrection(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllTimingsJsonFileAndSaveIntoDatabase() {
        ArrayList<Timings> azaanTimingsList = new ArrayList();
        String jsonString = loadJSONFromAzanTimings();
        try {

            JSONObject jsonObject = new JSONObject(jsonString);
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

            if (azaanTimingsList.size() > 0) {
                databaseHelper.insertIntoAzanTable(azaanTimingsList);
                DatabaseUtils.peekAllDataFromTimimgs(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetAllValuesAndData() {

        databaseHelper.deleteAzanTimings();
        DatabaseUtils.peekAllDataFromTimimgs(this);

        databaseHelper.deleteTodayAzanTimings();
        DatabaseUtils.peekAllDataFromTodayTimimgs(this);

        sharedPreferenceHandler.setDtsValue("");
        sharedPreferenceHandler.setTunePath("");
    }

    //Load JSON's...............................................
    private String loadJSONFromAzanTimings() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("AdhanTimings.json");
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

    private String loadManualCorrectionJSON() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("ManualCorrection.json");
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

    private String loadDTSJSON() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("DTS.json");
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

    private String loadNotiTypeJSON() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("NotiType.json");
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

    private String loadTunePathJSON() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("TunePath.json");
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