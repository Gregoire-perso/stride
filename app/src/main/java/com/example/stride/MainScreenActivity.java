package com.example.stride;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;


public class MainScreenActivity extends AppCompatActivity {

    public FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private User us;
    String newline =System.getProperty("line.separator");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://stride-99148-default-rtdb.europe-west1.firebasedatabase.app");

        reference = database.getReference().child("Users").child(user.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                us = (User)dataSnapshot.getValue(User.class);
                for (int i = 0 ; i< us.run.size() ; i++)
                {
                    if (us.run.get(i) == null)
                        continue;
                    System.out.println(us.run);
                    ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
                    long course = LocalDateTime.parse(us.run.get(i).getDate()).atZone(zoneId).toEpochSecond();
                    long mtn = LocalDateTime.now().atZone(zoneId).toEpochSecond();

                    if (course > mtn)
                    {
                        TextView tv9 = (TextView) findViewById(R.id.textView9);
                        long diff = course - mtn;
                        if (diff> 24 && diff < 48)
                        {
                            tv9.setText(diff / 60 / 60/24 + " day");
                        }
                        else if (diff >48)
                        {
                            tv9.setText(diff / 60 / 60/24 + " days");
                        }
                        else {
                            tv9.setText(diff / 60 / 60 + " hours");
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addListenerForSingleValueEvent(postListener);

    }

}