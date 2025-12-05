package com.example.elderlycare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not signed in, redirect to Login
            redirectToLogin();
            return;
        }

        // Get role from intent or fetch from Firestore if not available
        String role = getIntent().getStringExtra("role");
        if (role == null || role.isEmpty()) {
            // Role not in intent, fetch from Firestore
            fetchUserRole(currentUser.getUid());
        } else {
            // Role available, load appropriate fragment
            loadFragmentByRole(role);
        }
    }

    private void fetchUserRole(String userId) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String role = documentSnapshot.getString("role");
                    if (role != null && !role.isEmpty()) {
                        loadFragmentByRole(role);
                    } else {
                        handleInvalidUser("User role not found");
                    }
                } else {
                    handleInvalidUser("User data not found");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching user role", e);
                showErrorAndFinish("Failed to load user data");
            });
    }

    private void loadFragmentByRole(String role) {
        Fragment fragment;
        String fragmentTag;

        if ("senior".equalsIgnoreCase(role)) {
            fragment = new SeniorDashboardFragment();
            fragmentTag = "SeniorDashboard";
        } else {
            fragment = new CaretakerDoctorDashboardFragment();
            fragmentTag = "CaretakerDoctorDashboard";
        }

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment existingFragment = fragmentManager.findFragmentByTag(fragmentTag);
            
            if (existingFragment == null || !existingFragment.isAdded()) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment, fragmentTag);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment", e);
            showErrorAndFinish("Error loading dashboard");
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleInvalidUser(String message) {
        Log.w(TAG, message);
        // Sign out the user as their data is invalid
        mAuth.signOut();
        showErrorAndFinish("Invalid user data. Please sign in again.");
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        redirectToLogin();
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to login/previous activities
        moveTaskToBack(true);
    }
}
