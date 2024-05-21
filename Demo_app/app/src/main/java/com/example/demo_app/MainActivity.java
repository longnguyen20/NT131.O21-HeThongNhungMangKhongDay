package com.example.demo_app;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;

    private FirebaseAuth mAuth;

    private  String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedUserId = prefs.getString("userId", null);
        if (savedUserId != null) {
            // Phiên đăng nhập đã tồn tại, chuyển đến activity thành công
            Intent intent = new Intent(MainActivity.this, successfully.class);
            intent.putExtra("userId", savedUserId);
            startActivity(intent);
            finish();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, chuyển đến activity thành công
                            FirebaseUser user = mAuth.getCurrentUser();
                            userId = user.getUid();

                            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                            editor.putString("userId", userId);
                            editor.apply();

                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, successfully.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        } else {
                            // Đăng nhập thất bại
                            Toast.makeText(MainActivity.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Intent intent = new Intent(MainActivity.this, register.class);
        startActivity(intent);
    }
}
