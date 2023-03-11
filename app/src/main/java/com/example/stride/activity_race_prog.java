package com.example.stride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;


import android.content.Context;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import java.util.TimeZone;


import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class activity_race_prog<MainActivity> extends AppCompatActivity {
    TimePicker picker;
    TimePicker picker2;
    Button btnGet;
    TextView tvw;
    TextView tvw2;
    String newline =System.getProperty("line.separator");
    int hour, minute, hour2, minute2;
    int pm_am, pm_am2;
    private static final int STORAGE_PERMISSION_CODE = 101;

    public DatePicker datePicker;
    private EditText editTextDate;
    private Button buttonDate;

    EditText title;
    public int year;
    public int month;
    public int day;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        checkPermission("android.permission.WRITE_CALENDAR", STORAGE_PERMISSION_CODE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_prog);

        title = findViewById(R.id.editText_date);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month  = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        //tvw=(TextView)findViewById(R.id.textView2);
        picker=(TimePicker)findViewById(R.id.timePicker1);
        picker2=(TimePicker)findViewById(R.id.timePicker2);
        picker.setIs24HourView(false);
        btnGet=(Button)findViewById(R.id.button2);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    hour = picker.getHour();
                    minute = picker.getMinute();
                    hour2 = picker2.getHour();
                    minute2 = picker2.getMinute();
                } else {
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                    hour2 = picker2.getCurrentHour();
                    minute2 = picker2.getCurrentMinute();
                }
                if (hour > 12) {
                    pm_am = 0;
                    hour = hour - 12;
                } else {
                    pm_am = 1;
                }
                if (hour2 > 12) {
                    pm_am2 = 0;
                    hour2 = hour2 - 12;
                } else {
                    pm_am2 = 1;
                }

                Toast.makeText(activity_race_prog.this, "The :"+day+"/"+ (month+1)+ newline +"Start time: "+ hour +":"+ minute+" "+pm_am+ newline +"End time: "+ hour2 +":"+ minute2+" "+pm_am2, Toast.LENGTH_SHORT).show();

            }
        });

        this.editTextDate = (EditText) this.findViewById(R.id.editText_date);
        this.buttonDate = (Button) this.findViewById(R.id.button_date);
        this.datePicker = (DatePicker) this.findViewById(R.id.datePicker);

        this.datePicker.init( year, month , day , new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                datePickerChange(  datePicker,   year,   month,   day);
            }
        });


        this.buttonDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    hour = picker.getHour();
                    minute = picker.getMinute();
                    hour2 = picker2.getHour();
                    minute2 = picker2.getMinute();
                } else {
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                    hour2 = picker2.getCurrentHour();
                    minute2 = picker2.getCurrentMinute();
                }
                if (hour > 12) {
                    pm_am = 0;
                    hour = hour - 12;
                } else {
                    pm_am = 1;
                }
                if (hour2 > 12) {
                    pm_am2 = 0;
                    hour2 = hour2 - 12;
                } else {
                    pm_am2 = 1;
                }


                int year = datePicker.getYear();
                int month = datePicker.getMonth(); // 0 - 11
                int day = datePicker.getDayOfMonth();
                showDate(year, month, day);

                Intent intent;
                if(!title.getText().toString().isEmpty())
                {
                    AddCalendarEvent(v, year, month, day, hour, hour2);
                }

                else
                {
                    Toast.makeText(activity_race_prog.this, "Please fill all the fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void AddCalendarEvent(View view, int y, int m, int d, int h, int h2) {
        Calendar calendarEvent = Calendar.getInstance();
        System.out.println("first values: " + calendarEvent.getTime());
        calendarEvent.set(Calendar.MONTH, m);
        calendarEvent.set(Calendar.YEAR, y);
        calendarEvent.set(Calendar.DAY_OF_MONTH, d);
        calendarEvent.set(Calendar.HOUR, h);

        System.out.println("calendarEvent values: " + calendarEvent.getTime());

        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setType("vnd.android.cursor.item/event");
        i.putExtra("beginTime", calendarEvent.getTimeInMillis());
        i.putExtra("rule", "FREQ=YEARLY");
        i.putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60*(h2-h) * 1000);
        i.putExtra("title", "Run");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity_race_prog.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
        }
    }

    private void datePickerChange(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
        this.editTextDate.setText(dayOfMonth +"-" + (month + 1) + "-" + year);
    }

    public void showDate(int year, int month , int day)  {
        /*int year = this.datePicker.getYear();
        int month = this.datePicker.getMonth(); // 0 - 11
        int day = this.datePicker.getDayOfMonth();*/

        Toast.makeText(this, "Date: " + day+"-"+ (month+1) +"-"+ year+ newline +
                "At: "+hour+":"+minute+pm_am+ newline +
                "Until: "+hour2+":"+minute2+pm_am,
                Toast.LENGTH_LONG).show();
    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(activity_race_prog.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(activity_race_prog.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(activity_race_prog.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity_race_prog.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity_race_prog.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
