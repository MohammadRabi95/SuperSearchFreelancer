package com.itsoft.site.blocker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements VoiceRecognizerInterface {
    private WebView webView;
    String url = "https://www.google.co.uk/";
    LinearLayout reload, filter, back, forward,home,microphone;
    TextView webUrl;
    RelativeLayout progress;
    boolean isFilterEnabled = true;
    String isAdBlockingEnabled = "0";
    String filterValue = "bark,freeindex,tarofservice,creativepool";
    String ignoredValue = "";
    SwitchMaterial filterSwitch, adblockSwitch;
    int pageLoaderCounter = 0;
    private String urlFinished = "";
    boolean doHide = true;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_content_main);

        webView = findViewById(R.id.webview);
        filter = findViewById(R.id.filter);
        reload = findViewById(R.id.reload);
        progress = findViewById(R.id.progress);
        webUrl = findViewById(R.id.weburl);
        filterSwitch = findViewById(R.id.filterSwitch);
        back = findViewById(R.id.back);
        forward = findViewById(R.id.forward);
        adblockSwitch = findViewById(R.id.adblockSwitch);
        home = findViewById(R.id.home);
        microphone = findViewById(R.id.microphone);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "sb");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);

        reload.setOnClickListener(v -> { reset(); webView.loadUrl(GetSearchUrl(ignoredValue)); });

        back.setOnClickListener(v -> { if (webView.canGoBack()) { webView.goBack(); } });

        forward.setOnClickListener(v -> { if (webView.canGoForward()) { webView.goForward(); } });

        home.setOnClickListener(v -> { webView.clearHistory(); reset(); webView.loadUrl("https://www.google.co.uk/"); });

        filterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) { isFilterEnabled = true; hmm("Filter on");
            }
            else { isFilterEnabled = false; hmm("Filter off"); }
        });

        adblockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                AdBlocker.init(this);
                isAdBlockingEnabled = "0"; hmm("ads off"); }
            else {
                isAdBlockingEnabled = "1"; hmm("ads on"); }
        });

        microphone.setOnClickListener(v -> {
            requestRecordAudioPermission();
        });
    }

    void reset() { urlFinished = ""; doHide = true; }

    void hmm(String msg) {
//        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        reset();
        webView.loadUrl(GetSearchUrl(ignoredValue));
    }

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String  url) {
            view.loadUrl(url);
            return true;
        }


        private Map<String, Boolean> loadedUrls = new HashMap<>();
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            boolean ad;
            Log.e("shouldInterceptRequest:"," Called" );
            if (adblockSwitch.isChecked()){
                if (!loadedUrls.containsKey(url)) {
                    ad = AdBlocker.isAd(url);
                    loadedUrls.put(url, ad);
                }
                else {
                    ad = loadedUrls.get(url);
                }
                return ad ? AdBlocker.createEmptyResource() :
                        super.shouldInterceptRequest(view, url);
            }

            return super.shouldInterceptRequest(view, url);

        }

        @Override
        public void onPageFinished(WebView view, String url) {


            if (!urlFinished.equals(url))

            {
                if (!url.toLowerCase().replace("webhp","").equals("https://www.google.co.uk/"))

                {
                    if(url.contains("https://www.google.co"))

                    {
                        webView.loadUrl("javascript:(" +
                                "setTimeout(()=>{" +
                                "var allElement=document.getElementsByClassName('gLFyf')[0].value;" +
                                "if(allElement.length > 0)" +
                                "{" +
                                "console.log(allElement);"+
                                "var filterText = allElement.replace(' -\"jobs\"', '');"+
                                "console.log(filterText);"+
//                                "sb.LoadCheck(filterText);"+
                                "document.getElementsByClassName('gLFyf')[0].value = filterText;"+
                                "}" +
                                ";}, 1000))");

                        webView.loadUrl("javascript:(" +
                                "setTimeout(()=>{" +
                                "var allElement=document.getElementsByClassName('D0h3Gf')[0].value;" +
                                "if(allElement.length > 0)" +
                                "{" +
                                "console.log(allElement);"+
                                "var filterText = allElement.replace(' -\"jobs\"', '');"+
                                "console.log(filterText);"+
//                                "sb.LoadCheck(filterText);"+
                                "document.getElementsByClassName('D0h3Gf')[0].value = filterText;"+
                                "}" +
                                ";}, 1000))");
                    }

                    if (url.contains("https://www.google."))

                    {
                        webView.loadUrl("javascript:(" +
                                "setInterval(()=>{" +
                                "var filter='" + filterValue + "'; " +
                                "var ignore='" + ignoredValue + "'; " +
                                "var ad='" + isAdBlockingEnabled + "'; " +
                                "var arr=filter.split(',');" +
                                "var allElement=document.getElementsByClassName('mnr-c');" +
                                "var allElementSearchFor=document.getElementsByClassName('AuVD');" +
                                "var isAd = document.getElementsByClassName('uEierd');" +
                                "for(var j=0;j<allElement.length;j++)" +
                                "{" +
                                "var isLink = allElement[j].getElementsByClassName('jRjRIf');" +
                                "if(isLink.length > 0){ " +
                                "var srch = isLink[0].innerHTML.toLowerCase();" +
                                "for(var r=0; r<arr.length; r++){" +
                                "var finder=arr[r].toLowerCase();" +
                                "if(srch.search(finder.trim()) != -1 && ignore.search(finder.trim()) === -1){" +
                                "allElement[j].style.display = 'none';" +
                                "}" +
                                "}" +
                                "}" +
                                "}" +
                                "sb.LoadCheck(isAd.length);"+
                                "if(isAd.length > 0 && ad != 1){ " +
                                "for(var m=0;m<isAd.length;m++)" +
                                "{" +
                                "isAd[m].style.display = 'none';"+
                                "sb.LoadCheck('hide');"+
                                "}" +
                                "}" +
                                "for(var k=0;k<allElementSearchFor.length;k++)" +
                                "{" +
                                "allElementSearchFor[k].style.display = 'none'; " +
                                "}" +
                                ";}, 2000))");

                        webView.loadUrl("javascript:(" +
                                "setInterval(()=>{" +
                                "var ad='" + isAdBlockingEnabled + "'; " +
                                "var isTitle = document.getElementsByClassName('uEierd');" +
                                "if(isTitle.length > 0 && ad != 1){ " +
                                "for(var d=0;d<isTitle.length;d++)" +
                                "{" +
                                "isTitle[d].style.display = 'none';"+
                                "}" +
                                "}" +
                                ";}, 1000))");

                        webView.loadUrl("javascript:(" +
                                "setInterval(()=>{" +
                                "var jobs = document.querySelectorAll(\"[jscontroller='vfMXdb']\");" +
                                "for(var k=0;k<jobs.length;k++)" +
                                "{" +
                                "jobs[k].style.display = 'none'; " +
                                "}" +
                                ";}, 100))");

                        webView.loadUrl("javascript:(" +
                                "setInterval(()=>{" +
                                "var jobs = document.querySelectorAll(\"[jscontroller='vfMXdb']\");" +
                                "for(var k=0;k<jobs.length;k++)" +
                                "{" +
                                "jobs[k].style.display = 'none'; " +
                                "}" +
                                ";}, 100))");

                        new Handler(Looper.getMainLooper()).postDelayed(() -> setVisibility(), 2200);
                    }

                    else {setVisibility();}

                }

                else {setVisibility();reset();}

            }

            urlFinished = url;
        }

        private static final String TAG = "MainActivity";
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            progress.setVisibility(View.VISIBLE);

            if(url.contains("https://www.google.co"))

            {

//                webView.loadUrl("javascript:(" +
//                        "setTimeout(()=>{" +
//                        "var allElement=document.getElementsByClassName('gLFyf')[0].value;" +
//                        "if(allElement.length > 0)" +
//                        "{" +
//                        "console.log(allElement);"+
//                        "var filterText = allElement.replace(' -\"jobs\"', '');"+
//                        "console.log(filterText);"+
//                        "sb.LoadCheck(filterText);"+
//                        "document.getElementsByClassName('gLFyf')[0].value = filterText;"+
//                        "}" +
//                        ";}, 1000))");

                webView.loadUrl("javascript:(" +
                        "setInterval(()=>{" +
                        "var suggestion = document.querySelectorAll(\"[jscontroller='Lquguf']\");" +
                        "if(suggestion.length > 0)" +
                        "{" +
                        "for(var k=0;k<suggestion.length;k++)" +
                        "{" +
                        "suggestion[k].style.display = 'none'; " +
                        "}" +
                        "}" +
                        ";}, 10))");
            }

            if(url.contains("https://www.google.co.uk/search?q=") && doHide)

            {
                String[] hmm = url.split("=");
                String keyword = hmm[1].split("&")[0].replaceAll("\\+", " ").replace("#ip","").toLowerCase();
                if(!keyword.contains("jobs"))
                {
                    webView.loadUrl("https://www.google.co.uk/search?q=" + keyword.replace(" ","+") + " -\"jobs\"");
                    doHide = false;
                }
            }

            finishedPage(url, webView.getTitle());

        }
    };

    void setVisibility() { progress.setVisibility(View.GONE); }

    private final WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) { }
    };

    @Override
    public void onBackPressed()

    {
        WebBackForwardList list = webView.copyBackForwardList();
        int cIndex = list.getCurrentIndex();
        if (list.getCurrentItem().getUrl().contains("-%22jobs%22"))
        {
            webView.goBackOrForward(-2); reset();
        }
        else { if (webView.canGoBack()) { webView.goBack(); } else { super.onBackPressed(); } }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView = null;
    }

    private void finishedPage(String url, String title) {
        webUrl.setText(url);

        if (url.split("=").length > 1)

        {
            String[] hmm = url.split("=");
            String thisSearch = hmm[1].split("&")[0].replaceAll("\\+", " ").replaceAll("%20"," ");

            if(!ignoredValue.equals(thisSearch)) {  Log.d("--------reset", thisSearch); reset(); }

            ignoredValue = thisSearch.replace("-\"jobs\"", "").trim();
        }

    }

    @JavascriptInterface
    public void CounterCheck(String value) {  }

    @JavascriptInterface
    public void LoadCheck(String value) {
        //Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
        Log.d("--------ad",value);
    }

    String GetSearchUrl(String str) { return "https://www.google.co.uk/search?q=" + str.replace(" ", "+"); }

    void loadFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null && fragmentManager.findFragmentByTag("dialogVoiceRecognizer") == null && !isFinishing()) {
            VoiceRecognizerFragment languageDialogFragment = new VoiceRecognizerFragment(this,this);
            languageDialogFragment.show(fragmentManager, "dialogVoiceRecognizer");
        }
    }
    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            } else {
                loadFragment();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == 101) {

        }
    }

    @Override
    public void spokenText(String spokenText) {
        Toast.makeText(this, spokenText, Toast.LENGTH_SHORT).show();
        webView.loadUrl(GetSearchUrl(spokenText));
    }
}
