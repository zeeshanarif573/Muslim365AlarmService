package com.example.muhammadzeeshan.muslim360withservice.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Model.AlarmParameters;
import com.example.muhammadzeeshan.muslim360withservice.Model.MainData;
import com.example.muhammadzeeshan.muslim360withservice.Model.ManualCorrection;
import com.example.muhammadzeeshan.muslim360withservice.Model.NotiType;
import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/18/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_AZAN_TIMIMG = "azan_timimgs";
    public static final String TABLE_DTS = "DTS";
    public static final String TABLE_MANUAL_CORRECTION = "ManualCorrection";
    public static final String TABLE_NOTITYPE = "NotiType";
    public static final String TABLE_TODAY_TIMINGS = "today_timimgs";
    public static final String TABLE_DAILY_VERSE = "daily_verse";
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Azaan";
    private static final String CREATE_AZAN_TABLE = "CREATE TABLE " + TABLE_AZAN_TIMIMG +
            "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " Date TEXT, AzanName TEXT, Time TEXT);";

    private static final String CREATE_MANUAL_CORRECTION_TABLE = "CREATE TABLE " + TABLE_MANUAL_CORRECTION +
            "(Date TEXT, Azan TEXT, Time TEXT); ";

    private static final String CREATE_DTS_TABLE = "CREATE TABLE " + TABLE_DTS +
            "(Value TEXT); ";

    private static final String CREATE_NOTI_TYPE_TABLE = "CREATE TABLE " + TABLE_NOTITYPE +
            "(Date TEXT, Azan TEXT, NotiType TEXT, TunePath TEXT); ";

    private static final String CREATE_TODAY_TIMINGS_TABLE = "CREATE TABLE " + TABLE_TODAY_TIMINGS +
            "(Date TEXT, Azan TEXT, Time TEXT, NotiType TEXT, TunePath TEXT); ";

    private static final String CREATE_DAILY_VERSE_TABLE = "CREATE TABLE " + TABLE_DAILY_VERSE +
            "(Date TEXT, Title TEXT, Time TEXT, Arabic TEXT, English TEXT); ";


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
        sqLiteDatabase.execSQL(CREATE_DAILY_VERSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_AZAN_TIMIMG);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MANUAL_CORRECTION);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DTS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTITYPE);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TODAY_TIMINGS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY_VERSE);
            onCreate(sqLiteDatabase);
        }
    }


    /* Azaan Timing Functions */
    public void insertIntoAzanTable(List<Timings> timingsList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (Timings timings : timingsList) {
            ContentValues values = new ContentValues();

            values.put("Date", timings.getDate());
            values.put("AzanName", timings.getAzan());
            values.put("Time", timings.getTime());

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
                timings.setAzan(cursor.getString(2));
                timings.setTime(cursor.getString(3));
                // Adding contact to list
                timingsList.add(timings);
            } while (cursor.moveToNext());
        }

        db.close();
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

        db.close();
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
        db.close();
        return DTS_Value;
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

    public List<TodayTimings> getMergeTodayTiming(String date) {

        List<TodayTimings> todayTimingsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "Select AT.Date, AT.AzanName, AT.Time, NT.NotiType, NT.TunePath " +
                "FROM " + TABLE_AZAN_TIMIMG + " " +
                "AT LEFT JOIN " + TABLE_NOTITYPE + " NT ON AT.AzanName = NT.Azan " +
                "WHERE AT.Date = '" + date + "'";

        Cursor cursor = db.rawQuery(query, null);

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

        db.close();
        return todayTimingsList;
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

        db.close();
        return todayTimingsList;
    }

    public TodayTimings getCurrentAzanParams(String time) {
        TodayTimings todayTimings = new TodayTimings();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TODAY_TIMINGS + " where Time ='" + time + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                todayTimings.setAzan(cursor.getString(1));
                todayTimings.setActualTime(cursor.getString(2));
                todayTimings.setNotiType(cursor.getString(3));
                todayTimings.setTunePath(cursor.getString(4));

            } while (cursor.moveToNext());
        }

        db.close();
        return todayTimings;
    }

    public String getNearest(String time) {
        String nextTime = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT today_timimgs.Time FROM today_timimgs Where today_timimgs.Time > '" + time + "' Limit 1";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            nextTime = cursor.getString(0);
        }
        db.close();
        return nextTime;
    }


    /* NotiType Table Functions */
    public void insertIntoNotiType(List<NotiType> notiTypeList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (NotiType notiType : notiTypeList) {
            ContentValues values = new ContentValues();

            values.put("Date", notiType.getDate());
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

                notiType.setDate(cursor.getString(0));
                notiType.setAzan(cursor.getString(1));
                notiType.setType(cursor.getString(2));
                notiType.setTunePath(cursor.getString(3));

                // Adding contact to list
                notiTypeList.add(notiType);
            } while (cursor.moveToNext());
        }

        db.close();
        return notiTypeList;
    }

    public AlarmParameters getAlarmParams(String time) {

        AlarmParameters alarmParameters = new AlarmParameters();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select Azan, NotiType, TunePath,Time from today_timimgs where Time ='" + time + "'";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            alarmParameters.setAzanName(cursor.getString(0));
            alarmParameters.setNotiType(cursor.getString(1));
            alarmParameters.setTunePath(cursor.getString(2));
            alarmParameters.setTime(cursor.getString(3));

        }
        db.close();
        return alarmParameters;
    }


    /* Delete Functions */
    public void deleteTodayAzanTimings() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_TODAY_TIMINGS;
        db.execSQL(query);
        db.close();
    }

    public void deletefromNotiType() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_NOTITYPE;
        db.execSQL(query);
        db.close();
    }

    public void deletefromManualCorrection() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_MANUAL_CORRECTION;
        db.execSQL(query);
        db.close();
    }

    public void deletefromDTS() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_DTS;
        db.execSQL(query);
        db.close();
    }


    public int getDateCount(String date) {
        String countQuery = "SELECT * FROM " + TABLE_TODAY_TIMINGS + " WHERE Date ='" + date + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        Log.e("Cursor Count", String.valueOf(count));

        db.close();
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
                " LEFT JOIN NotiType NT ON TT.Azan = NT.Azan" +
                " WHERE TT.Date = '" + date + "'" +
                " Order by TT.Time ";

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
        db.close();
        return alarmTriggerList;
    }

}