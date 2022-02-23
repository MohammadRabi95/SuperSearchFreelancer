package com.itsoft.site.blocker;

import android.content.Context;

import java.util.Date;

public class app_state {

    private app_state() {}
    private static app_state instance;
    public Context ctx;

    public static app_state getInstance()

    {
        if(instance == null)
            instance = new app_state();

        return instance;
    }

}
