package com.itsoft.site.blocker;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPref {
    private final String SharedPrefileName = "site_blocker";

    private Context _context;

    private SharedPref instance;

    public SharedPref(Context context)
    {
        _context = context;
    }

    public void SaveKeyValue(String key, String value)

    {
        SharedPreferences prefs = _context.getSharedPreferences(SharedPrefileName, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    public String GetKeyValue(String key)

    {
        SharedPreferences prefs = _context.getSharedPreferences(SharedPrefileName, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    public void saveBlockKeywordData(String value) { SaveKeyValue("keywords" , value); }

    public String GetBlockKeywords()
    {
        return GetKeyValue("keywords");
    }

}
