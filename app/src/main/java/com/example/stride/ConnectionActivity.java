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

        sharedPreferences = getSharedPreferences("REMEMBER", MODE_PRIVATE);
    }

    public void setRememberMe(boolean b) {
        sharedPreferences.edit().putBoolean("Remember", b).commit();
    }

    public boolean getRememberMe() {
        return sharedPreferences.getBoolean("Remember", false);
    }
}
