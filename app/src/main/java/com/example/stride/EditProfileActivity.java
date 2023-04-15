package com.example.stride;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText uName;
    private EditText uAge;
    private Spinner uGender;
    private EditText uEmail;

    private ImageButton editName;
    private ImageButton editEmail;

    private ImageButton editAge;

    private Button editPswd;

    private TextView invalidAge;

    private TextView linkSent;

    private ImageButton backBtn;

    public FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://stride-99148-default-rtdb.europe-west1.firebasedatabase.app");;
        reference = database.getReference().child("Users");

        // Get the current user UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String Uid = currentUser.getUid();
        DatabaseReference userRef = reference.child(Uid);

        uName = findViewById(R.id.userName);
        uAge = findViewById(R.id.userAge);
        uGender = findViewById(R.id.userGender);
        uEmail = findViewById(R.id.userEmail);
        invalidAge = findViewById(R.id.invalidAge);
        linkSent = findViewById(R.id.linkSent);
        backBtn = findViewById(R.id.imageButton7);

        uName.setEnabled(false);
        uAge.setEnabled(false);
        uGender.setEnabled(true);
        uEmail.setEnabled(false);
        backBtn.setEnabled(true);
        invalidAge.setVisibility(View.INVISIBLE);
        linkSent.setVisibility(View.INVISIBLE);

        // Display user name
        ValueEventListener nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String userName = dataSnapshot.child("name").getValue(String.class);
                uName.setText(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef.addValueEventListener(nameListener);

        // Display user gender
        ValueEventListener genderListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String userGender = dataSnapshot.child("gender").getValue(String.class);
                int pos;
                if (userGender.equals("Male"))
                    pos = 0;
                else if (userGender.equals("Female"))
                {
                    pos = 1;
                }
                else if (userGender.equals("Non-Binary"))
                {
                    pos = 2;
                }
                else
                {
                    pos = 3;
                }
                uGender.setSelection(pos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef.addValueEventListener(genderListener);

        // Display user age
        ValueEventListener ageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int userAge = dataSnapshot.child("age").getValue(Integer.class);
                uAge.setText(Integer.toString(userAge));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef.addValueEventListener(ageListener);

        // Display user email

        ValueEventListener emailListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userEmail = dataSnapshot.child("email").getValue(String.class);
                uEmail.setText(userEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef.addValueEventListener(emailListener);


        editName = findViewById(R.id.imageButton);
        editEmail = findViewById(R.id.imageButton4);
        editAge = findViewById(R.id.imageButton2);
        editPswd = findViewById(R.id.userPassword);

        // Edit user name
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                enableDisableEditText(uName, editName);
            }
        });
        //Edit user age
        editAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                enableDisableEditText(uAge, editAge);
            }
        });
        // Edit user email
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                enableDisableEditText(uEmail, editEmail);
            }
        });
        // Edit user password
        editPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mAuth.sendPasswordResetEmail(uEmail.getText().toString());
                linkSent.setVisibility(View.VISIBLE);
                new CountDownTimer(2000, 100)
                {

                    @Override
                    public void onTick(long arg0)
                    {

                    }

                    @Override
                    public void onFinish()
                    {
                        linkSent.setVisibility(View.VISIBLE);
                    }
                }.start();
            }

        });


        // Update the changes and link them to database
        Button updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                linkDatabase(userRef, uName, uAge, uGender, uEmail, currentUser);

                updateBtn.setBackgroundColor(Color.GRAY);
                new CountDownTimer(2000, 100)
                {

                    @Override
                    public void onTick(long arg0)
                    {

                    }

                    @Override
                    public void onFinish()
                    {

                        updateBtn.setBackgroundColor(Color.rgb(10,36,99));

                    }
                }.start();
            }
        });

        // Go back to the profile view

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
            }
        });

    }

    public void linkDatabase(DatabaseReference userRef, EditText uName, EditText uAge, Spinner uGender, EditText uEmail, FirebaseUser currentUser)
    {
        //push user name
        if (!uName.getText().toString().equals(userRef.child("name")))
        {
            userRef.child("name").setValue(uName.getText().toString());
        }
        //push user gender
        if (!uGender.getSelectedItem().toString().equals(userRef.child("gender")))
        {
            userRef.child("gender").setValue(uGender.getSelectedItem().toString());
        }
        //push user age
        verifyAge(userRef, uAge);
        //push user email
        if (!uEmail.getText().toString().equals(userRef.child("email")))
        {
            userRef.child("email").setValue(uEmail.getText().toString());
            currentUser.updateEmail(uEmail.getText().toString());
        }
    }

    private void enableDisableEditText(EditText edText, ImageButton editBtn) {
        if (edText.isEnabled()) {
            edText.setEnabled(false);
            editBtn.setBackgroundResource(R.drawable.edit);
        } else {
            edText.setEnabled(true);
            editBtn.setBackgroundResource(R.drawable.validate);
        }
    }

    private void verifyAge(DatabaseReference userRef, EditText uAge)
    {
        String age = uAge.getText().toString();
        int ageNbr = Integer.parseInt(age);
        if (ageNbr < 13 || ageNbr > 120)
        {
            invalidAge.setVisibility(View.VISIBLE);
            new CountDownTimer(2000, 100)
            {
                @Override
                public void onTick(long arg0)
                {

                }

                @Override
                public void onFinish()
                {
                    invalidAge.setVisibility(View.INVISIBLE);
                }
            }.start();
        }
        else
        {
            userRef.child("age").setValue(ageNbr);
        }
    }


}