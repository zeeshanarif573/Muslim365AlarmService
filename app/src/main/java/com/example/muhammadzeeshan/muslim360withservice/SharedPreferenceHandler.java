package com.example.muhammadzeeshan.muslim360withservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Muhammad Zeeshan on 3/28/2018.
 */

public class SharedPreferenceHandler {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SharedPreferenceHandler(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public String getDtsValue() {
        return preferences.getString("DtsValue", "");
    }

    public void setDtsValue(String dtsValue) {
        editor.putString("DtsValue", dtsValue);
        editor.apply();
    }

    public String getTunePath() {
        return preferences.getString("tunePath", "");
    }

    public void setTunePath(String tunePath) {
        editor.putString("tunePath", tunePath);
        editor.apply();
    }
}
