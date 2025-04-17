package com.example.appathon;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private CheckBox cbShowPassword;
    private Button btnSignup;
    private TextView tvLogin;
    private Spinner spinnerRole;  // Spinner for role selection

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etNamee);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhoneNumber);  // Phone number EditText
        etAddress = findViewById(R.id.etAddress);  // Address EditText
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbShowPassword = findViewById(R.id.cbShowPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        spinnerRole = findViewById(R.id.spinnerRole);  // Initialize the Spinner

        // Set up the spinner with roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPassword.setInputType(0x00000081); // TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                etConfirmPassword.setInputType(0x00000081);
            } else {
                etPassword.setInputType(0x00000081 | 0x00000020); // TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD
                etConfirmPassword.setInputType(0x00000081 | 0x00000020);
            }
        });

        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();  // Get phone number
            String address = etAddress.getText().toString().trim();  // Get address
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();  // Get selected role

            // Validation
            if (TextUtils.isEmpty(name)) {
                etName.setError("Name is required");
                return;
            }
            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Valid email is required");
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                etPhone.setError("Phone number is required");
                return;
            }
            if (TextUtils.isEmpty(address)) {
                etAddress.setError("Address is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                etConfirmPassword.setError("Confirm your password");
                return;
            }
            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            // Prepare JSON and make API call
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("name", name);
                jsonBody.put("email", email);
                jsonBody.put("phoneNumber", phone);  // new
// Include phone number
                jsonBody.put("address", address);  // Include address
                jsonBody.put("password", password);
                jsonBody.put("role", role);  // Include selected role
                Log.d("SignupActivity", "Request Body: " + jsonBody.toString());
                String baseUrl = getString(R.string.base_url);
                String url = baseUrl + "/users/register";
                postRequest(url, jsonBody.toString());
                Log.d("SignupActivity", "Request Body: " + jsonBody.toString());

            } catch (Exception e) {
                Log.e("SignupActivity", "Error creating JSON body", e);
                Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, Register.class);
            startActivity(intent);
            finish();
        });
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
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Network Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("SignupActivity", "Network Error: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, Register.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        Log.e("SignupActivity", "Response Code: " + response.code() + ", Message: " + response.message());
                    }
                });
            }
        });
    }
}
