package com.example.stride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class activity_race_prog extends AppCompatActivity {
    TimePicker picker;
    TimePicker picker2;
    Button btnGet;
    String newline =System.getProperty("line.separator");
    int hour, minute, hour2, minute2;
    int pm_am, pm_am2;
    private static final int STORAGE_PERMISSION_CODE = 101;

    public DatePicker datePicker;
    private EditText editTextDate;

    EditText title;
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
        picker=findViewById(R.id.timePicker1);
        picker2=findViewById(R.id.timePicker2);
        picker.setIs24HourView(false);
        btnGet=findViewById(R.id.button2);

        btnGet.setOnClickListener(v -> {

            hour = picker.getHour();
            minute = picker.getMinute();
            hour2 = picker2.getHour();
            minute2 = picker2.getMinute();

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

        });

        this.editTextDate =  this.findViewById(R.id.editText_date);
        Button buttonDate = this.findViewById(R.id.button_date);
        this.datePicker = this.findViewById(R.id.datePicker);

        this.datePicker.init( year, month , day , this::datePickerChange);


        buttonDate.setOnClickListener(v -> {


            hour = picker.getHour();
            minute = picker.getMinute();
            hour2 = picker2.getHour();
            minute2 = picker2.getMinute();

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


            int year1 = datePicker.getYear();
            int month1 = datePicker.getMonth(); // 0 - 11
            int day1 = datePicker.getDayOfMonth();
            showDate(year1, month1, day1);

            if(!title.getText().toString().isEmpty())
            {
                AddCalendarEvent(year1, month1, day1, hour, hour2, minute);
            }

            else
            {
                Toast.makeText(activity_race_prog.this, "Please fill all the fields",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void AddCalendarEvent( int y, int m, int d, int h, int h2, int min) {
        Calendar calendarEvent = Calendar.getInstance();
        System.out.println("first values: " + calendarEvent.getTime());
        calendarEvent.set(Calendar.MONTH, m);
        calendarEvent.set(Calendar.YEAR, y);
        calendarEvent.set(Calendar.DAY_OF_MONTH, d);
        calendarEvent.set(Calendar.HOUR, h);
        calendarEvent.set(Calendar.MINUTE, min);


        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setType("vnd.android.cursor.item/event");
        i.putExtra("beginTime", calendarEvent.getTimeInMillis());
        //i.putExtra("rule", "FREQ=WEEKLY");
        i.putExtra("endTime", calendarEvent.getTimeInMillis() + (long)60 * 60*(h2-h) * 1000);
        i.putExtra("title", "Run");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity_race_prog.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void datePickerChange(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
        this.editTextDate.setText(dayOfMonth +"-" + (month + 1) + "-" + year);
    }

    public void showDate(int year, int month , int day)  {
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
