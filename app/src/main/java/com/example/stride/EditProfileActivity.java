package com.example.stride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private EditText uName;
    private EditText uAge;
    private Spinner uGender;
    private EditText uEmail;
    private ImageButton editName;
    private ImageButton editEmail;
    private ImageButton editAge;
    private Button editPswd;
    private TextView fieldsError;
    private TextView databaseRelatedMessage;
    private ImageButton backBtn;
    private ImageButton ppButton;
    public FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;

    private User userData;
    private String imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://stride-99148-default-rtdb.europe-west1.firebasedatabase.app");;
        reference = database.getReference().child("Users");

        storage = FirebaseStorage.getInstance("gs://stride-99148.appspot.com");
        storageReference = storage.getReference();

        // Get the current user UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String Uid = currentUser.getUid();
        DatabaseReference userRef = reference.child(Uid);

        uName = findViewById(R.id.userName);
        uAge = findViewById(R.id.userAge);
        uGender = findViewById(R.id.userGender);
        uEmail = findViewById(R.id.userEmail);
        fieldsError = findViewById(R.id.invalidAge);
        databaseRelatedMessage = findViewById(R.id.linkSent);
        backBtn = findViewById(R.id.imageButton7);
        ppButton = findViewById(R.id.ChangePpImageButton);

        uName.setEnabled(false);
        uAge.setEnabled(false);
        uGender.setEnabled(true);
        uEmail.setEnabled(false);
        backBtn.setEnabled(true);
        fieldsError.setVisibility(View.INVISIBLE);
        databaseRelatedMessage.setVisibility(View.INVISIBLE);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                userData = (User)dataSnapshot.getValue(User.class);

                // Display user name
                uName.setText(userData.getName());

                // Display user age
                uAge.setText(Integer.toString(userData.getAge()));

                // Display user gender
                switch (userData.getGender()) {
                    case "Male":
                        uGender.setSelection(0);
                        break;

                    case "Female":
                        uGender.setSelection(1);
                        break;

                    case "Non-Binary":
                        uGender.setSelection(2);
                        break;

                    default:
                        uGender.setSelection(3);
                        break;
                }

                // Display user email
                uEmail.setText(userData.getEmail());

                // Display profile picture
                Log.e("PP", userData.getProfilePictureURI());
                Picasso.get().load(userData.getProfilePictureURI()).into(ppButton);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef.addListenerForSingleValueEvent(postListener);

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
                databaseRelatedMessage.setTextColor(getResources().getColor(R.color.valid));
                databaseRelatedMessage.setText(R.string.editProfile_link_sent);
                databaseRelatedMessage.setVisibility(View.VISIBLE);
                new CountDownTimer(2000, 100)
                {

                    @Override
                    public void onTick(long arg0)
                    {

                    }

                    @Override
                    public void onFinish()
                    {
                        databaseRelatedMessage.setVisibility(View.VISIBLE);
                    }
                }.start();
            }

        });

        // Edit profile picture
        ppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open gallery
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST);
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
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri filePath = null;
            if (data != null && data.getData() != null) {
                // Get the Uri of data
                filePath = data.getData();
                Log.e("PROFILE PICTURE", filePath.toString());
            }
            if (filePath != null) {
                StorageReference imgRef = storageReference.child("images/" + userData.getUid() + "_" + UUID.randomUUID());
                imgRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                userData.setProfilePictureURI(uri.toString());
                                Picasso.get().load(userData.getProfilePictureURI()).into(ppButton);
                            }
                        });

                        fieldsError.setText(R.string.editProfile_uploaded);
                        fieldsError.setTextColor(getResources().getColor(R.color.valid));
                        fieldsError.setVisibility(View.VISIBLE);
                        new CountDownTimer(2000, 100)
                        {
                            @Override
                            public void onTick(long arg0)
                            {

                            }

                            @Override
                            public void onFinish()
                            {
                                fieldsError.setVisibility(View.INVISIBLE);
                            }
                        }.start();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PROFILE PICTURE", e.toString());
                        fieldsError.setText(R.string.editProfileError_pfp);
                        fieldsError.setTextColor(getResources().getColor(R.color.error));
                        fieldsError.setVisibility(View.VISIBLE);
                        new CountDownTimer(2000, 100)
                        {
                            @Override
                            public void onTick(long arg0)
                            {

                            }

                            @Override
                            public void onFinish()
                            {
                                fieldsError.setVisibility(View.INVISIBLE);
                            }
                        }.start();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress
                                = (100.0
                                * snapshot.getBytesTransferred()
                                / snapshot.getTotalByteCount());

                        fieldsError.setText(String.format("Uploading... (%.02f)", progress));
                        fieldsError.setTextColor(getResources().getColor(R.color.stride));
                        fieldsError.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    public void linkDatabase(DatabaseReference userRef, EditText uName, EditText uAge, Spinner uGender, EditText uEmail, FirebaseUser currentUser)
    {
        //push user name
        if (!uName.getText().toString().equals(userRef.child("name")))
        {
            userData.setName(uName.getText().toString());
        }

        //push user gender
        if (!uGender.getSelectedItem().toString().equals(userRef.child("gender")))
        {
            userData.setGender(uGender.getSelectedItem().toString());
        }

        //push user age
        if (verifyAge(userRef, uAge))
            userData.setAge(Integer.parseInt(uAge.getText().toString()));

        //push user email
        if (!uEmail.getText().toString().equals(userRef.child("email")))
        {
            userData.setEmail(uEmail.getText().toString());
            currentUser.updateEmail(userData.getEmail());
        }

        userRef.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseRelatedMessage.setTextColor(getResources().getColor(R.color.valid));
                databaseRelatedMessage.setText(R.string.editProfile_successful_update);
                databaseRelatedMessage.setVisibility(View.VISIBLE);
                new CountDownTimer(2000, 100)
                {

                    @Override
                    public void onTick(long arg0)
                    {

                    }

                    @Override
                    public void onFinish()
                    {
                        databaseRelatedMessage.setVisibility(View.VISIBLE);
                    }
                }.start();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                databaseRelatedMessage.setTextColor(getResources().getColor(R.color.error));
                databaseRelatedMessage.setText(R.string.editProfile_problem_update);
                databaseRelatedMessage.setVisibility(View.VISIBLE);
                new CountDownTimer(2000, 100)
                {

                    @Override
                    public void onTick(long arg0)
                    {

                    }

                    @Override
                    public void onFinish()
                    {
                        databaseRelatedMessage.setVisibility(View.VISIBLE);
                    }
                }.start();
            }
        });
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

    private boolean verifyAge(DatabaseReference userRef, EditText uAge)
    {
        String age = uAge.getText().toString();
        int ageNbr = Integer.parseInt(age);
        if (ageNbr < 13 || ageNbr > 120)
        {
            fieldsError.setText(R.string.editProfileError_Age);
            fieldsError.setTextColor(getResources().getColor(R.color.error));
            fieldsError.setVisibility(View.VISIBLE);
            new CountDownTimer(2000, 100)
            {
                @Override
                public void onTick(long arg0)
                {

                }

                @Override
                public void onFinish()
                {
                    fieldsError.setVisibility(View.INVISIBLE);
                }
            }.start();
            return false;
        }
        else
        {
            return true;
        }
    }
}