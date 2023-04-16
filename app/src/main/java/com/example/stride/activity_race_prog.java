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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class activity_race_prog extends AppCompatActivity {
    TimePicker picker;
    TimePicker picker2;
    public FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private User us;
    //String newline =System.getProperty("line.separator");
    int hour, minute, hour2, minute2;
    int pm_am, pm_am2;
    private static final int STORAGE_PERMISSION_CODE = 101;

    //Button btnGet;
    public DatePicker datePicker;
    private EditText editTextDate;

    EditText title;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://stride-99148-default-rtdb.europe-west1.firebasedatabase.app");


        //CHECK THE CALENDAR PERMISSION
        checkPermission("android.permission.WRITE_CALENDAR", STORAGE_PERMISSION_CODE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_prog);

        title = findViewById(R.id.editText_date);

        //CALENDAR INIT
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month  = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //TIME PICKERS INIT
        picker=findViewById(R.id.timePicker1);
        picker2=findViewById(R.id.timePicker2);
        picker.setIs24HourView(false); //false = 12h format / true = 24h format
        this.datePicker = this.findViewById(R.id.datePicker);
        this.datePicker.init( year, month , day , this::datePickerChange);

/*
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

        });*/



        //FIELD OF THE DAY INIT
        this.editTextDate =  this.findViewById(R.id.editText_date);
        Button buttonDate = this.findViewById(R.id.button_date);

        buttonDate.setOnClickListener(v -> {

            //CHOOSE THE HOURS
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

            //CHOOSE THE DAY
            int year1 = datePicker.getYear();
            int month1 = datePicker.getMonth(); // 0 = jan / 11=dec
            int day1 = datePicker.getDayOfMonth();
            //showDate(year1, month1, day1);

            //ADD A EVENT IN THE CALENDAR
            if(!title.getText().toString().isEmpty())
            {
                AddCalendarEvent(year1, month1, day1, hour, hour2, minute);

                //Come back to the main screen
                Button button_date = this.findViewById(R.id.button_date);
                button_date.setOnClickListener(view -> {
                    Intent i = new Intent(this, MainScreenActivity.class);
                    startActivity(i);
                });

                //add to firebase
                reference = database.getReference().child("Users").child(user.getUid());
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        us = (User)dataSnapshot.getValue(User.class);
                        LocalDateTime test = LocalDateTime.of(year1, month1+1, day1, hour, minute);
                        us.AddRun(test.toString());
                        reference.setValue(us);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                };
                reference.addListenerForSingleValueEvent(postListener);


            }

            else
            {
                Toast.makeText(activity_race_prog.this, "Please fill all the fields",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void AddCalendarEvent( int y, int m, int d, int h, int h2, int min) {
        //SET THE CALENDAR WITH THE HOURS AND DAY CHOSEN
        Calendar calendarEvent = Calendar.getInstance();
        System.out.println("first values: " + calendarEvent.getTime());
        calendarEvent.set(Calendar.MONTH, m);
        calendarEvent.set(Calendar.YEAR, y);
        calendarEvent.set(Calendar.DAY_OF_MONTH, d);
        calendarEvent.set(Calendar.HOUR, h);
        calendarEvent.set(Calendar.MINUTE, min);

        //SET THE EVENT
        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setType("vnd.android.cursor.item/event");
        i.putExtra("beginTime", calendarEvent.getTimeInMillis());
        //i.putExtra("rule", "FREQ=WEEKLY");
        i.putExtra("endTime", calendarEvent.getTimeInMillis() + (long)60 * 60*(h2-h) * 1000);
        i.putExtra("title", "Run");
        try {
            //OPEN GOOGLE CALENDAR
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
/*
    public void showDate(int year, int month , int day)  {
        Toast.makeText(this, "Date: " + day+"-"+ (month+1) +"-"+ year+ newline +
                        "At: "+hour+":"+minute+pm_am+ newline +
                        "Until: "+hour2+":"+minute2+pm_am,
                Toast.LENGTH_LONG).show();
    }*/

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
