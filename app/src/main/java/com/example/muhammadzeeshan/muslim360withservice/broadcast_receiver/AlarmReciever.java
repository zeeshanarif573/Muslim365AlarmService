package com.example.muhammadzeeshan.muslim360withservice.broadcast_receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.muhammadzeeshan.muslim360withservice.Alarm;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.Model.AlarmParameters;
import com.example.muhammadzeeshan.muslim360withservice.Model.MainData;
import com.example.muhammadzeeshan.muslim360withservice.Model.NotiType;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodaysData;
import com.example.muhammadzeeshan.muslim360withservice.Service.AlarmNotificationSoundService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/15/2018.
 */

public class AlarmReciever extends BroadcastReceiver {

    DatabaseHelper databaseHelper;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        databaseHelper = new DatabaseHelper(context);

        String NotiType = intent.getStringExtra("notiType");
        String TunePath = intent.getStringExtra("tunePath");


        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();
        Intent intent1 = new Intent(context, AlarmNotificationSoundService.class);
        intent1.putExtra("notiType", NotiType);
        intent1.putExtra("tunePath", TunePath);
        context.startService(intent1);

        setupAlarm(context, intent.getStringExtra("nearestTime"), intent.getIntExtra("reqCode", 0));
    }


    @SuppressLint("LongLogTag")
    private void setupAlarm(Context context, String nearestTime, int requestCode) {
        String strDate;

        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Log.e("Intent: ", "nearest tIme: " + nearestTime);
        Log.e("Intent: ", "request code: " + requestCode);
        requestCode++;
        Log.e("Next Request Code: ", "" + requestCode);


        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);
        Log.e("strDate", strDate);

        //Get Current Time................................
        String strTime = getCurrentTime();

        //Get Nearest Next Time.............................
        nearestTime = databaseHelper.getNearestTime(strTime);
        Log.e("Next Nearest Time: ", nearestTime);

        if (!nearestTime.isEmpty()) {

            Log.e("If", "Running");

            //Split Time into Hours and Minutes.................
            String[] splitTime = nearestTime.split(" ");
            String splitPKT = splitTime[0];


            String[] splitHour = nearestTime.split("\\:");
            String hour = splitHour[0];
            String minute = splitHour[1];

            //Split Date into years, month and date.............
            String[] splitYear = strDate.split("\\/");
            String Year = splitYear[0];
            String Month = splitYear[1];
            String Date = splitYear[2];

            //Set Alarm at Nearest date and time...............
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(Year), Integer.parseInt(Month), Integer.parseInt(Date),
                    Integer.parseInt(hour), Integer.parseInt(minute), 00);

            AlarmParameters alarmParameters = databaseHelper.getAlarmParams(nearestTime);

            Log.e("alarmPArams", "NotiType: " + alarmParameters.getNotiType() + "\n" + "TuneType: " + alarmParameters.getTunePath());
            Alarm alarm = new Alarm();
            alarm.setAlarm(cal, nearestTime, context, requestCode, alarmParameters);

        } else {

            Log.e("Else", "Running");

            databaseHelper.deleteTodayAzanTimings();
            getTodayDataFromMasterAndSaveInTodayData();

            int index = getOnlyNextDate();
            String nextdate = getNextDate();

            ArrayList<TodayTimings> todayTimingsArrayList = new ArrayList<>();

            List<MainData> mainDataList = databaseHelper.getAlarmTriggerTime(nextdate);
            if (mainDataList.size() > 0) {
                for (MainData mainData : mainDataList) {

                    String AzanTime = mainData.getTime().substring(0, 5);

                    TodayTimings todayTimings = new TodayTimings(nextdate, mainData.getAzan(), AzanTime, mainData.getNotiType(), mainData.getTunePath());
                    todayTimingsArrayList.add(todayTimings);

                    String Fajar = mainDataList.get(0).getTime();
                    nearestTime = Fajar;

                    databaseHelper.deleteTodayAzanTimings();
                    databaseHelper.insertIntoTodayTiming(todayTimingsArrayList);

                }

                Log.e("Data In Today Timing Status", " Updated");
                DatabaseUtils.peekAllDataFromTodayTimimgs(context);

                //Split Time into Hours and Minutes...................
                String[] splitTime = nearestTime.split(" ");
                String splitPKT = splitTime[0];

                String[] splitHour = nearestTime.split("\\:");
                String hour = splitHour[0];
                String minute = splitHour[1];

                //Split Date into years, month and date.............
                String[] splitYear = strDate.split("\\/");
                String Year = splitYear[0];
                String Month = splitYear[1];

                String SplitFajrTime = nearestTime.substring(0, 5);
                String FajrTime = SplitFajrTime;

                Log.e("Fajar_Time: ", FajrTime);

                //Set Alarm at Nearest date and time...............
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(Year), Integer.parseInt(Month), index, Integer.parseInt(hour),
                        Integer.parseInt(minute), 00);

                AlarmParameters alarmParameters = databaseHelper.getAlarmParams(FajrTime);
                Log.e("alarmPArams", "NotiType: " + alarmParameters.getNotiType() + "\n" + "TuneType: " + alarmParameters.getTunePath());
                Alarm alarm = new Alarm();
                alarm.setAlarm(cal, FajrTime, context, requestCode, alarmParameters);

                Log.e("Timing Updated", "Get Data After Main Data Inserted");

            } else {
                Log.e("AgentMainData", "Empty");
            }
        }

    }

    public String getCurrentTime() {

        //Get Current Time................................
        Calendar getTime = Calendar.getInstance();

        if ((getTime.get(Calendar.HOUR_OF_DAY) < 10) && (getTime.get(Calendar.MINUTE) < 10)) {
            String strTime = "0" + getTime.get(Calendar.HOUR_OF_DAY) + ":" + "0" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
            return strTime;

        } else if (getTime.get(Calendar.HOUR_OF_DAY) < 10) {
            String strTime = "0" + getTime.get(Calendar.HOUR_OF_DAY) + ":" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
            return strTime;

        } else if (getTime.get(Calendar.MINUTE) < 10) {
            String strTime = getTime.get(Calendar.HOUR_OF_DAY) + ":" + "0" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
            return strTime;

        } else {
            String strTime = getTime.get(Calendar.HOUR_OF_DAY) + ":" + getTime.get(Calendar.MINUTE);
            Log.e("Time..", strTime);
            return strTime;
        }
    }

    private String getNextDate() {
        //Get Only Next Date....................
        Calendar onlyDate = Calendar.getInstance();
        String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
        int index = Integer.parseInt(date) + 1;

        Calendar calDate = Calendar.getInstance();
        String nextdate = calDate.get(Calendar.YEAR) + "/" + calDate.get(Calendar.MONTH) + "/" + index;

        return nextdate;
    }

    private int getOnlyNextDate() {
        //Get Only Next Date....................
        Calendar onlyDate = Calendar.getInstance();
        String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
        int index = Integer.parseInt(date) + 1;

        return index;
    }

    private void getTodayDataFromMasterAndSaveInTodayData() {

        String nextdate = getNextDate();
        int onlyDate = getOnlyNextDate();

        List<TodaysData> getTodayDataList = databaseHelper.getTodaysDataFromAzanTiming(String.valueOf(onlyDate));

        if (getTodayDataList.size() > 0) {

            for (int i = 0; i < getTodayDataList.size(); i++) {

                ArrayList<NotiType> notiTypeArrayList = prepareNotiTypeList();
                TodaysData todaysData = getTodayDataList.get(i);

                String SplitFajrTime[] = todaysData.getFajr().split(" ");
                String SplitDhuhrTime[] = todaysData.getDhuhr().split(" ");
                String SplitAsrTime[] = todaysData.getAsr().split(" ");
                String SplitMaghribTime[] = todaysData.getMaghrib().split(" ");
                String SplitIshaTime[] = todaysData.getIsha().split(" ");

                String FajrTime = SplitFajrTime[0];
                String DhuhrTime = SplitDhuhrTime[0];
                String AsrTime = SplitAsrTime[0];
                String MaghribTime = SplitMaghribTime[0];
                String IshaTime = SplitIshaTime[0];

                List<TodayTimings> todayTimingsList = new ArrayList<>();

                todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(0).getAzan(), FajrTime, notiTypeArrayList.get(0).getType(), notiTypeArrayList.get(0).getTunePath()));
                todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(1).getAzan(), DhuhrTime, notiTypeArrayList.get(1).getType(), notiTypeArrayList.get(1).getTunePath()));
                todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(2).getAzan(), AsrTime, notiTypeArrayList.get(2).getType(), notiTypeArrayList.get(2).getTunePath()));
                todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(3).getAzan(), MaghribTime, notiTypeArrayList.get(3).getType(), notiTypeArrayList.get(3).getTunePath()));
                todayTimingsList.add(new TodayTimings(nextdate, notiTypeArrayList.get(4).getAzan(), IshaTime, notiTypeArrayList.get(4).getType(), notiTypeArrayList.get(4).getTunePath()));


                databaseHelper.insertIntoTodayTiming(todayTimingsList);
                DatabaseUtils.peekAllDataFromTodayTimimgs(context);
                Log.e("Retrieve...", "Data from Today Timing Retrieve Successfully...");
            }

        } else {
            Log.e("TodaysData", "Empty");
        }
    }

    private ArrayList<NotiType> prepareNotiTypeList() {

        ArrayList<NotiType> notiTypeList = new ArrayList();
        String jsonString = loadNotiTypeJSON();
        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject getData = jsonArray.getJSONObject(i);

                NotiType notiType = new NotiType(getData.getString("Azan"), getData.getString("Type"), getData.getString("TPath"));
                notiTypeList.add(notiType);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notiTypeList;
    }

    private String loadNotiTypeJSON() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("NotiType.json");
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