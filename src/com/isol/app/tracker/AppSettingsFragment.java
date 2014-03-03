package com.isol.app.tracker;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class AppSettingsFragment extends PreferenceFragment  {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_settings);
    }
}