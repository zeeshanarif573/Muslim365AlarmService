package com.example.muhammadzeeshan.muslim360withservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Muhammad Zeeshan on 4/18/2018.
 */

public class SharedPreference {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    public SharedPreference(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    /* Login */
    public int getNotiId() {
        return preferences.getInt("noti_id", 1);
    }

    public void setNotiId(int id) {
        editor.putInt("noti_id", id);
        editor.apply();
    }
}
