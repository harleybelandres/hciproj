package com.example.elderlycare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseUtils.getAuth();
        db = FirebaseUtils.getDb();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Show loading state
        setFormEnabled(false);
        
        // Attempt to sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, get the current user
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        // Save FCM token for push notifications
                        FirebaseUtils.saveFcmToken(uid);
                        
                        // Fetch user data from Firestore
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String role = documentSnapshot.getString("role");
                                    if (role != null && !role.isEmpty()) {
                                        // Navigate to Dashboard with user role
                                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                        intent.putExtra("role", role);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Role not found in user document
                                        handleLoginError("User role not found. Please contact support.");
                                        auth.signOut();
                                    }
                                } else {
                                    // User document doesn't exist
                                    handleLoginError("User data not found. Please contact support.");
                                    auth.signOut();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("LoginActivity", "Error fetching user data", e);
                                handleLoginError("Error loading user data. Please try again.");
                                auth.signOut();
                            });
                    } else {
                        // This should not happen if task was successful
                        handleLoginError("Authentication error. Please try again.");
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    String errorMessage = "Authentication failed.";
                    if (task.getException() != null) {
                        String error = task.getException().getMessage();
                        if (error != null) {
                            if (error.contains("no user record")) {
                                errorMessage = "No account found with this email.";
                            } else if (error.contains("password is invalid")) {
                                errorMessage = "Incorrect password. Please try again.";
                            } else if (error.contains("network error")) {
                                errorMessage = "Network error. Please check your connection.";
                            } else {
                                errorMessage = "Error: " + error;
                            }
                        }
                    }
                    handleLoginError(errorMessage);
                }
                
                // Re-enable form after login attempt
                setFormEnabled(true);
            });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        
        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter your email address");
            etEmail.requestFocus();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }
        
        // Show loading state
        setFormEnabled(false);
        
        // Send password reset email
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                // Re-enable form
                setFormEnabled(true);
                
                if (task.isSuccessful()) {
                    showToast("Password reset email sent to " + email);
                } else {
                    String errorMessage = "Failed to send reset email";
                    if (task.getException() != null && task.getException().getMessage() != null) {
                        String error = task.getException().getMessage();
                        if (error.contains("user-not-found")) {
                            errorMessage = "No account found with this email address";
                        } else if (error.contains("invalid-email")) {
                            errorMessage = "Invalid email address";
                        } else if (error.contains("network-request-failed")) {
                            errorMessage = "Network error. Please check your connection.";
                        } else {
                            errorMessage = "Error: " + error;
                        }
                    }
                    showToast(errorMessage);
                }
                });
    }
    
    private void setFormEnabled(boolean enabled) {
        etEmail.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
        tvForgotPassword.setEnabled(enabled);
        
        // Update button text to show loading state
        if (enabled) {
            btnLogin.setText(R.string.login);
        } else {
            btnLogin.setText(R.string.please_wait);
        }
    }
    
    private void handleLoginError(String message) {
        showToast(message);
        // Clear password field on error for security
        etPassword.setText("");
        etPassword.requestFocus();
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
