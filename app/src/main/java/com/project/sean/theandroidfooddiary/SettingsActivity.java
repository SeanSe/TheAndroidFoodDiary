package com.project.sean.theandroidfooddiary;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.project.sean.theandroidfooddiary.Database.BackupData;
import com.project.sean.theandroidfooddiary.Notification.NotificationPublisher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, BackupData.OnBackupListener {

    EditText editTime;

    int clockHour;
    int clockMinute;

    long currentMilli = 0;
    long userSetTime = 0;

    private Context context;

    private BackupData backupData;

    boolean permResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        editTime = (EditText) findViewById(R.id.editTime);
        editTime.setOnClickListener(this);

        context = this;

        backupData = new BackupData(context);
        backupData.setOnBackupListener(this);

        Button buttonNoteSet = (Button) findViewById(R.id.buttonNoteSet);
        buttonNoteSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMilli != 0 && userSetTime != 0) {
                    setNotification();
                    Toast.makeText(SettingsActivity.this, "Notification set!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Please enter a suitable time!", Toast.LENGTH_LONG).show();
                }

            }
        });

        Button buttonExportSD = (Button) findViewById(R.id.buttonExportSD);
        buttonExportSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupData.exportToSD();
            }
        });

        Button buttonImportSD = (Button) findViewById(R.id.buttonImportSD);
        buttonImportSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupData.importToSD();
            }
        });

        permResult = isStoragePermissionGranted();
        if(permResult) {
            Toast.makeText(this, "External Storage access granted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "External Storage access denied!", Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleNotification(Notification notification, long delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Food Diary");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editTime: {
                addTime();
                break;
            }
        }
    }

    public void addTime() {
        final Calendar mcurrentTime = Calendar.getInstance();
        int currentHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = mcurrentTime.get(Calendar.MINUTE);
        mcurrentTime.clear();
        mcurrentTime.set(Calendar.HOUR_OF_DAY, currentHour);
        mcurrentTime.set(Calendar.MINUTE, currentMinute);
        currentMilli = mcurrentTime.getTimeInMillis();
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                //Displays in the EditText the time selected by user.
                mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String selectHourMinute = timeFormat.format(mcurrentTime.getTime());
                editTime.setText(selectHourMinute);

                //Variables used to store in the database.
                clockHour = selectedHour;
                clockMinute = selectedMinute;

                Log.d("Current Millis:", String.valueOf(mcurrentTime.getTimeInMillis()));
                Log.d("Current Millis:", String.valueOf(currentMilli));

                userSetTime = mcurrentTime.getTimeInMillis();
                Toast.makeText(SettingsActivity.this, "Timeset to " + selectHourMinute + "!", Toast.LENGTH_LONG).show();
            }
        }, currentHour, currentMinute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void setNotification() {
        long difference = userSetTime - currentMilli;
        Log.d("Current Millis:", String.valueOf(difference));
        scheduleNotification(getNotification("Enter into your food diary!"), difference);
    }

    @Override
    public void onFinishExport(String error) {
        String notify = error;
        if (error == null) {
            notify = "Export success";
        }
        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishImport(String error) {
        String notify = error;
        if (error == null) {
            notify = "Import success";
        }
        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
}