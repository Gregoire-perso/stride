package com.example.stride;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {


    public FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private TextView uName;
    private TextView uAge;
    private TextView uGender;
    private TextView racesNbr;
    private TextView kmNbr;

    private ImageButton changePfp;
    private ImageView pfp;

    private Button editBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uName = findViewById(R.id.nameView);
        uAge = findViewById(R.id.textView);
        uGender = findViewById(R.id.textView2);
        racesNbr = findViewById(R.id.textView3);
        kmNbr = findViewById(R.id.textView4);
        changePfp = findViewById(R.id.changePfp);
        pfp = findViewById(R.id.imageView);
        editBtn = findViewById(R.id.editButton);

        // Display profile picture

        // Change profile picture

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>()
                {
                    @Override
                    public void onActivityResult(Uri result)
                    {
                        if (result != null)
                        {
                            pfp.setImageURI(result);
                        }
                    }
                }
        );

        changePfp.setOnClickListener(v -> mGetContent.launch("image/*"));

        // Go to edit personal details view
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://stride-99148-default-rtdb.europe-west1.firebasedatabase.app");;
        reference = database.getReference().child("Users");

        // Get the current user UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String Uid = currentUser.getUid();
        DatabaseReference userRef = reference.child(Uid);

        // Get the user datas
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User userData = (User)dataSnapshot.getValue(User.class);

                // Display user name
                uName.setText(userData.getName());

                // Display user age
                uAge.setText(userData.getAge());

                // Display user gender
                uGender.setText(userData.getGender());

                // Display races number
                racesNbr.setText(userData.getRacesNbr());

                // Display kms traveled
                kmNbr.setText(Float.toString(userData.getTotalMeters() / 1000f));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef.addListenerForSingleValueEvent(postListener);
    }
}