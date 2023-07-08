package com.example.quitsmokingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AdView adView;
    private TextView timerTextView;
    private TextView timer;
    private TextView pressButtonText;
    private Button startTimerBtn;
    private Button stopTimerBtn;
    private boolean isRunning;
    private long startTime;
    private int seconds;
    private SharedPreferences timerPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerPreferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        isRunning = timerPreferences.getBoolean("isRunning", false);
        startTime = timerPreferences.getLong("startTime", 0);

        timerTextView = findViewById(R.id.timerText);

        pressButtonText = findViewById(R.id.pressButtonText);

        timer = findViewById(R.id.timer);

        startTimerBtn = findViewById(R.id.startTimerBtn);

        stopTimerBtn = findViewById(R.id.stopTimerBtn);

        stopTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.confirm_stop_timer)
                        .setTitle(R.string.confirm_stop_timer_title)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                stopTimer();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        startTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
            }
        });

        timerIsNotRunning();

        // Restore the timer state
        if (isRunning) {
            timerIsRunning();
            long elapsedTime = System.currentTimeMillis() - startTime;
            seconds = (int) (elapsedTime / 1000);
            runTimer();
        }

        // Initialize ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Toast.makeText(MainActivity.this, "Initialization completed.", Toast.LENGTH_LONG).show();
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void timerIsRunning() {
        startTimerBtn.setVisibility(View.INVISIBLE);
        stopTimerBtn.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);
        pressButtonText.setVisibility(View.INVISIBLE);
    }

    private void timerIsNotRunning() {
        startTimerBtn.setVisibility(View.VISIBLE);
        stopTimerBtn.setVisibility(View.INVISIBLE);
        timerTextView.setVisibility(View.INVISIBLE);
        timer.setVisibility(View.INVISIBLE);
        pressButtonText.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRunning) {
            stopTimer();
        }
    }

    private void startTimer() {
        isRunning = true;
        startTime = System.currentTimeMillis();
        timerIsRunning();
        runTimer();
        saveTimerState();
    }

    private void stopTimer() {
        isRunning = false;
        timerIsNotRunning();
        saveTimerState();
    }

    private void saveTimerState() {
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putBoolean("isRunning", isRunning);
        editor.putLong("startTime", startTime);
        editor.apply();
    }

    private void runTimer() {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedTime / 1000);

                int days = seconds / 86400;
                int hours = (seconds % 86400) / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", days, hours, minutes, secs);
                timer.setText(time);

                if (isRunning) {
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }
}

