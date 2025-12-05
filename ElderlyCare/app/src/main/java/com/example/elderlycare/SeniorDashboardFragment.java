package com.example.elderlycare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class SeniorDashboardFragment extends Fragment {

    private Button btnMedicalUpdates, btnMessages, btnMyCaretakers, btnEmergency, btnDOH, btnPhilHealth, btnDSWD;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_senior_dashboard, container, false);

        try {
            db = FirebaseUtils.getDb();
            FirebaseUser currentUser = FirebaseUtils.getAuth().getCurrentUser();
            if (currentUser == null) {
                handleUserNotLoggedIn();
                return view;
            }
            currentUserId = currentUser.getUid();

            // Initialize buttons
            btnEmergency = view.findViewById(R.id.btnEmergency);
            btnDOH = view.findViewById(R.id.btnDOH);
            btnPhilHealth = view.findViewById(R.id.btnPhilHealth);
            btnDSWD = view.findViewById(R.id.btnDSWD);
            btnMessages = view.findViewById(R.id.btnMessages);
            btnMyCaretakers = view.findViewById(R.id.btnMyCaretakers);
            btnMedicalUpdates = view.findViewById(R.id.btnMedicalUpdates);

            // Set click listeners
            if (btnEmergency != null) {
                btnEmergency.setOnClickListener(v -> sendEmergencySMS());
            }
            if (btnDOH != null) {
                btnDOH.setOnClickListener(v -> openUrl("https://www.doh.gov.ph"));
            }
            if (btnPhilHealth != null) {
                btnPhilHealth.setOnClickListener(v -> openUrl("https://www.philhealth.gov.ph"));
            }
            if (btnDSWD != null) {
                btnDSWD.setOnClickListener(v -> openUrl("https://www.dswd.gov.ph"));
            }
            
            // Initialize other buttons with null checks
            if (btnMessages != null) {
                btnMessages.setOnClickListener(v -> {
                    // Open chat activity - implement as needed
                });
            }
            
            if (btnMyCaretakers != null) {
                btnMyCaretakers.setOnClickListener(v -> {
                    // Open caretakers list - implement as needed
                });
            }
            
            if (btnMedicalUpdates != null) {
                btnMedicalUpdates.setOnClickListener(v -> {
                    // Open medical updates - implement as needed
                });
            }
            
        } catch (Exception e) {
            Log.e("SeniorDashboard", "Error initializing fragment", e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error initializing dashboard", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    private void sendEmergencySMS() {
        Context context = getContext();
        if (context == null || getActivity() == null) {
            Log.e("SeniorDashboard", "Context or Activity is null");
            return;
        }

        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), 
                new String[]{Manifest.permission.SEND_SMS}, 1);
            return;
        }

        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("SeniorDashboard", "Invalid user ID");
            showToast(context, "Error: Invalid user session");
            return;
        }

        db.collection("users").document(currentUserId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    showToast(context, "Error: User data not found");
                    return;
                }
                
                List<String> caretakers = (List<String>) documentSnapshot.get("caretakers");
                if (caretakers == null || caretakers.isEmpty()) {
                    showToast(context, "No caretakers linked to your account");
                    return;
                }

                SmsManager smsManager = SmsManager.getDefault();
                String message = "EMERGENCY: I need help! Please contact me immediately.";
                int sentCount = 0;
                int totalCaretakers = caretakers.size();

                for (String caretakerId : caretakers) {
                    if (caretakerId == null || caretakerId.isEmpty()) continue;
                    
                    db.collection("users").document(caretakerId).get()
                        .addOnSuccessListener(caretakerDoc -> {
                            if (!caretakerDoc.exists()) return;
                            
                            String phone = caretakerDoc.getString("phone");
                            if (phone != null && !phone.isEmpty()) {
                                try {
                                    smsManager.sendTextMessage(phone, null, message, null, null);
                                    Log.d("SeniorDashboard", "SMS sent to: " + phone);
                                } catch (Exception e) {
                                    Log.e("SeniorDashboard", "Failed to send SMS to " + phone, e);
                                }
                            }
                        })
                        .addOnFailureListener(e -> 
                            Log.e("SeniorDashboard", "Error fetching caretaker " + caretakerId, e)
                        );
                }
                
                showToast(context, "Emergency alert sent to your caretakers");
            })
            .addOnFailureListener(e -> {
                Log.e("SeniorDashboard", "Error fetching user data", e);
                showToast(context, "Error: Could not send emergency alert");
            });
    }

    private void openUrl(String url) {
        if (getActivity() == null) return;
        
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            // Verify that the intent will resolve to an activity
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showToast(getContext(), "No app found to handle this request");
            }
        } catch (Exception e) {
            Log.e("SeniorDashboard", "Error opening URL: " + url, e);
            showToast(getContext(), "Could not open the link");
        }
    }
    
    private void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void handleUserNotLoggedIn() {
        if (getActivity() != null) {
            showToast(getContext(), "Please log in to continue");
            getActivity().finish();
        }
    }
}
