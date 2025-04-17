package com.example.appathon;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnRegister;
    private static final int REQ_ONE_TAP = 100;
    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
   private TextView tvRegister;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnLogin);
        Button googleBtn = findViewById(R.id.googleBtn);
        ImageView ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Password toggle logic
        ivTogglePassword.setOnClickListener(v -> {
            if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.toggle); // Show password
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.toogleo); // Hide password
            }
            etPassword.setSelection(etPassword.getText().length());
        });
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            // Navigate to your ForgotPasswordActivity or show a dialog
            Intent intent = new Intent(Register.this, SignupActivity.class);
            startActivity(intent);
        });
       tvRegister = findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // System bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Google One Tap Setup
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        // Register Button Click
        btnRegister.setOnClickListener(v -> registerUser());

        // Google Button Click
        googleBtn.setOnClickListener(this::buttonGoogle);
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", email);
                jsonBody.put("password", password);

                String baseUrl = getString(R.string.base_url);
                String url = baseUrl + "/users/login";
                postRequest(url, jsonBody.toString());

            } catch (Exception e) {
                Log.e("RegisterActivity", "Error creating JSON body", e);
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postRequest(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
                Log.d("RegisterActivity", "Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();

                        if (responseBody != null && responseBody.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String status = jsonResponse.getString("status");

                            JSONObject userObject = jsonResponse.getJSONObject("user");
                            int id = userObject.getInt("id");
                            String name = userObject.getString("name");

// (Optional) Log or use the ID and name
                            Log.d("Response", "User ID: " + id);
                            Log.d("Response", "User Name: " + name);

                            // Get the ID from the response

                            // (Optional) Log or use the ID
                            Log.d("Response", "User ID: " + id);
                            Log.d("Response", "User NAme: " + name);

                            runOnUiThread(() -> {
                                if (status.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Register.this, Resident.class);
                                    intent.putExtra("RESIDENT_NAME",name );
                                    intent.putExtra("RESIDENT_ID", id);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Registration failed: " + status, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            throw new IOException("Invalid JSON response");
                        }
                    } catch (Exception e) {
                        Log.e("RegisterActivity", "Response parsing error", e);
                        runOnUiThread(() ->
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                } else {
                    Log.e("RegisterActivity", "Registration failed: " + response.message());
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Registration failed: " + response.message(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    public void buttonGoogle(View view) {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d(TAG, "Google Sign-In failed: " + e.getLocalizedMessage());
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP && data != null) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                String username = credential.getId();
                String password = credential.getPassword();
                String email = credential.getGivenName();
                Log.d(TAG, "Google Sign-In Success:");
                Log.d(TAG, "Username: " + username);
                Log.d(TAG, "Email: " + email);
                if (idToken != null) {
                    Log.d(TAG, "Got ID token: " + idToken);
                    // TODO: Send ID token to backend server for verification and login
                } else if (password != null) {
                    Log.d(TAG, "Got password login for: " + username);
                    // TODO: Authenticate using username and password
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-In failed: " + e.getMessage());
            }
        }
    }
}
