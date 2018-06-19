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
import java.util.Iterator;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/28/2018.
 */

public class AlarmSchedular extends Service {

    File AdhanTimingJsonPath, ManualCorrectionJsonPath, DtsJsonPath, NotitypeJsonPath;
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
            AdhanTimingJsonPath = new File(intent.getStringExtra("adhan_timing_json"));
            ManualCorrectionJsonPath = new File(intent.getStringExtra("mc_timing_json"));
            DtsJsonPath = new File(intent.getStringExtra("dts_json"));
            NotitypeJsonPath = new File(intent.getStringExtra("notitype_json"));

            if (AdhanTimingJsonPath.exists() && ManualCorrectionJsonPath.exists() && DtsJsonPath.exists() && NotitypeJsonPath.exists()) {
                resetAllValuesAndData();

                try {
                    loadAllTimingsJsonFileAndSaveIntoDatabase();
                    loadManualCorrectionJsonAndSaveIntoTable();
                    loadDTSJsonAndSaveIntoTable();
                    loadNotiTypeJsonAndSaveIntoTable();
                    getTodayDataFromMasterAndSaveInTodayData();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Peek All Data from Main Data with Actual Time on which Alarm is Triggered.........................
                Calendar getDate = Calendar.getInstance();
                String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

                strTime = getCurrentTime();
                getDataFromMainDataAndInsertIntoTodayTiming();

                //Get Nearest Next Time.............................
                //    String nearestTime = databaseHelper.getNearestTime(strTime);
                String nearestTime = databaseHelper.getNearest(strTime);

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

                    Log.e("alarmPArams", "Alarm: " + alarmParameters.getAzanName() + "\n" + ",Time: " + alarmParameters.getTime() + "\n" + " ,NotiType: " + alarmParameters.getNotiType() + "\n" + "TuneType: " + alarmParameters.getTunePath());

                    Alarm alarm = new Alarm();
                    alarm.setAlarm(cal, nearestTime, this, 1, alarmParameters);

                    stopSelf();
                }

                //Else Set Next Alarm of Fajar on Next Day..............................
                else {
                    Log.e("Else", "Running");

                    //Delete Previous Data of today Date and Set Data of next Day......................
                    databaseHelper.deleteTodayAzanTimings();
                    try {
                        getNextDayDataFromMasterAndSaveInTodayData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                        }
                        databaseHelper.deleteTodayAzanTimings();
                        databaseHelper.insertIntoTodayTiming(todayTimingsArrayList);

                        //Peek Data After Data Updated to next date in Today Table.........................
                        Log.e("Data In Today Timing Status", " Updated");
                        DatabaseUtils.peekAllDataFromTodayTimimgs(this);

                        //Split Time into Hours and Minutes...................
                        String[] splitTime = nearestTime.split(" ");
                        String splitPKT = splitTime[0];

                        String[] splitHour = splitPKT.split("\\:");
                        String hour = splitHour[0];
                        String minute = splitHour[1];

                        //Split Date into years, month and date............
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

                        Log.e("alarmPArams", "Alarm: " + alarmParameters.getAzanName() + "\n" + " ,NotiType: " + alarmParameters.getNotiType() + "\n" + "TuneType: " + alarmParameters.getTunePath());

                        Alarm alarm = new Alarm();
                        alarm.setAlarm(cal, FajrTime, this, 1, alarmParameters);

                        stopSelf();

                        Log.e("Timing Updated", "Get Data After Main Data Inserted");

                    } else {
                        Log.e("AgentMainData", "Empty");
                    }
                }
            } else {
                stopSelf();
            }
        } else {
            stopSelf();
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

        databaseHelper.deleteAzanTimings();
        databaseHelper.deleteTodayAzanTimings();
        databaseHelper.deletefromNotiType();
        databaseHelper.deletefromManualCorrection();
        databaseHelper.deletefromDTS();

        DatabaseUtils.peekAllDataFromTodayTimimgs(this);
    }

    private void loadAllTimingsJsonFileAndSaveIntoDatabase() throws IOException {
        ArrayList<Timings> azaanTimingsList = new ArrayList();
        String jsonString = loadJSONFromAzanTimings();
        try {

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject getData = jsonArray.getJSONObject(i);
                JSONObject getTimings = getData.getJSONObject("timings");

                Iterator<?> keys = getTimings.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (key.equalsIgnoreCase("Sunrise")) {
                    } else if (key.equalsIgnoreCase("Sunset")) {
                    } else if (key.equalsIgnoreCase("Midnight")) {
                    } else {
                        Timings timingsModel = new Timings();
                        timingsModel.setDate(i + 1);
                        timingsModel.setAzan(key);
                        timingsModel.setTime(getTimings.getString(key));
                        azaanTimingsList.add(timingsModel);
                    }

                }
            }
            if (azaanTimingsList.size() > 0) {
                databaseHelper.insertIntoAzanTable(azaanTimingsList);
                DatabaseUtils.peekAllDataFromTimimgs(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadManualCorrectionJsonAndSaveIntoTable() throws IOException {

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

    private void loadDTSJsonAndSaveIntoTable() throws IOException {

        String jsonString = loadDTSJSON();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject getData = jsonArray.getJSONObject(0);

            databaseHelper.insertIntoDTS(getData.getString("value"));
            DatabaseUtils.peekDataFromDTS(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<NotiType> prepareNotiTypeList() throws IOException {

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);


        ArrayList<NotiType> notiTypeList = new ArrayList();
        String jsonString = loadNotiTypeJSON();
        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject getData = jsonArray.getJSONObject(i);

                NotiType notiType = new NotiType(strDate, getData.getString("Azan"), getData.getString("Type"), getData.getString("TPath"));
                notiTypeList.add(notiType);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notiTypeList;
    }

    private void loadNotiTypeJsonAndSaveIntoTable() throws IOException {

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);

        ArrayList<NotiType> notiTypeList = new ArrayList();
        String jsonString = loadNotiTypeJSON();
        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject getData = jsonArray.getJSONObject(i);

                NotiType notiType = new NotiType(strDate, getData.getString("Azan"), getData.getString("Type"), getData.getString("TPath"));
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

    private void getTodayDataFromMasterAndSaveInTodayData() throws IOException {

        //Get Whole Current Date....................
        Calendar getDate = Calendar.getInstance();
        String strDate = getDate.get(Calendar.YEAR) + "/" + getDate.get(Calendar.MONTH) + "/" + getDate.get(Calendar.DAY_OF_MONTH);
        Log.e("Date..", strDate);

        int date = getOnlyDate();

        List<TodayTimings> getTodayTimings = databaseHelper.getMergeTodayTiming(String.valueOf(date));

        for (TodayTimings todayTimings : getTodayTimings) {
            String[] timeSplit = todayTimings.getActualTime().split(" ");
            todayTimings.setActualTime(timeSplit[0]);
            todayTimings.setDate(strDate);
            if (todayTimings.getNotiType() == null) {
                todayTimings.setNotiType("1");
            }
            if (todayTimings.getTunePath() == null) {
                todayTimings.setTunePath("");
            }
        }

        if (getTodayTimings.size() > 0) {
            databaseHelper.insertIntoTodayTiming(getTodayTimings);
            DatabaseUtils.peekAllDataFromTodayTimimgs(this);
            Log.e("Retrieve...", "Data from Today Timing Retrieve Successfully...");
        } else {
            Log.e("TodaysData", "Empty");
        }

    }

    private void getNextDayDataFromMasterAndSaveInTodayData() throws IOException {

        String nextDate = getNextDate();
        int date = getOnlyNextDate();

        List<TodayTimings> getTodayTimings = databaseHelper.getMergeTodayTiming(String.valueOf(date));
        for (TodayTimings todayTimings : getTodayTimings) {
            String[] timeSplit = todayTimings.getActualTime().split(" ");
            todayTimings.setActualTime(timeSplit[0]);
            todayTimings.setDate(nextDate);
            if (todayTimings.getNotiType() == null) {
                todayTimings.setNotiType("1");
            }
            if (todayTimings.getTunePath() == null) {
                todayTimings.setTunePath("");
            }
        }

        if (getTodayTimings.size() > 0) {
            databaseHelper.insertIntoTodayTiming(getTodayTimings);
            DatabaseUtils.peekAllDataFromTodayTimimgs(this);
            Log.e("Retrieve...", "Data from Today Timing Retrieve Successfully...");
        } else {
            Log.e("TodaysData", "Empty");
        }

    }


    //Load JSON's...............................................
    private String loadJSONFromAzanTimings() throws IOException {
        String json = null;
        FileInputStream is = new FileInputStream(AdhanTimingJsonPath);
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
        FileInputStream is = new FileInputStream(ManualCorrectionJsonPath);
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
        FileInputStream is = new FileInputStream(DtsJsonPath);
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
        FileInputStream is = new FileInputStream(NotitypeJsonPath);
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


}