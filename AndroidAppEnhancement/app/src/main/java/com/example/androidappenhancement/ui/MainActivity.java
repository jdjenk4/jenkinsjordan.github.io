package com.example.androidappenhancement.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.telephony.SmsManager;

import com.example.androidappenhancement.data.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    EditText nameText, passwordText;
    TextView textGreeting;
    Button buttonLogin, buttonRegister, buttonSendSMS;
    DatabaseHelper db;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        nameText = findViewById(R.id.editTextTextPersonName);
        passwordText = findViewById(R.id.editTextTextPassword); // Assume you have this EditText for password input
        textGreeting = findViewById(R.id.textView2);
        buttonLogin = findViewById(R.id.buttonLogin); // Assume you have this button for logging in
        buttonRegister = findViewById(R.id.buttonRegister); // Assume this is your register button
        buttonSendSMS = findViewById(R.id.buttonSendSMS); // Button to trigger SMS sending for demonstration

        buttonLogin.setOnClickListener(view -> loginUser());
        buttonRegister.setOnClickListener(view -> registerUser());
        buttonSendSMS.setOnClickListener(view -> sendSMSMessage());
    }
    protected void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+1 111-123-4567", null, "SMS message", null, null);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("+1 111-123-4567", null, "SMS message", null, null);
            }
            // permission denied
        }
    }
    @SuppressLint("SetTextI18n")
    private void loginUser() {
        String username = nameText.getText().toString();
        String password = passwordText.getText().toString();

        if (db.authenticateUser(username, password)) {
            textGreeting.setText(String.format("Hello, %s", username));
        } else {
            textGreeting.setText("Authentication failed.");
        }
    }
    @SuppressLint("SetTextI18n")
    private void registerUser() {
        String username = nameText.getText().toString();
        String password = passwordText.getText().toString();

        db.addUser(username, password);
        textGreeting.setText("Registration successful.");
    }
}