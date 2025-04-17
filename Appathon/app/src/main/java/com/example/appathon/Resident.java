package com.example.appathon;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.util.Log;

import android.widget.TextView;


import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Resident extends AppCompatActivity {

    private TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident); // Your layout with RelativeLayout & Grid

        Intent intent = getIntent();
        String residentName = intent.getStringExtra("RESIDENT_NAME");
        int residentId = intent.getIntExtra("RESIDENT_ID", -1);

        Log.d("ResidentActivity", "Received Name: " + residentName);
        Log.d("ResidentActivity", "Received ID: " + residentId);

        backButton = findViewById(R.id.textView7);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(Resident.this, Register.class));
            finish();
        });

        // Set insets for status bar (like edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bb), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle "Pre-register"
        CardView preRegister = findViewById(R.id.card_preregister);
        preRegister.setOnClickListener(v -> {
            Intent it = new Intent(Resident.this, PreRegisterActivity.class);
            it.putExtra("RESIDENT_NAME", residentName);
            it.putExtra("RESIDENT_ID", residentId);
            Log.d("ResidentActivity", "Receivedmsnnssj ID: " + residentId);
            startActivity(it);
        });

        // Handle "Available Slots"
        CardView availableSlots = findViewById(R.id.card_available_slots);
        availableSlots.setOnClickListener(v -> {
            Intent it = new Intent(Resident.this, AvailableSlotsActivity.class);
            it.putExtra("RESIDENT_NAME", residentName);
            it.putExtra("RESIDENT_ID", residentId);
            startActivity(it);
        });

        // Handle "Bulk Booking"
        CardView bulkBooking = findViewById(R.id.card_bulk_booking);
        bulkBooking.setOnClickListener(v -> {
            Intent it = new Intent(Resident.this, BulkBookingActivity.class);
            it.putExtra("RESIDENT_NAME", residentName);
            it.putExtra("RESIDENT_ID", residentId);
            startActivity(it);
        });

        // Handle "Cancel Booking"
        CardView cancelBooking = findViewById(R.id.card_cancel_booking);
        cancelBooking.setOnClickListener(v -> {
            Intent it = new Intent(Resident.this, CancelBookingActivity.class);
            it.putExtra("RESIDENT_NAME", residentName);
            it.putExtra("RESIDENT_ID", residentId);
            startActivity(it);
        });
    }
}
