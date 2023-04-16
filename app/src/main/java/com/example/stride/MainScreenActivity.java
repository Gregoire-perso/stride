package com.example.stride;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;


public class MainScreenActivity extends AppCompatActivity {

    public FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private User us;
    //String newline =System.getProperty("line.separator");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        ImageButton prof = findViewById(R.id.profileImageButton);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://stride-99148-default-rtdb.europe-west1.firebasedatabase.app");

        reference = database.getReference().child("Users").child(user.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                us = (User)dataSnapshot.getValue(User.class);

                // Display retrieved profile picture
                Picasso.get().load(us.getProfilePictureURI()).into(prof);

                ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");

                boolean pass = false;
                TextView tv14 = (TextView) findViewById(R.id.textView14);
                TextView tv16 = (TextView) findViewById(R.id.textView16);
                TextView tv15 = (TextView) findViewById(R.id.textView15);
                TextView tv17 = (TextView) findViewById(R.id.textView17);
                TextView tv18 = (TextView) findViewById(R.id.textView18);

                for (int i = 0 ; i< us.run.size() ; i++)
                {
                    if (us.run.get(i) == null)
                        continue;

                    long course = LocalDateTime.parse(us.run.get(i).getDate()).atZone(zoneId).toEpochSecond();
                    long mtn = LocalDateTime.now().atZone(zoneId).toEpochSecond();

                    if (course > mtn)
                    {
                        TextView tv9 = (TextView) findViewById(R.id.textView9);
                        long diff = (course - mtn)/60/60;

                        if (diff>= 24 && diff <= 48)
                        {
                            tv9.setText(diff /24 + " day");
                        }
                        else if (diff >48)
                        {
                            tv9.setText(diff /24 + " days");
                        }
                        else {
                            tv9.setText(diff + " hours");
                        }


                        if (i>0) {
                            pass = true;
                            Run before = us.run.get(i - 1);

                            tv14.setText((float)before.distance / 1000 + " km");
                            tv16.setText(before.calories + " cal");
                            tv15.setText(before.hundredthSecs / 100/60 + " min");
                            tv17.setText((float)((before.hundredthSecs /100/60))/((before.distance / 1000)) + " min/km");
                            tv18.setText(before.height + " m");
                        }
                        break;
                    }
                }

                if(us.run.size()==0 || !pass)
                {
                    tv14.setText("0 km");
                    tv16.setText("0 cal");
                    tv15.setText("0 min");
                    tv17.setText("0 min/km");
                    tv18.setText("0 m");
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addListenerForSingleValueEvent(postListener);

        Button planRace = this.findViewById(R.id.button);
        planRace.setOnClickListener(v -> {
                Intent i = new Intent(this, activity_race_prog.class);
                startActivity(i);
        });

        prof.setOnClickListener(v -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });

        Button startRace = this.findViewById(R.id.button2);
        startRace.setOnClickListener(v -> {
            Intent i = new Intent(this, TrackRunActivity.class);
            startActivity(i);
        });
    }

}