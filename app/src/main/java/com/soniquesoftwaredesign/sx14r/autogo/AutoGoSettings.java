package com.soniquesoftwaredesign.sx14r.autogo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by SX14R on 8/15/2014.
 */
public class AutoGoSettings extends PreferenceActivity {

    private static final String PREFERENCES = "AutoGo Preferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(PREFERENCES);
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        addPreferencesFromResource(R.xml.preferences);


    }




}
