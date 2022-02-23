package com.itsoft.site.blocker;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.NetworkStatus;

public class splashActivity extends MerlinActivity implements Connectable, Disconnectable, Bindable {

    private MerlinsBeard merlinsBeard;

    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        merlinsBeard = new MerlinsBeard.Builder().build(this);
        new Handler(Looper.getMainLooper()).postDelayed(() -> proceed(), 1500);
    }

    void startApp ()

    {
        if (merlinsBeard.isConnectedToWifi() || merlinsBeard.isConnectedToMobileNetwork())

        {
            merlinsBeard.hasInternetAccess(hasAccess ->

            {
                if (hasAccess)

                {
                    ServerCalls.getInstance().GetKeywordData(new ServerCalls.OnKeywordResult()

                    {
                        @Override
                        public void onSuccess(KeywordModel keywordModel)

                        {
                            if (keywordModel != null) { new SharedPref(splashActivity.this).saveBlockKeywordData(keywordModel.getKeywords()); }
                        }

                        @Override
                        public void onFailed(int errorCode, String errorMessage)

                        {
                            //Toast.makeText(splashActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    });

                }

                else { proceed(); }
            });

        }

        else { proceed();}

    }

    void proceed()

    {
        startActivity(new Intent(splashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (!networkStatus.isAvailable()) {
            onDisconnect();
        }
    }

    @Override
    protected Merlin createMerlin() {
        return new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerConnectable(this);
        registerDisconnectable(this);
        registerBindable(this);
    }

    @Override
    public void onConnect() { }

    @Override
    public void onDisconnect() { }
}
