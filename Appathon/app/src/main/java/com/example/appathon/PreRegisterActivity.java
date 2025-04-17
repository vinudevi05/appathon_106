
package com.example.appathon;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class PreRegisterActivity extends AppCompatActivity {

    private EditText etVisitorName, etPurpose, etEmail, etVehicleNumber, etNoOfMembers;
    private Button btnCheckSlot, btnSubmit;
   private  TextView tvStartTime, tvEndTime;
    private EditText mDisplay;
    private Spinner spinnerStartTime, spinnerEndTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private final OkHttpClient client = new OkHttpClient();
    private int  residentId;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pre_register);
        mDisplay = findViewById(R.id.tvdate);
        // Initialize the views
        etVisitorName = findViewById(R.id.etVisitorName);
        Intent intent = getIntent();
        String residentName = intent.getStringExtra("RESIDENT_NAME");
        residentId = intent.getIntExtra("RESIDENT_ID", -1);

        etPurpose = findViewById(R.id.etPurpose);
        etEmail = findViewById(R.id.etEmail);
        etVehicleNumber = findViewById(R.id.etVehicleNumber);
        etNoOfMembers = findViewById(R.id.etNoOfMembers);
        btnCheckSlot = findViewById(R.id.btnCheckSlot);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        mDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvStartTime);
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1; // Month is 0-based
                String date = month + "/" + dayOfMonth + "/" + year;
                mDisplay.setText(date);
            }
        };

        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvEndTime);
            }
        });

        btnCheckSlot.setOnClickListener(v -> checkSlotAvailability());
        btnSubmit.setOnClickListener(v -> submitPreRegistration());
    }

    private void checkSlotAvailability() {
        // Code to check slot availability
        // For now, it just shows a toast message, but you can implement a server-side check
        Toast.makeText(getApplicationContext(), "Checking slot availability...", Toast.LENGTH_SHORT).show();
    }
    private void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(PreRegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private void submitPreRegistration() {
        String visitorName = etVisitorName.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String vehicleNumber = etVehicleNumber.getText().toString().trim();
        int noOfMembers = Integer.parseInt(etNoOfMembers.getText().toString().trim());


        // Validation
        if (TextUtils.isEmpty(visitorName)) {
            etVisitorName.setError("Visitor Name is required");
            return;
        }
        if (TextUtils.isEmpty(purpose)) {
            etPurpose.setError("Purpose is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(vehicleNumber)) {
            etVehicleNumber.setError("Vehicle Number is required");
            return;
        }
        if (noOfMembers <= 0) {
            etNoOfMembers.setError("Number of members must be greater than 0");
            return;
        }

        // Prepare JSON and make API call to submit
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("visitorName", visitorName);
            jsonBody.put("purpose", purpose);
            jsonBody.put("email", email);
            jsonBody.put("vehicleNumber", vehicleNumber);
            jsonBody.put("noOfMembers", noOfMembers);
            jsonBody.put("date", mDisplay.getText().toString().trim()); // Add date from EditText
            jsonBody.put("startTime", tvStartTime.getText().toString().trim());
            jsonBody.put("endTime", tvEndTime.getText().toString().trim());
            jsonBody.put("bookedById",residentId );

            String baseUrl = getString(R.string.base_url);
            String url = baseUrl + "/api/preregister";
            postRequest(url, jsonBody.toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error creating JSON body", Toast.LENGTH_SHORT).show();
        }
    }
    private void showTimePickerDialog(TextView targetView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                PreRegisterActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, hourOfDay, minute1) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    targetView.setText(time);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();
    }
    private void postRequest(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Network Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Pre-registration successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Pre-registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
