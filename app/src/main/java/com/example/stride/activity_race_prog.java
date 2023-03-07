package com.example.stride;

import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.os.Build;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Calendar;

public class activity_race_prog extends AppCompatActivity {
    TimePicker picker;
    TimePicker picker2;
    Button btnGet;
    TextView tvw;
    TextView tvw2;
    String newligne=System.getProperty("line.separator");
    int hour, minute, hour2, minute2;
    String am_pm, am_pm2;

    private DatePicker datePicker;
    private EditText editTextDate;
    private Button buttonDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_prog);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month  = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        tvw=(TextView)findViewById(R.id.textView2);
        picker=(TimePicker)findViewById(R.id.timePicker1);
        picker2=(TimePicker)findViewById(R.id.timePicker2);
        picker.setIs24HourView(false);
        btnGet=(Button)findViewById(R.id.button2);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23 ){
                    hour = picker.getHour();
                    minute = picker.getMinute();
                    hour2 = picker2.getHour();
                    minute2 = picker2.getMinute();
                }
                else{
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                    hour2 = picker2.getCurrentHour();
                    minute2 = picker2.getCurrentMinute();
                }
                if(hour > 12) {
                    am_pm = "PM";
                    hour = hour - 12;
                }
                else
                {
                    am_pm="AM";
                }
                if(hour2 > 12) {
                    am_pm2 = "PM";
                    hour2 = hour2 - 12;
                }
                else
                {
                    am_pm2="AM";
                }

                //tvw.setText("The :"+day+"/"+ (month+1)+newligne+"Start time: "+ hour +":"+ minute+" "+am_pm+newligne+"End time: "+ hour2 +":"+ minute2+" "+am_pm2);
                //tvw.setText("Start time: "+ hour +":"+ minute+" "+am_pm+newligne+"End time: "+ hour2 +":"+ minute2+" "+am_pm2);
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
                showDate();
            }
        });
    }

    private void datePickerChange(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
        this.editTextDate.setText(dayOfMonth +"-" + (month + 1) + "-" + year);
    }

    public void showDate()  {
        int year = this.datePicker.getYear();
        int month = this.datePicker.getMonth(); // 0 - 11
        int day = this.datePicker.getDayOfMonth();

        Toast.makeText(this, "Date: " + day+"-"+ (month + 1) +"-"+ year+newligne+
                "At: "+hour+":"+minute+am_pm+newligne+
                "Until: "+hour2+":"+minute2+am_pm2,
                Toast.LENGTH_LONG).show();
    }
}