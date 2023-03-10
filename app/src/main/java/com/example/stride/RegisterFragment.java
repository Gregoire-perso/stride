package com.example.stride;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;

    private enum PasswordStrength {
        NONE,
        WEAK,
        REASONABLE,
        STRONG,
        VERY_STRONG
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //if(currentUser != null){
        //    reload();
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    private PasswordStrength computePassword(@NonNull String password) {
        if (password.length() < 8)
            return PasswordStrength.NONE;

        int upper = 0;
        int lower = 0;
        int digit = 0;
        int special = 0;

        char[] char_pass = password.toCharArray();

        for(char c : char_pass){
            if (Character.isDigit(c))
                digit = 1;

            if (Character.isUpperCase(c))
                upper = 1;

            if (Character.isLowerCase(c))
                lower = 1;

            if (!Character.isLetterOrDigit(c))
                special = 1;
        }

        int sum = upper + lower + digit + special;

        switch (sum) {
            case 1:
            case 2:
                return PasswordStrength.WEAK;

            case 3:
                return PasswordStrength.REASONABLE;

            case 4:
                if (password.length() < 12)
                    return PasswordStrength.STRONG;
                else
                    return PasswordStrength.VERY_STRONG;

            default:
                return PasswordStrength.NONE;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle SavedInstanceState) {
        super.onViewCreated(view, SavedInstanceState);

        ImageButton backButton = view.findViewById(R.id.registerBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_welcomeFragment);
            }
        });

        Button RegisterButton = view.findViewById(R.id.registerButton);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) view.findViewById(R.id.registerEmail)).getText().toString();
                EditText passwordText = view.findViewById(R.id.registerPassword);
                String password = passwordText.getText().toString();
                EditText confirmPasswordText = view.findViewById(R.id.registerConfirmPassword);
                String confirmPassword = confirmPasswordText.getText().toString();
                TextView registrationError = view.findViewById(R.id.registerRegistrationError);
                registrationError.setVisibility(View.INVISIBLE);

                if (password.length() == 0 || email.length() == 0) {
                    registrationError.setText(R.string.connection_no_email_or_password);
                    registrationError.setVisibility(View.VISIBLE);
                }
                else if (!password.equals(confirmPassword)) {
                    registrationError.setText(R.string.connection_not_same);
                    registrationError.setVisibility(View.VISIBLE);
                    passwordText.setText("");
                    confirmPasswordText.setText("");
                }
                else if (computePassword(password) == PasswordStrength.NONE) {
                    registrationError.setText(R.string.connection_too_short);
                    registrationError.setVisibility(View.VISIBLE);
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // TODO
                                        //updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        registrationError.setText(task.getException().getMessage());
                                        registrationError.setVisibility(View.VISIBLE);
                                        //updateUI(null);
                                    }
                                }
                            });
                }
            }
        });

        EditText passwordEdit = view.findViewById(R.id.registerPassword);
        Drawable defaultEditTextBackground = passwordEdit.getBackground();
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (computePassword(passwordEdit.getText().toString())) {
                    case WEAK:
                        passwordEdit.setBackgroundResource(R.drawable.weak_password);
                        break;

                    case REASONABLE:
                        passwordEdit.setBackgroundResource(R.drawable.reasonable_password);
                        break;

                    case STRONG:
                        passwordEdit.setBackgroundResource(R.drawable.strong_password);
                        break;

                    case VERY_STRONG:
                        passwordEdit.setBackgroundResource(R.drawable.very_strong_password);
                        break;

                    default:
                        passwordEdit.setBackground(defaultEditTextBackground);
                        break;

                }
            }
        });
    }

}