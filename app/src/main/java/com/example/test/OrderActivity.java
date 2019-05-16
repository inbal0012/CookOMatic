package com.example.test;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "OrderActivity";

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime, txtAlarm, txtTimer;
    ImageButton btnAddEvent, btnAddAlarm, btnAddTimer;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mTodayStr;

    //REQUESTS
    final int ALARM_REQUEST = 1;
    final int TIMER_REQUEST = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        btnDatePicker = findViewById(R.id.btn_date);
        btnTimePicker = findViewById(R.id.btn_time);
        txtDate = findViewById(R.id.in_date);
        txtTime = findViewById(R.id.in_time);
        txtAlarm = findViewById(R.id.time_for_alarm);
        txtTimer = findViewById(R.id.time_for_timer);
        btnAddEvent = findViewById(R.id.save_to_calendar);
        btnAddAlarm = findViewById(R.id.alarmBtn);
        btnAddTimer = findViewById(R.id.timerBtn);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnAddEvent.setOnClickListener(this);
        btnAddAlarm.setOnClickListener(this);
        btnAddTimer.setOnClickListener(this);

        TextView name_tv = findViewById(R.id.name_output);

        //name handler
        //activity main -> order
        String nameStr = getIntent().getStringExtra("name");
        name_tv.setText("Hello " + nameStr);

    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {
            onClickDate(v);
        }
        if (v == btnTimePicker) {
            onClickTime(v);
        }
        if (v == btnAddEvent) {
            addEventWrapper();
        }
        if (v == btnAddAlarm) {
            runTimePermission(v);
            //runTimePermission_temp(Manifest.permission.SET_ALARM, ALARM_REQUEST, addAlarm());
        }
        if (v == btnAddTimer) {
            Log.d(TAG, "onClick: btnAddTimer");
            runTimePermission(v);
            //runTimePermission_temp(Manifest.permission.SET_TIME, TIMER_REQUEST, startTimerWrapper());
        }
    }

    //TODO runTimePermission callback void(void)
    private void runTimePermission(View v) {

        if (v == btnAddAlarm) {
            Log.d(TAG, "runTimePermission: btnAddAlarm");
            addAlarm();
            if (Build.VERSION.SDK_INT >= 23) {
                int hasPermission = checkSelfPermission(Manifest.permission.SET_ALARM);
                Log.d(TAG, "runTimePermission: VERSION >= 23" + " hasPermission " + hasPermission);
                if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                    addAlarm();
                } else {  //PERMISSION_DENIED
                    requestPermissions(new String[]{Manifest.permission.SET_ALARM}, ALARM_REQUEST);
                }
            } else {
                addAlarm();
            }
        }

        if (v == btnAddTimer) {
            Log.d(TAG, "runTimePermission: btnAddTimer");
            startTimerWrapper();
            //TODO fix requestPermissions
            if (Build.VERSION.SDK_INT >= 23) {
                int hasPermission = checkSelfPermission(Manifest.permission.SET_TIME);
                Log.d(TAG, "runTimePermission: VERSION >= 23 " + " hasPermission " + hasPermission);
                if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                    startTimerWrapper();
                } else {  //PERMISSION_DENIED
                    requestPermissions(new String[]{Manifest.permission.SET_TIME}, TIMER_REQUEST);
                }
            } else {
                startTimerWrapper();
            }

        }
    }

    //
    //interface addListener {
    //void addEventWrapper();
    //
    //}

    //callback
    public void runTimePermission_temp(String i_permission, int i_REQUEST, Runnable i_callback) {
        String permission;
        if (i_permission.contains("Manifest.permission.")) {         // dumb developer handler
            permission = i_permission;
        } else {
            permission = "Manifest.permission." + i_permission;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            int hasPermission = checkSelfPermission(permission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                i_callback.run();
            } else {  //PERMISSION_DENIED
                requestPermissions(new String[]{permission}, i_REQUEST);
            }
        } else {
            i_callback.run();
        }
    }

    private void onClickDate(View v) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mTodayStr = mDay + "/" + (mMonth + 1) + "/" + mYear;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        try {
                            if (checkDateGreaterThanToday(date)) {
                                txtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void onClickTime(View v) {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        txtTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private Boolean checkDateGreaterThanToday(String date) throws ParseException {
        //dumb user handler
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dateD = sdf.parse(date);
        Date todayD = sdf.parse(mTodayStr);

        Log.d("local", "checkDateGreaterThanToday: date: " + date + " mTodayStr: " + mTodayStr);

        if (dateD.before(todayD)) {
            Toast.makeText(this, "can't add a task to the past", Toast.LENGTH_SHORT).show();
            Log.d("local", "checkDateGreaterThanToday: past date - dumb user handler");
            return false;
        } else {
            return true;
        }
    }

    private void addEventWrapper() {
        //String dateTime = txtDate.getText().toString() + txtTime.getText().toString();
        //Log.d(TAG, "onClick: " + dateTime);
        try {

            Long millisDate = new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText().toString()).getTime();
            Long millisTime = new SimpleDateFormat("hh:mm").parse(txtTime.getText().toString()).getTime() + 3600000 * 2; //TODO 2 hours
            Log.d(TAG, "onClick: Date = " + txtDate.getText() + " Time = " + txtTime.getText());
            Log.d(TAG, "onClick: millisDate = " + millisDate + " millisTime = " + millisTime);
            //Log.d(TAG, "onClick: " + txtDate.getText());
            Long millis = millisDate + millisTime;
            addEvent("recipe by Cook o Matric", null, millis, millis + 3600000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void addEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void addAlarm() {
        //TODO timr based on event def

        String inputTimeStr = txtAlarm.getText().toString();
        if (inputTimeStr.length() == 0) {            //dumb user handler: empty title
            Toast.makeText(OrderActivity.this, "Please enter time in minuets", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "addAlarm: dumb user handler: empty alarm value");
            return;
        }

        int inputTime = Integer.parseInt(txtAlarm.getText().toString());
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Cook o Matric pickup time");
        int hours = inputTime / 60;
        int mins = inputTime % 60;
        intent.putExtra(AlarmClock.EXTRA_HOUR, hours);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, mins);
        startActivity(intent);
    }

    private void startTimerWrapper() {
        Log.d(TAG, "startTimerWrapper: ");
        String inputTimeStr = txtTimer.getText().toString();
        if (inputTimeStr.length() == 0) {            //dumb user handler: empty title
            Toast.makeText(OrderActivity.this, "Please enter time in minuets", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "startTimerWrapper: dumb user handler: empty timer value");
            return;
        }

        String message = "Cook o Matric timer";
        int seconds = Integer.parseInt(inputTimeStr) * 60;
        Log.d(TAG, "startTimerWrapper: startTimer(" + message + "," + seconds + ")");
        startTimer(message, seconds);
    }
    private void startTimer(String message, int seconds) {
        Log.d(TAG, "startTimer: ");
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            Toast.makeText(this, "Timer started for " + (seconds/60) + "min", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "startTimer: Timer started. time: " + (seconds/60) + "min (seconds: " + seconds + ")");
            startActivity(intent);
        }
    }

}
