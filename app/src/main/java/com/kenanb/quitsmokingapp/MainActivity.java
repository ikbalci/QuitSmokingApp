package com.kenanb.quitsmokingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private enum TimerState {
        RUNNING, NOT_RUNNING
    }

    private AdView adView;
    private TextView timerTextView, timer, txtMoneySaved, moneySaved, txtDaysQuit, daysQuit, txtAvoided, cigarettesAvoided, pressButtonText;
    private ImageView moneySavedIcon, daysQuitIcon, cigarettesAvoidedIcon;
    private Button startTimerBtn, stopTimerBtn;

    private double moneySavedValue, cigarettesAvoidedValue;
    private int daysQuitValue, cigarettesPerDay, seconds;
    private float costPerCigarettePack;
    private boolean isRunning;
    private long startTime;

    private SharedPreferences timerPreferences, dataPreferences;
    private TimerState timerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadPreferences();

        if (isRunning) {
            resumeTimer();
        } else {
            showCustomDialog();
        }

        setupAds();
        setupListeners();
    }

    private void initViews() {
        timerPreferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        dataPreferences = getSharedPreferences("DataPrefs", MODE_PRIVATE);

        timerTextView = findViewById(R.id.timerText);
        timer = findViewById(R.id.timer);
        txtMoneySaved = findViewById(R.id.txtMoneySaved);
        moneySavedIcon = findViewById(R.id.moneySavedIcon);
        moneySaved = findViewById(R.id.moneySaved);
        txtDaysQuit = findViewById(R.id.txtDaysQuit);
        daysQuitIcon = findViewById(R.id.daysQuitIcon);
        daysQuit = findViewById(R.id.daysQuit);
        txtAvoided = findViewById(R.id.txtAvoided);
        cigarettesAvoidedIcon = findViewById(R.id.cigarettesAvoidedIcon);
        cigarettesAvoided = findViewById(R.id.cigarettesAvoided);
        pressButtonText = findViewById(R.id.pressButtonText);
        startTimerBtn = findViewById(R.id.startTimerBtn);
        stopTimerBtn = findViewById(R.id.stopTimerBtn);
    }

    private void loadPreferences() {
        cigarettesPerDay = dataPreferences.getInt("cigarettesPerDay", 0);
        costPerCigarettePack = dataPreferences.getFloat("costPerCigarettePack", 0);
        isRunning = timerPreferences.getBoolean("isRunning", false);
        startTime = timerPreferences.getLong("startTime", 0);

        timerState = isRunning ? TimerState.RUNNING : TimerState.NOT_RUNNING;
        updateUI();
    }

    private void setupAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                // Initialization completed.
            }
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setupListeners() {
        startTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
            }
        });

        stopTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStopTimerConfirmation();
            }
        });
    }

    private void showStopTimerConfirmation() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(R.string.confirm_stop_timer)
                .setTitle(R.string.confirm_stop_timer_title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopTimer();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        final EditText cigarettesPerDayInput = dialog.findViewById(R.id.edtxCigarettesPerDay);
        final EditText costPerCigarettePackInput = dialog.findViewById(R.id.edtxCostPerPack);
        Button btnDialog = dialog.findViewById(R.id.btnDialog);

        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cigarettesPerDayInput.getText().toString().isEmpty() || costPerCigarettePackInput.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    cigarettesPerDay = Integer.parseInt(cigarettesPerDayInput.getText().toString());
                    costPerCigarettePack = Float.parseFloat(costPerCigarettePackInput.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void updateUI() {
        boolean isRunning = timerState == TimerState.RUNNING;

        startTimerBtn.setVisibility(isRunning ? View.INVISIBLE : View.VISIBLE);
        stopTimerBtn.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        timerTextView.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        timer.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        pressButtonText.setVisibility(isRunning ? View.INVISIBLE : View.VISIBLE);
        txtMoneySaved.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        moneySavedIcon.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        moneySaved.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        txtAvoided.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        cigarettesAvoidedIcon.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        cigarettesAvoided.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        txtDaysQuit.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        daysQuitIcon.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        daysQuit.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRunning) {
            stopTimer();
        }
    }

    private void startTimer() {
        timerState = TimerState.RUNNING;
        isRunning = true;
        startTime = System.currentTimeMillis();
        updateUI();
        runTimer();
        saveTimerState();
        saveDataValues();
    }

    private void stopTimer() {
        timerState = TimerState.NOT_RUNNING;
        isRunning = false;
        updateUI();
        saveTimerState();
        saveDataValues();
    }

    private void saveDataValues() {
        SharedPreferences.Editor editor = dataPreferences.edit();
        editor.putInt("cigarettesPerDay", cigarettesPerDay);
        editor.putFloat("costPerCigarettePack", costPerCigarettePack);
        editor.apply();
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
                seconds = (int) (elapsedTime / 1000);

                int days = seconds / 86400;
                int hours = (seconds % 86400) / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                timer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", days, hours, minutes, secs));

                daysQuitValue = days;
                cigarettesAvoidedValue = (cigarettesPerDay / 24.0f) * (seconds / 3600.0f);
                moneySavedValue = cigarettesAvoidedValue * (costPerCigarettePack / 20.0);

                daysQuit.setText(String.valueOf(daysQuitValue));
                cigarettesAvoided.setText(String.format(Locale.getDefault(), "%.0f", cigarettesAvoidedValue));
                moneySaved.setText(String.format(Locale.getDefault(), "₺%.2f", moneySavedValue));

                if (isRunning) {
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void resumeTimer() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        seconds = (int) (elapsedTime / 1000);
        runTimer();
        updateUI();
    }
}