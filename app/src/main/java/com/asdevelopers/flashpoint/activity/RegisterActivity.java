package com.asdevelopers.flashpoint.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asdevelopers.flashpoint.R;
import com.asdevelopers.flashpoint.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText fullNameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;
    private TextView signInMessage;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        progressBar = findViewById(R.id.progress_bar_signup);
        fullNameEditText = findViewById(R.id.name_edit_text);
        usernameEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        signUpButton = findViewById(R.id.sign_up_button);

        signInMessage = findViewById(R.id.signup_message);

        signInMessage.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        signUpButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            String fullName = fullNameEditText.getText().toString().trim();
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = new User(fullName, email);

                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userID)
                                    .setValue(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.putExtra("userID", userID);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "User database failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        } else {
                            Toast.makeText(RegisterActivity.this, "Account creation failed", Toast.LENGTH_SHORT).show();
                        }
                    });
            progressBar.setVisibility(View.GONE);
        });
    }
}