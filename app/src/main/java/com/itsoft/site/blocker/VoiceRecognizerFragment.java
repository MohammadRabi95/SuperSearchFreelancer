package com.itsoft.site.blocker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecognizerFragment extends DialogFragment implements RecognitionListener {
    ImageView imageView;
    TextView speak;
//    Button done;
    int i=0;


    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Context context;
    VoiceRecognizerInterface signal;

    @SuppressLint("ValidFragment")
    public VoiceRecognizerFragment(Context context, VoiceRecognizerInterface signal) {
        this.context = context;
        this.signal = signal;
    }


    public VoiceRecognizerFragment() {

    }



    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_voice_recognizer, container, false);
        {
            imageView=view.findViewById(R.id.image_voice_input);
            speak=view.findViewById(R.id.speak_voice_input_text);
//            done=view.findViewById(R.id.Done);

        }

//        view.setBackground(new ColorDrawable(Color.TRANSPARENT));



        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //Customize language by passing language code
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //To receive partial results on the callback
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,100000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,100000);
        //  mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_RESULTS,true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                context.getPackageName());
        imageView.setOnClickListener(v -> {
//            done.setVisibility(View.VISIBLE);
            startListening();


        });
//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mSpeechRecognizer.stopListening();
//                mSpeechRecognizer.destroy();
//                getActivity().getSupportFragmentManager().beginTransaction().remove(VoiceRecognizerFragment.this).commit();
//                done.setVisibility(View.GONE);
//
//            }
//        });

        return view;

    }
    public void startListening(){
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        changeUIStateToListening();
    }
    public void changeUIStateToListening(){

        speak.setText("Listening...");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null) {

            mSpeechRecognizer.destroy();
        }
    }
    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

        if(i == 7){

            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            startListening();
            //  changeUIStateToRetry();
        }
    }
    public void changeUIStateToRetry(){

        speak.setText("Tap on mic and speak");
    }
    @Override
    public void onResults(Bundle bundle) {

        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(matches == null){

            return;
        }

        int i =0;
        String first ="";

        for(String s : matches){
            if(i==0){
                first =first+s;
            }
            i++;
        }
        signal.spokenText(first);
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer.destroy();
        startListening();
        getActivity().getSupportFragmentManager().beginTransaction().remove(VoiceRecognizerFragment.this).commit();

        /*this.dismiss();*/
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
