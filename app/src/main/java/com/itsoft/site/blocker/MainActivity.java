package com.itsoft.site.blocker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements VoiceRecognizerInterface {
    private WebView webView;
    String url = "https://www.google.co.uk/";
    LinearLayout reload, filter, back, forward, home, microphone;
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
    private EditText searchEditText;

//    private static String file_type = "*/*";
//    private String cam_file_data = null;
//    private ValueCallback<Uri> file_data;
//    private ValueCallback<Uri[]> file_path;
//    private final static int file_req_code = 1;

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
        searchEditText = findViewById(R.id.search);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "sb");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);

        reload.setOnClickListener(v -> {
            reset();
            webView.loadUrl(GetSearchUrl(ignoredValue));
        });

        back.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });

        forward.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });

        home.setOnClickListener(v -> {
            webView.clearHistory();
            reset();
            webView.loadUrl("https://www.google.co.uk/");
        });

        filterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isFilterEnabled = true;
                hmm("Filter on");
            } else {
                isFilterEnabled = false;
                hmm("Filter off");
            }
        });

        adblockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                isAdBlockingEnabled = "1";
                hmm("ads on");
            } else {
                AdBlocker.init(this);
                isAdBlockingEnabled = "0";
                hmm("ads off");
            }
        });

        microphone.setOnClickListener(v -> {
            requestRecordAudioPermission();
        });

//        Search text
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("JavascriptInterface")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchUrl = searchEditText.getText().toString().trim();
                webView.loadUrl(GetSearchUrl(searchUrl));
//                Toast.makeText(MainActivity.this, searchUrl, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        webView.setWebChromeClient(new WebChromeClient() {
//
//            /*-- handling input[type="file"] requests for android API 21+ --*/
//            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//
//                if (file_permission() && Build.VERSION.SDK_INT >= 21) {
//                    file_path = filePathCallback;
//                    Intent takePictureIntent = null;
//                    Intent takeVideoIntent = null;
//
//                    boolean includeVideo = false;
//                    boolean includePhoto = false;
//
//                    /*-- checking the accept parameter to determine which intent(s) to include --*/
//
//                    paramCheck:
//                    for (String acceptTypes : fileChooserParams.getAcceptTypes()) {
//                        String[] splitTypes = acceptTypes.split(", ?+");
//                        /*-- although it's an array, it still seems to be the whole value; split it out into chunks so that we can detect multiple values --*/
//                        for (String acceptType : splitTypes) {
//                            switch (acceptType) {
//                                case "*/*":
//                                    includePhoto = true;
//                                    includeVideo = true;
//                                    break paramCheck;
//                                case "image/*":
//                                    includePhoto = true;
//                                    break;
//                                case "video/*":
//                                    includeVideo = true;
//                                    break;
//                            }
//                        }
//                    }
//
//                    if (fileChooserParams.getAcceptTypes().length == 0) {
//
//                        /*-- no `accept` parameter was specified, allow both photo and video --*/
//
//                        includePhoto = true;
//                        includeVideo = true;
//                    }
//
//                    if (includePhoto) {
//                        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
//                            File photoFile = null;
//                            try {
//                                photoFile = create_image();
//                                takePictureIntent.putExtra("PhotoPath", cam_file_data);
//                            } catch (IOException ex) {
//                                Log.e("TAG", "Image file creation failed", ex);
//                            }
//                            if (photoFile != null) {
//                                cam_file_data = "file:" + photoFile.getAbsolutePath();
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                            } else {
//                                cam_file_data = null;
//                                takePictureIntent = null;
//                            }
//                        }
//                    }
//
//                    if (includeVideo) {
//                        takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                        if (takeVideoIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
//                            File videoFile = null;
//                            try {
//                                videoFile = create_video();
//                            } catch (IOException ex) {
//                                Log.e("TAG", "Video file creation failed", ex);
//                            }
//                            if (videoFile != null) {
//                                cam_file_data = "file:" + videoFile.getAbsolutePath();
//                                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
//                            } else {
//                                cam_file_data = null;
//                                takeVideoIntent = null;
//                            }
//                        }
//                    }
//
//                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//                    contentSelectionIntent.setType(file_type);
//
//
//                    Intent[] intentArray;
//                    if (takePictureIntent != null && takeVideoIntent != null) {
//                        intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
//                    } else if (takePictureIntent != null) {
//                        intentArray = new Intent[]{takePictureIntent};
//                    } else if (takeVideoIntent != null) {
//                        intentArray = new Intent[]{takeVideoIntent};
//                    } else {
//                        intentArray = new Intent[0];
//                    }
//
//                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//                    startActivityForResult(chooserIntent, file_req_code);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//
//
//        });

    }

    void reset() {
        urlFinished = "";
        doHide = true;
    }

    void hmm(String msg) {
//        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        reset();
        webView.loadUrl(GetSearchUrl(ignoredValue));
    }

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        private Map<String, Boolean> loadedUrls = new HashMap<>();

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            boolean ad;
            Log.e("shouldInterceptRequest:", " Called");
            if (!adblockSwitch.isChecked()) {
                if (!loadedUrls.containsKey(url)) {
                    ad = AdBlocker.isAd(url);
                    loadedUrls.put(url, ad);
                } else {
                    ad = loadedUrls.get(url);
                }
                return ad ? AdBlocker.createEmptyResource() :
                        super.shouldInterceptRequest(view, url);
            }

            return super.shouldInterceptRequest(view, url);

        }

        @Override
        public void onPageFinished(WebView view, String url) {


            if (!urlFinished.equals(url)) {
                if (!url.toLowerCase().replace("webhp", "").equals("https://www.google.co.uk/")) {
                    if (url.contains("https://www.google.co")) {
                        webView.loadUrl("javascript:(" +
                                "setTimeout(()=>{" +
                                "var allElement=document.getElementsByClassName('gLFyf')[0].value;" +
                                "if(allElement.length > 0)" +
                                "{" +
                                "console.log(allElement);" +
                                "var filterText = allElement.replace(' -\"jobs\"', '');" +
                                "console.log(filterText);" +
//                                "sb.LoadCheck(filterText);"+
                                "document.getElementsByClassName('gLFyf')[0].value = filterText;" +
                                "}" +
                                ";}, 1000))");

                        webView.loadUrl("javascript:(" +
                                "setTimeout(()=>{" +
                                "var allElement=document.getElementsByClassName('D0h3Gf')[0].value;" +
                                "if(allElement.length > 0)" +
                                "{" +
                                "console.log(allElement);" +
                                "var filterText = allElement.replace(' -\"jobs\"', '');" +
                                "console.log(filterText);" +
//                                "sb.LoadCheck(filterText);"+
                                "document.getElementsByClassName('D0h3Gf')[0].value = filterText;" +
                                "}" +
                                ";}, 1000))");
                    }

                    if (url.contains("https://www.google.")) {
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
                                "sb.LoadCheck(isAd.length);" +
                                "if(isAd.length > 0 && ad != 1){ " +
                                "for(var m=0;m<isAd.length;m++)" +
                                "{" +
                                "isAd[m].style.display = 'none';" +
                                "sb.LoadCheck('hide');" +
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
                                "isTitle[d].style.display = 'none';" +
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
                    } else {
                        setVisibility();
                    }

                } else {
                    setVisibility();
                    reset();
                }

            }

            urlFinished = url;
        }

        private static final String TAG = "MainActivity";

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            progress.setVisibility(View.VISIBLE);

            if (url.contains("https://www.google.co")) {

//                view.evaluateJavascript("(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
//                        value -> {
//                            Log.d("onReceiveValueCheck:", value+" Client");
////                            if (value.contains("www.bark.com")) {
////                                Log.d(TAG, "onReceiveValue: " + value);
////                            }
//                        });

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

            if (url.contains("https://www.google.co.uk/search?q=") && doHide) {
                String[] hmm = url.split("=");
                String keyword = hmm[1].split("&")[0].replaceAll("\\+", " ").replace("#ip", "").toLowerCase();
                if (!keyword.contains("jobs")) {
                    webView.loadUrl("https://www.google.co.uk/search?q=" + keyword.replace(" ", "+") + " -\"jobs\"");
                    doHide = false;
                }
            }

            finishedPage(url, webView.getTitle());

        }
    };

    void setVisibility() {
        progress.setVisibility(View.GONE);
    }

    private final WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        }
    };

    @Override
    public void onBackPressed() {
        WebBackForwardList list = webView.copyBackForwardList();
        int cIndex = list.getCurrentIndex();
        if (list.getCurrentItem().getUrl().contains("-%22jobs%22")) {
            webView.goBackOrForward(-2);
            reset();
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView = null;
    }

    private void finishedPage(String url, String title) {
        webUrl.setText(url);

        if (url.split("=").length > 1) {
            String[] hmm = url.split("=");
            String thisSearch = hmm[1].split("&")[0].replaceAll("\\+", " ").replaceAll("%20", " ");

            if (!ignoredValue.equals(thisSearch)) {
                Log.d("--------reset", thisSearch);
                reset();
            }

            ignoredValue = thisSearch.replace("-\"jobs\"", "").trim();
        }

    }

    @JavascriptInterface
    public void CounterCheck(String value) {
    }

    @JavascriptInterface
    public void LoadCheck(String value) {
        //Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
        Log.d("--------ad", value);
    }

    String GetSearchUrl(String str) {
        return "https://www.google.co.uk/search?q=" + str.replace(" ", "+");
    }

    void loadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null && fragmentManager.findFragmentByTag("dialogVoiceRecognizer") == null && !isFinishing()) {
            VoiceRecognizerFragment languageDialogFragment = new VoiceRecognizerFragment(this, this);
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        if (Build.VERSION.SDK_INT >= 21) {
//            Uri[] results = null;
//
//            /*-- if file request cancelled; exited camera. we need to send null value to make future attempts workable --*/
//            if (resultCode == Activity.RESULT_CANCELED) {
//                file_path.onReceiveValue(null);
//                return;
//            }
//
//            /*-- continue if response is positive --*/
//            if (resultCode == Activity.RESULT_OK) {
//                if (null == file_path) {
//                    return;
//                }
//                ClipData clipData;
//                String stringData;
//
//                try {
//                    clipData = intent.getClipData();
//                    stringData = intent.getDataString();
//                } catch (Exception e) {
//                    clipData = null;
//                    stringData = null;
//                }
//                if (clipData == null && stringData == null && cam_file_data != null) {
//                    results = new Uri[]{Uri.parse(cam_file_data)};
//                } else {
//                    if (clipData != null) {
//                        final int numSelectedFiles = clipData.getItemCount();
//                        results = new Uri[numSelectedFiles];
//                        for (int i = 0; i < clipData.getItemCount(); i++) {
//                            results[i] = clipData.getItemAt(i).getUri();
//                        }
//                    } else {
//                        try {
//                            Bitmap cam_photo = (Bitmap) intent.getExtras().get("data");
//                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                            cam_photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                            stringData = MediaStore.Images.Media.insertImage(this.getContentResolver(), cam_photo, null, null);
//                        } catch (Exception ignored) {
//                        }
//
//                        results = new Uri[]{Uri.parse(stringData)};
//                    }
//                }
//            }
//
//            file_path.onReceiveValue(results);
//            file_path = null;
//        } else {
//            if (requestCode == file_req_code) {
//                if (null == file_data) return;
//                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
//                file_data.onReceiveValue(result);
//                file_data = null;
//            }
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
//
//    public boolean file_permission() {
//        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ) {
//
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.CAMERA}, 1);
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    private File create_image() throws IOException {
//        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "img_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(imageFileName, ".jpg", storageDir);
//    }
//
//    private File create_video() throws IOException {
//        @SuppressLint("SimpleDateFormat")
//        String file_name = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
//        String new_name = "file_" + file_name + "_";
//        File sd_directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(new_name, ".3gp", sd_directory);
//    }


}
