package com.example.muhammadzeeshan.muslim360withservice.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Alarm;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseHelper;
import com.example.muhammadzeeshan.muslim360withservice.Database.DatabaseUtils;
import com.example.muhammadzeeshan.muslim360withservice.Model.AlarmParameters;
import com.example.muhammadzeeshan.muslim360withservice.Model.MainData;
import com.example.muhammadzeeshan.muslim360withservice.Model.ManualCorrection;
import com.example.muhammadzeeshan.muslim360withservice.Model.NotiType;
import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodaysData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/28/2018.
 */

public class AlarmSchedular extends Service {

    String AdhanTimingJsonPath, ManualCorrectionJsonPath, DtsJsonPath, NotitypeJsonPath;
    DatabaseHelper databaseHelper;
    String strTime;

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
    }

    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("AlarmSchedular", "onStartCommand");

        if (intent.getExtras() != null) {
            AdhanTimingJsonPath = intent.getStringExtra("adhan_timing_json");
            ManualCorrectionJsonPath = intent.getStringExtra("mc_timing_json");
            DtsJsonPath = intent.getStringExtra("dts_json");
            NotitypeJsonPath = intent.getStringExtra("notitype_json");
        }

        resetAllValuesAndData();

        loadAllTimingsJsonFileAndSaveIntoDatabase();
        getTodayDataFromMasterAndSaveInTodayData();
        loadManualCorrectionJsonAndSaveIntoTable();
        loadDTSJsonAndSaveIntoTable();
        loadNotiTypeJsonAndSaveIntoTable();


        // Peek All Data from Main Data with Actual Time on which Alarm is Triggered.........................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

        strTime = getCurrentTime();
        getDataFromMainDataAndInsertIntoTodayTiming();

        //Get Nearest Next Time.............................
        String nearestTime = databaseHelper.getNearestTime(strTime);

        //If Next Alarm Exist in Todays Date then Setup ALarm.........................
        if (!nearestTime.isEmpty()) {

            Log.e("If", "Running...");

            //Split Time into Hours and Minutes.................
            String[] splitTime = nearestTime.split(" ");
            String splitPKT = splitTime[0];

            String[] splitHour = splitPKT.split("\\:");
            String hour = splitHour[0];
            String minute = splitHour[1];

            //Split Date into years, month and date.............
            String[] splitYear = strDate.split("\\/");
            String Year = splitYear[0];
            String Month = splitYear[1];
            String Date = splitYear[2];


            //Set Alarm at Nearest date and time...............
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(Year), Integer.parseInt(Month), Integer.parseInt(Date), Integer.parseInt(hour),
                    Integer.parseInt(minute), 00);

            AlarmParameters alarmParameters = databaseHelper.getAlarmParams(nearestTime);

            Log.e("alarmPArams", "NotiType: " + alarmParameters.getNotiType() + "\n" + "TuneType: " + alarmParameters.getTunePath());

            Alarm alarm = new Alarm();
            alarm.setAlarm(cal, nearestTime, this, 0, alarmParameters);

            stopSelf();
        }

        //Else Set Next Alarm of Fajar on Next Day..............................
        else {
            Log.e("Else", "Running");

            //Delete Previous Data of today Date and Set Data of next Day......................
            databaseHelper.deleteTodayAzanTimings();
            getNextDayDataFromMasterAndSaveInTodayData();

            Calendar onlyDate = Calendar.getInstance();
            String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
            int index = Integer.parseInt(date) + 1;

            String nextdate = getNextDate();
            ArrayList<TodayTimings> todayTimingsArrayList = new ArrayList<>();

            List<MainData> mainDataList = databaseHelper.getAlarmTriggerTime(nextdate);

            //Get Data from Today Table After Join Query......................................
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

                //Peek Data After Data Updated to next date in Today Table.........................
                Log.e("Data In Today Timing Status", " Updated");
                DatabaseUtils.peekAllDataFromTodayTimimgs(this);

                //Split Time into Hours and Minutes...................
                String[] splitTime = nearestTime.split(" ");
                String splitPKT = splitTime[0];

                String[] splitHour = splitPKT.split("\\:");
                String hour = splitHour[0];
                String minute = splitHour[1];

                //Split Date into years, month and date.............
                String[] splitYear = strDate.split("\\/");
                String Year = splitYear[0];
                String Month = splitYear[1];

                String SplitFajrTime = nearestTime.substring(0, 5);
                String FajrTime = SplitFajrTime;

                //Set Alarm at Nearest date and time...............
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(Year), Integer.parseInt(Month), index, Integer.parseInt(hour),
                        Integer.parseInt(minute), 00);

                AlarmParameters alarmParameters = databaseHelper.getAlarmParams(FajrTime);

                Log.e("alarmPArams", "NotiType: " + alarmParameters.getNotiType() + "\n" + "TuneType: " + alarmParameters.getTunePath());

                Alarm alarm = new Alarm();
                alarm.setAlarm(cal, FajrTime, this, 0, alarmParameters);

                stopSelf();

                Log.e("Timing Updated", "Get Data After Main Data Inserted");

            } else {
                Log.e("AgentMainData", "Empty");
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service", "Destroy");
    }

    private void getDataFromMainDataAndInsertIntoTodayTiming() {

        ArrayList<TodayTimings> todayTimingsArrayList = new ArrayList<>();

        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

        List<MainData> mainDataList = databaseHelper.getAlarmTriggerTime(strDate);

        if (mainDataList.size() > 0) {
            for (MainData mainData : mainDataList) {

                String AzanTime = mainData.getTime().substring(0, 5);

                TodayTimings todayTimings = new TodayTimings(strDate, mainData.getAzan(), AzanTime, mainData.getNotiType(), mainData.getTunePath());
                todayTimingsArrayList.add(todayTimings);

                databaseHelper.deleteTodayAzanTimings();
                databaseHelper.insertIntoTodayTiming(todayTimingsArrayList);
            }

            Log.e("Today Timing Updated", "Get Data After Main Data Inserted");
            DatabaseUtils.peekAllDataFromTodayTimimgs(this);

        } else {
            Log.e("AgentMainData", "Empty");
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

    private int getOnlyDate() {

        //Get Only Next Date....................
        Calendar onlyDate = Calendar.getInstance();
        String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
        int index = Integer.parseInt(date);

        return index;
    }

    private int getOnlyNextDate() {

        //Get Only Next Date....................
        Calendar onlyDate = Calendar.getInstance();
        String date = String.valueOf(onlyDate.get(Calendar.DAY_OF_MONTH));
        int index = Integer.parseInt(date) + 1;

        return index;
    }

    private String getAlarmTime(String strTime, List<MainData> mainDataList) {
        Log.e("StrTime", strTime);
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        Date currentTime = null;
        try {
            currentTime = parser.parse(strTime);

            String ListTimeFirstSplit = currentTime.toString().substring(11, currentTime.toString().length());
            String ListTimeSecondSplit[] = ListTimeFirstSplit.split(" ");
            Log.e("currentTime: ", ListTimeSecondSplit[0]);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (MainData mainData : mainDataList) {
            try {
                Date time = parser.parse(mainData.getTime());

                String ListTimeFirstSplit = time.toString().substring(11, time.toString().length());
                String ListTimeSecondSplit[] = ListTimeFirstSplit.split(" ");
                Log.e("ListTimeSplit", ListTimeSecondSplit[0]);

                if (currentTime.before(time)) {

                    String firstSplit = time.toString().substring(11, time.toString().length());
                    String secondSplit[] = firstSplit.split(" ");
                    Log.e("Nearest Time is: ", secondSplit[0]);

                    return secondSplit[0];
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return "";
    }


    private void resetAllValuesAndData() {

        databaseHelper.deleteTodayAzanTimings();
        DatabaseUtils.peekAllDataFromTodayTimimgs(this);
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
            }

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

    private void loadDTSJsonAndSaveIntoTable() {

        String jsonString = loadDTSJSON();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject getData = jsonArray.getJSONObject(0);

            databaseHelper.insertIntoDTS(getData.getString("Value"));
            DatabaseUtils.peekDataFromDTS(this);

        } catch (Exception e) {
            e.printStackTrace();
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

    private void loadNotiTypeJsonAndSaveIntoTable() {

        ArrayList<NotiType> notiTypeList = new ArrayList();
        String jsonString = loadNotiTypeJSON();
        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject getData = jsonArray.getJSONObject(i);

                NotiType notiType = new NotiType(getData.getString("Azan"), getData.getString("Type"), getData.getString("TPath"));
                notiTypeList.add(notiType);
            }

//            if (notiTypeList.size() > 0) {
//                databaseHelper.insertIntoNotiType(notiTypeList);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTodayDataFromMasterAndSaveInTodayData() {

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);
        Log.e("Date..", strDate);

        int date = getOnlyDate();

        List<TodaysData> getTodayDataList = databaseHelper.getTodaysDataFromAzanTiming(String.valueOf(date));

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

                todayTimingsList.add(new TodayTimings(strDate, notiTypeArrayList.get(0).getAzan(), FajrTime, notiTypeArrayList.get(0).getType(), notiTypeArrayList.get(0).getTunePath()));
                todayTimingsList.add(new TodayTimings(strDate, notiTypeArrayList.get(1).getAzan(), DhuhrTime, notiTypeArrayList.get(1).getType(), notiTypeArrayList.get(1).getTunePath()));
                todayTimingsList.add(new TodayTimings(strDate, notiTypeArrayList.get(2).getAzan(), AsrTime, notiTypeArrayList.get(2).getType(), notiTypeArrayList.get(2).getTunePath()));
                todayTimingsList.add(new TodayTimings(strDate, notiTypeArrayList.get(3).getAzan(), MaghribTime, notiTypeArrayList.get(3).getType(), notiTypeArrayList.get(3).getTunePath()));
                todayTimingsList.add(new TodayTimings(strDate, notiTypeArrayList.get(4).getAzan(), IshaTime, notiTypeArrayList.get(4).getType(), notiTypeArrayList.get(4).getTunePath()));


                databaseHelper.insertIntoTodayTiming(todayTimingsList);
                DatabaseUtils.peekAllDataFromTodayTimimgs(this);
                Log.e("Retrieve...", "Data from Today Timing Retrieve Successfully...");
            }

        } else {
            Log.e("TodaysData", "Empty");
        }
    }

    private void getNextDayDataFromMasterAndSaveInTodayData() {

        String nextDate = getNextDate();
        int date = getOnlyNextDate();

        List<TodaysData> getTodayDataList = databaseHelper.getTodaysDataFromAzanTiming(String.valueOf(date));

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

                todayTimingsList.add(new TodayTimings(nextDate, notiTypeArrayList.get(0).getAzan(), FajrTime, notiTypeArrayList.get(0).getType(), notiTypeArrayList.get(0).getTunePath()));
                todayTimingsList.add(new TodayTimings(nextDate, notiTypeArrayList.get(1).getAzan(), DhuhrTime, notiTypeArrayList.get(1).getType(), notiTypeArrayList.get(1).getTunePath()));
                todayTimingsList.add(new TodayTimings(nextDate, notiTypeArrayList.get(2).getAzan(), AsrTime, notiTypeArrayList.get(2).getType(), notiTypeArrayList.get(2).getTunePath()));
                todayTimingsList.add(new TodayTimings(nextDate, notiTypeArrayList.get(3).getAzan(), MaghribTime, notiTypeArrayList.get(3).getType(), notiTypeArrayList.get(3).getTunePath()));
                todayTimingsList.add(new TodayTimings(nextDate, notiTypeArrayList.get(4).getAzan(), IshaTime, notiTypeArrayList.get(4).getType(), notiTypeArrayList.get(4).getTunePath()));


                databaseHelper.insertIntoTodayTiming(todayTimingsList);
            }

        } else {
            Log.e("TodaysData", "Empty");
        }
    }


    //Load JSON's...............................................
    private String loadJSONFromAzanTimings() {
        String json = null;
        try {
            File file = new File(AdhanTimingJsonPath);
            FileInputStream is = new FileInputStream(file);
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
            File file = new File(ManualCorrectionJsonPath);
            FileInputStream is = new FileInputStream(file);
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
            File file = new File(DtsJsonPath);
            FileInputStream is = new FileInputStream(file);
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
            File file = new File(NotitypeJsonPath);
            FileInputStream is = new FileInputStream(file);
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