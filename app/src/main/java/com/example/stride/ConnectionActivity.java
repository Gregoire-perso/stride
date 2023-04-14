package com.example.stride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class ConnectionActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        sharedPreferences = getSharedPreferences("TEST", MODE_PRIVATE);
    }

    public void setRememberMe(boolean b) {
        Log.e("LOLILOL SET", String.valueOf(b));
        sharedPreferences.edit().putBoolean("Remember", b).commit();
    }

    public boolean getRememberMe() {
        Log.e("LOLILOL GET", String.valueOf(sharedPreferences.getBoolean("Remember", false)));
        Log.e("LOLILOL GET", String.valueOf(sharedPreferences.getBoolean("Remember", true)));
        return sharedPreferences.getBoolean("Remember", false);
    }
}