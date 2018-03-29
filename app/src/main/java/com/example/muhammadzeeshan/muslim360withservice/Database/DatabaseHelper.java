package com.example.muhammadzeeshan.muslim360withservice.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodayTimings;
import com.example.muhammadzeeshan.muslim360withservice.Model.TodaysData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Zeeshan on 3/18/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Azan";
    public static final String TABLE_AZAN_TIMIMG = "azan_timimgs";
    public static final String TABLE_TODAY_TIMINGS = "today_timimgs";
    String getTime;


    private static final String CREATE_AZAN_TABLE = "CREATE TABLE " + TABLE_AZAN_TIMIMG +
            "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " Date TEXT, Fajr TEXT, Sunrise TEXT, Dhuhr TEXT, Asr TEXT, Sunset TEXT, Maghrib TEXT, Isha TEXT, Imsak TEXT, " +
            " Midnight TEXT );";

    private static final String CREATE_TODAY_TIMINGS_TABLE = "CREATE TABLE " + TABLE_TODAY_TIMINGS +
            "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Date TEXT, Azan TEXT, Timing TEXT, Status TEXT); ";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_AZAN_TABLE);
        sqLiteDatabase.execSQL(CREATE_TODAY_TIMINGS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_AZAN_TIMIMG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TODAY_TIMINGS);
    }

     /* Timings Table Functions */

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

     /* Today_Timings Table Functions */

    public void insertIntoTodayTiming(List<TodayTimings> todayTimingsList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (TodayTimings todayTimings : todayTimingsList) {
            ContentValues values = new ContentValues();

            values.put("Date", todayTimings.getDate());
            values.put("Azan", todayTimings.getAzan());
            values.put("Timing", todayTimings.getTiming());
            values.put("Status", todayTimings.getStatus());

            db.insert(TABLE_TODAY_TIMINGS, null, values);
        }

        db.close();
    }

    public List<TodayTimings> getAllTodayTimings() {
        List<TodayTimings> timingsList = new ArrayList<TodayTimings>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TODAY_TIMINGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TodayTimings todayTimings = new TodayTimings();

                todayTimings.setId(cursor.getString(0));
                todayTimings.setDate(cursor.getString(1));
                todayTimings.setAzan(cursor.getString(2));
                todayTimings.setTiming(cursor.getString(3));
                todayTimings.setStatus(cursor.getString(4));

                // Adding contact to list
                timingsList.add(todayTimings);
            } while (cursor.moveToNext());
        }

        return timingsList;
    }

    public List<TodaysData> getTodaysData(String date) {
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

    public String getNearestTime(String time) {
        String nextTime = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "WITH RECURSIVE today(Timing) AS (" +
                " VALUES('')" +
                " UNION ALL " +
                " SELECT today_timimgs.Timing" +
                " FROM today_timimgs, today WHERE '" + time +
                "' < today_timimgs.Timing And '" + time +
                "' > today.Timing)" +
                " SELECT * FROM today " +
                " Where Timing <> ''" +
                " Order by Timing" +
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


}