package com.example.muhammadzeeshan.muslim360withservice.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Model.MainData;
import com.example.muhammadzeeshan.muslim360withservice.Model.ManualCorrection;
import com.example.muhammadzeeshan.muslim360withservice.Model.NotiType;
import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodaysData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/18/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Azaan";

    public static final String TABLE_AZAN_TIMIMG = "azan_timimgs";
    public static final String TABLE_DTS = "DTS";
    public static final String TABLE_MANUAL_CORRECTION = "ManualCorrection";
    public static final String TABLE_NOTITYPE = "NotiType";
    public static final String TABLE_TODAY_TIMINGS = "today_timimgs";


    private static final String CREATE_AZAN_TABLE = "CREATE TABLE " + TABLE_AZAN_TIMIMG +
            "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " Date TEXT, Fajr TEXT, Sunrise TEXT, Dhuhr TEXT, Asr TEXT, Sunset TEXT, Maghrib TEXT, Isha TEXT, Imsak TEXT, " +
            " Midnight TEXT );";

    private static final String CREATE_MANUAL_CORRECTION_TABLE = "CREATE TABLE " + TABLE_MANUAL_CORRECTION +
            "(Date TEXT, Azan TEXT, Time TEXT); ";

    private static final String CREATE_DTS_TABLE = "CREATE TABLE " + TABLE_DTS +
            "(Value TEXT); ";

    private static final String CREATE_NOTI_TYPE_TABLE = "CREATE TABLE " + TABLE_NOTITYPE +
            "(Date TEXT, Azan TEXT, NotiType TEXT, TunePath TEXT); ";

    private static final String CREATE_TODAY_TIMINGS_TABLE = "CREATE TABLE " + TABLE_TODAY_TIMINGS +
            "(Date TEXT, Azan TEXT, Time TEXT, NotiType TEXT, TunePath TEXT); ";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_AZAN_TABLE);
        sqLiteDatabase.execSQL(CREATE_MANUAL_CORRECTION_TABLE);
        sqLiteDatabase.execSQL(CREATE_DTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_NOTI_TYPE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TODAY_TIMINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_AZAN_TIMIMG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MANUAL_CORRECTION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTITYPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TODAY_TIMINGS);
    }


    /* Azaan Timing Functions */
    public void insertIntoAzanTable(List<Timings> timingsList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (Timings timings : timingsList) {
            ContentValues values = new ContentValues();

            values.put("Date", timings.getDate());
            values.put("Fajr", timings.getFajr());
            values.put("Sunrise", timings.getSunrise());
            values.put("Dhuhr", timings.getDhuhr());
            values.put("Asr", timings.getAsr());
            values.put("Sunset", timings.getSunset());
            values.put("Maghrib", timings.getMaghrib());
            values.put("Isha", timings.getIsha());
            values.put("Imsak", timings.getImsak());
            values.put("Midnight", timings.getMidnight());

            db.insert(TABLE_AZAN_TIMIMG, null, values);
        }

        db.close();
    }

    public void deleteAzanTimings() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_AZAN_TIMIMG;
        db.execSQL(query);
        db.close();
    }

    public List<Timings> getAllTimings() {
        List<Timings> timingsList = new ArrayList<Timings>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_AZAN_TIMIMG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Timings timings = new Timings();

                timings.setId(cursor.getString(0));
                timings.setDate(Integer.parseInt(cursor.getString(1)));
                timings.setFajr(cursor.getString(2));
                timings.setSunrise(cursor.getString(3));
                timings.setDhuhr(cursor.getString(4));
                timings.setAsr(cursor.getString(5));
                timings.setSunset(cursor.getString(6));
                timings.setMaghrib(cursor.getString(7));
                timings.setIsha(cursor.getString(8));
                timings.setImsak(cursor.getString(9));
                timings.setMidnight(cursor.getString(10));


                // Adding contact to list
                timingsList.add(timings);
            } while (cursor.moveToNext());
        }

        return timingsList;
    }


    /* Maunal Correction Table Functions */
    public void insertIntoManualCorrection(List<ManualCorrection> manualCorrectionList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (ManualCorrection manualCorrection : manualCorrectionList) {
            ContentValues values = new ContentValues();

            values.put("Date", manualCorrection.getDate());
            values.put("Azan", manualCorrection.getAzan());
            values.put("Time", manualCorrection.getTiming());

            db.insert(TABLE_MANUAL_CORRECTION, null, values);
        }

        db.close();
    }

    public List<ManualCorrection> getAllManualCorrectionData() {
        List<ManualCorrection> manualCorrectionList = new ArrayList<ManualCorrection>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_MANUAL_CORRECTION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ManualCorrection manualCorrection = new ManualCorrection();

                manualCorrection.setDate(cursor.getString(0));
                manualCorrection.setAzan(cursor.getString(1));
                manualCorrection.setTiming(cursor.getString(2));

                // Adding contact to list
                manualCorrectionList.add(manualCorrection);

            } while (cursor.moveToNext());
        }

        return manualCorrectionList;
    }


    /* DTS Table Functions */
    public void insertIntoDTS(String value) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Value", value);

        db.insert(TABLE_DTS, null, values);

        db.close();
    }

    public String getDTSData() {
        String DTS_Value = "";
        String selectQuery = "SELECT * FROM " + TABLE_DTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToNext()) {

            DTS_Value = cursor.getString(0);
        }
        return DTS_Value;
    }


    /* Noti_Type Table Functions */
    public void insertIntoNotiType(List<NotiType> notiTypeList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (NotiType notiType : notiTypeList) {
            ContentValues values = new ContentValues();

            values.put("Azan", notiType.getAzan());
            values.put("NotiType", notiType.getType());
            values.put("TunePath", notiType.getTunePath());

            db.insert(TABLE_NOTITYPE, null, values);
        }

        db.close();
    }

    public List<NotiType> getAllNotiTypeData() {
        List<NotiType> notiTypeList = new ArrayList<NotiType>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NOTITYPE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NotiType notiType = new NotiType();

                notiType.setAzan(cursor.getString(0));
                notiType.setType(cursor.getString(1));
                notiType.setTunePath(cursor.getString(2));

                // Adding contact to list
                notiTypeList.add(notiType);
            } while (cursor.moveToNext());
        }

        return notiTypeList;
    }


    /* Today_Timings Table Functions */
    public void insertIntoTodayTiming(List<TodayTimings> todayTimingsList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (TodayTimings todayTimings : todayTimingsList) {
            ContentValues values = new ContentValues();

            values.put("Date", todayTimings.getDate());
            values.put("Azan", todayTimings.getAzan());
            values.put("Time", todayTimings.getActualTime());
            values.put("NotiType", todayTimings.getNotiType());
            values.put("TunePath", todayTimings.getTunePath());

            db.insert(TABLE_TODAY_TIMINGS, null, values);
        }

        db.close();
    }

    public List<TodaysData> getTodaysDataFromAzanTiming(String date) {
        List<TodaysData> todaysDataList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_AZAN_TIMIMG, new String[]{"*"}, "Date" + " =? ", new String[]{date}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TodaysData todaysData = new TodaysData();

                todaysData.setDate(cursor.getString(1));
                todaysData.setFajr(cursor.getString(2));
                todaysData.setDhuhr(cursor.getString(4));
                todaysData.setAsr(cursor.getString(5));
                todaysData.setMaghrib(cursor.getString(7));
                todaysData.setIsha(cursor.getString(8));

                // Adding contact to list
                todaysDataList.add(todaysData);
            } while (cursor.moveToNext());
        }

        return todaysDataList;
    }

    public List<TodayTimings> getAllTodayTimingData() {
        List<TodayTimings> todayTimingsList = new ArrayList<TodayTimings>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TODAY_TIMINGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TodayTimings todayTimings = new TodayTimings();

                todayTimings.setDate(cursor.getString(0));
                todayTimings.setAzan(cursor.getString(1));
                todayTimings.setActualTime(cursor.getString(2));
                todayTimings.setNotiType(cursor.getString(3));
                todayTimings.setTunePath(cursor.getString(4));

                // Adding contact to list
                todayTimingsList.add(todayTimings);
            } while (cursor.moveToNext());
        }

        return todayTimingsList;
    }

    public String getNearestTime(String time) {
        String nextTime = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "WITH RECURSIVE today(Time) AS (" +
                " VALUES('')" +
                " UNION ALL " +
                " SELECT today_timimgs.Time" +
                " FROM today_timimgs, today WHERE '" + time +
                "' < today_timimgs.Time And '" + time +
                "' > today.Time)" +
                " SELECT * FROM today " +
                " Where Time <> ''" +
                " Order by Time" +
                " LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            nextTime = cursor.getString(0);
        }
        return nextTime;
    }

    public void deleteTodayAzanTimings() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_TODAY_TIMINGS;
        db.execSQL(query);
        db.close();
    }

    /* Update Status in Today Timing Table */
    public int UpdateTodayTimingTable(String time) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("status", 1);

        int count = database.update(TABLE_TODAY_TIMINGS, values, "Timing = '" + time + "'", null);
        return count;
    }

    public int getDateCount(String date) {
        String countQuery = "SELECT * FROM " + TABLE_TODAY_TIMINGS + " WHERE Date ='" + date + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        Log.e("Cursor Count", String.valueOf(count));
        cursor.close();
        return count;
    }


    /* Get Actual Times After DTS And Manual Correction With join Functions */
    public List<MainData> getAlarmTriggerTime(String date) {
        List<MainData> alarmTriggerList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT TT.Azan, " +
                " time(time(TT.Time, CASE WHEN MC.Time >= 0 THEN '+'  ELSE '' END || MC.Time || ' minute')" +
                ", CASE WHEN DTS.Value >= 0 THEN '+'  ELSE '' END  || DTS.Value || ' minute') AS AT, " +
                " TT.NotiType, TT.TunePath " +
                " FROM today_timimgs TT " +
                " LEFT JOIN ManualCorrection MC ON TT.Azan = MC.Azan" +
                " CROSS JOIN DTS DTS" +
//                " LEFT JOIN NotiType TT ON TT.Azan = NT.Azan" +
                " WHERE TT.Date = '" + date + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MainData mainData = new MainData();

                mainData.setAzan(cursor.getString(0));
                mainData.setTime(cursor.getString(1));
                mainData.setNotiType(cursor.getString(2));
                mainData.setTunePath(cursor.getString(3));

                alarmTriggerList.add(mainData);
            }
            while (cursor.moveToNext());
        }
        return alarmTriggerList;
    }

}