package com.example.quitsmokingapp;

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

    private AdView adView;
    private TextView timerTextView;
    private TextView timer;
    private TextView txtOverallProgress;
    private TextView txtMoneySaved;
    private ImageView moneySavedIcon;
    private TextView moneySaved;
    private double moneySavedValue;
    private TextView txtDaysQuit;
    private ImageView daysQuitIcon;
    private TextView daysQuit;
    private int daysQuitValue;
    private TextView txtAvoided;
    private ImageView cigarettesAvoidedIcon;
    private TextView cigarettesAvoided;
    private double cigarettesAvoidedValue;
    private int cigarettesPerDay;
    private float costPerCigarettePack;
    private TextView pressButtonText;
    private Button startTimerBtn;
    private Button stopTimerBtn;
    private boolean isRunning;
    private long startTime;
    private int seconds;
    private SharedPreferences timerPreferences;
    private SharedPreferences dataPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerPreferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        dataPreferences = getSharedPreferences("DataPrefs", MODE_PRIVATE);

        cigarettesPerDay = dataPreferences.getInt("cigarettesPerDay", 0);
        costPerCigarettePack = dataPreferences.getFloat("costPerCigarettePack", 0);

        isRunning = timerPreferences.getBoolean("isRunning", false);
        startTime = timerPreferences.getLong("startTime", 0);

        timerTextView = findViewById(R.id.timerText);

        txtOverallProgress = findViewById(R.id.txtOverallProgress);

        txtDaysQuit = findViewById(R.id.txtDaysQuit);
        daysQuitIcon = findViewById(R.id.daysQuitIcon);
        daysQuit = findViewById(R.id.daysQuit);

        txtAvoided = findViewById(R.id.txtAvoided);
        cigarettesAvoidedIcon = findViewById(R.id.cigarettesAvoidedIcon);
        cigarettesAvoided = findViewById(R.id.cigarettesAvoided);

        txtMoneySaved = findViewById(R.id.txtMoneySaved);
        moneySavedIcon = findViewById(R.id.moneySavedIcon);
        moneySaved = findViewById(R.id.moneySaved);

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
        }else{
            showCustomDialog();
        }

        // Initialize ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                //Toast.makeText(MainActivity.this, "Initialization completed.", Toast.LENGTH_LONG).show();
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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

    private void timerIsRunning() {
        startTimerBtn.setVisibility(View.INVISIBLE);
        stopTimerBtn.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);
        pressButtonText.setVisibility(View.INVISIBLE);
        txtOverallProgress.setVisibility(View.VISIBLE);
        txtMoneySaved.setVisibility(View.VISIBLE);
        moneySavedIcon.setVisibility(View.VISIBLE);
        moneySaved.setVisibility(View.VISIBLE);
        txtAvoided.setVisibility(View.VISIBLE);
        cigarettesAvoidedIcon.setVisibility(View.VISIBLE);
        cigarettesAvoided.setVisibility(View.VISIBLE);
        txtDaysQuit.setVisibility(View.VISIBLE);
        daysQuitIcon.setVisibility(View.VISIBLE);
        daysQuit.setVisibility(View.VISIBLE);
    }

    private void timerIsNotRunning() {
        startTimerBtn.setVisibility(View.VISIBLE);
        stopTimerBtn.setVisibility(View.INVISIBLE);
        timerTextView.setVisibility(View.INVISIBLE);
        timer.setVisibility(View.INVISIBLE);
        pressButtonText.setVisibility(View.VISIBLE);
        txtOverallProgress.setVisibility(View.INVISIBLE);
        txtMoneySaved.setVisibility(View.INVISIBLE);
        moneySavedIcon.setVisibility(View.INVISIBLE);
        moneySaved.setVisibility(View.INVISIBLE);
        txtAvoided.setVisibility(View.INVISIBLE);
        cigarettesAvoidedIcon.setVisibility(View.INVISIBLE);
        cigarettesAvoided.setVisibility(View.INVISIBLE);
        txtDaysQuit.setVisibility(View.INVISIBLE);
        daysQuitIcon.setVisibility(View.INVISIBLE);
        daysQuit.setVisibility(View.INVISIBLE);
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
        saveDataValues();
    }

    private void stopTimer() {
        isRunning = false;
        timerIsNotRunning();
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
                int seconds = (int) (elapsedTime / 1000);

                int days = seconds / 86400;
                int hours = (seconds % 86400) / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", days, hours, minutes, secs);
                timer.setText(time);

                // Overall Progress
                daysQuitValue = days;
                cigarettesAvoidedValue = (cigarettesPerDay/24.0f) * (seconds/3600.0f);
                moneySavedValue = cigarettesAvoidedValue * (costPerCigarettePack/20.0);

                String daysQuitValueString = String.valueOf(daysQuitValue);
                String cigarettesAvoidedValueString = String.format(Locale.getDefault(), "%.0f", cigarettesAvoidedValue);
                String moneySavedValueString = String.format(Locale.getDefault(), "₺%.2f", moneySavedValue);

                daysQuit.setText(daysQuitValueString);
                cigarettesAvoided.setText(cigarettesAvoidedValueString);
                moneySaved.setText(moneySavedValueString);

                if (isRunning) {
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }
}

