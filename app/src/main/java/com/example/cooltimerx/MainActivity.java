package com.example.cooltimerx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    private boolean isTimerOn;
    private Button button;
    private CountDownTimer countDownTimer;
    private  int defaultInterval;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600);
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);
        button = findViewById(R.id.button);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                long progresss = progress * 1000;
                updateTimer(progresss);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    private void updateTimer(long progresss){
        int minutes = (int) progresss / 1000 / 60;
        int seconds = (int) progresss / 1000 - (minutes * 60);

        String minutesString = "";
        String secondsString = "";

        if(minutes < 10){
            minutesString = "0" + minutes;
        }else{
            minutesString = String.valueOf(minutes);
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = String.valueOf(seconds);
        }
        textView.setText(minutesString + ":" + secondsString);
    }
    private void resetTimer(){
        countDownTimer.cancel();
        button.setText("Start");
        seekBar.setEnabled(true);
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);
    }

    public void start(View view) {
        if(!isTimerOn){
            button.setText("Stop");
            seekBar.setEnabled(false);
            isTimerOn = true;

            countDownTimer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) {
                @Override
                public void onTick(long progresss) {
                    updateTimer(progresss);
                }

                @Override
                public void onFinish() {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if(sharedPreferences.getBoolean("enable_sound", true)){

                        String melodyName = sharedPreferences.getString("timer_melody","bell");
                        if(melodyName.equals("bell")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bombdef);
                            mediaPlayer.start();
                        }else if(melodyName.equals("explor")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.explor);
                            mediaPlayer.start();
                        }else if(melodyName.equals("beep")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
                            mediaPlayer.start();
                        }

                    }

                    resetTimer();
                }
            };
            countDownTimer.start();
        } else {
            resetTimer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;

        }
        else if(id == R.id.action_about){
            Intent openAbout = new Intent(this, AboutActivity.class);
            startActivity(openAbout);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void  setIntervalFromSharedPreferences(SharedPreferences sharedPreferences){
        defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval", "30"));
        long defaultIntervalInMillis = defaultInterval * 1000;
        updateTimer(defaultInterval);
        seekBar.setProgress(defaultInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if(key.equals("default_interval")){
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}