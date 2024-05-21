package com.example.demo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Timer;
import java.util.TimerTask;

public class successfully extends AppCompatActivity {

    private ImageButton textViewLogout, btn_open, btn_close, btn_auto;
    private TextView number, status_door;
    private DatabaseReference mDatabase;
    private String userId;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_successfully);

        userId = getIntent().getStringExtra("userId");

        // Khởi tạo Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        textViewLogout = findViewById(R.id.btnLogout);
        btn_open = findViewById(R.id.btn_open);
        btn_close = findViewById(R.id.btn_close);
        btn_auto = findViewById(R.id.btn_auto);
        number = findViewById(R.id.number_people);
        status_door = findViewById(R.id.mode);

        mDatabase.child("users").child(userId).child("MyHome").child("number_people").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer numberPeople = dataSnapshot.getValue(Integer.class);
                if (numberPeople != null) {
                    // Update UI with the number of people
                    number.setText(String.valueOf(numberPeople));
                } else {
                    // Handle null case or default value
                    number.setText("N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Toast.makeText(successfully.this, "Failed to read data number_people from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });


        // Lắng nghe sự kiện khi click nút Logout
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                finish();
            }
        });

        // Lắng nghe sự kiện khi click nút Auto
        btn_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataToFirebase("auto");
            }
        });

        // Lắng nghe sự kiện khi click nút Open
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataToFirebase("open");
            }
        });

        // Lắng nghe sự kiện khi click nút Close
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataToFirebase("close");
            }
        });
    }

    // Hàm cập nhật trạng thái của cửa lên Firebase Realtime Database
    private void updateDataToFirebase(String mode) {
        status_door.setText(mode.toString());
        mDatabase.child("users").child(userId).child("MyHome").child("status_door").setValue(mode);
    }

    // Hàm đăng xuất người dùng
    private void logout() {
        // Xóa thông tin phiên đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.apply();

        // Đăng xuất khỏi Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Chuyển hướng người dùng đến màn hình đăng nhập
        Intent intent = new Intent(successfully.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa tất cả các Activity khác ra khỏi ngăn xếp và tạo một Task mới
        startActivity(intent);
    }
}
