package com.example.adminpart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginAdmin extends AppCompatActivity {

    TextInputEditText tietemail, tietpass;
    Button btnlogin;
    TextView tvforgot;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser user;


    private static final String ADMIN_EMAIL = "khuzaimashakeel3@gmail.com";
    private static final String ADMIN_PASSWORD = "1111111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_admin);
        FirebaseApp.initializeApp(this);



        init();
        if(user!=null)
        {
            MovetoHome();
            return;
        }
        setLoginButtonListener();
    }

    private void setLoginButtonListener() {
        btnlogin.setOnClickListener(v -> {
            String email = Objects.requireNonNull(tietemail.getText()).toString().trim();
            String pass = Objects.requireNonNull(tietpass.getText()).toString().trim();
            if (email.isEmpty()) {
                tietemail.setError("Email can't be empty");
                return;
            }
            if (pass.isEmpty()) {
                tietpass.setError("Password can't be empty");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            // Check if the email and password match the hardcoded admin credentials
            if (email.equals(ADMIN_EMAIL) && pass.equals(ADMIN_PASSWORD)) {
                // Attempt to sign in with Firebase Auth
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                                if (user != null) {
                                    MovetoHome();
                                } else {
                                    Toast.makeText(LoginAdmin.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginAdmin.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginAdmin.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void MovetoHome() {
        startActivity(new Intent(LoginAdmin.this, Home.class));
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        tietemail = findViewById(R.id.tietEmail);
        tietpass = findViewById(R.id.tietPass);
        btnlogin = findViewById(R.id.btnLogin);
        tvforgot = findViewById(R.id.tvForgot);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }
}
