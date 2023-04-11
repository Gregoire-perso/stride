package com.example.stride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private TextView uName;
    private TextView uAge;
    private TextView uGender;
    private TextView uEmail;
    private ImageButton editName;

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

        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //String Uid = currentUser.getUid();
        String Uid = "HwLYNJxuIvNLGyGe6qGn3BdouVb2";
        DatabaseReference userRef = reference.child(Uid);

        uName = findViewById(R.id.userName);

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

        editName = findViewById(R.id.imageButton);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //
            }
        });

    }

    public void editDetail()
    {

    }

}