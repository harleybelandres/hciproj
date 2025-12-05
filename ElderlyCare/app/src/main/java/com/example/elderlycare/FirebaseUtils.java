package com.example.elderlycare;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Utility class for Firebase-related operations.
 * Handles initialization and provides access to Firebase services.
 */
public class FirebaseUtils {
    private static final String TAG = "FirebaseUtils";
    
    @Nullable
    private static FirebaseAuth auth;
    
    @Nullable
    private static FirebaseFirestore db;
    
    // Private constructor to prevent instantiation
    private FirebaseUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Gets the FirebaseAuth instance, initializing it if necessary.
     * @return The FirebaseAuth instance
     */
    @NonNull
    public static synchronized FirebaseAuth getAuth() {
        if (auth == null) {
            try {
                auth = FirebaseAuth.getInstance();
                // Enable persistence for better offline support
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build();
                getDb().setFirestoreSettings(settings);
            } catch (IllegalStateException e) {
                Log.e(TAG, "FirebaseApp not initialized. Make sure to call FirebaseApp.initializeApp(context) first.", e);
                throw new IllegalStateException("Firebase not initialized. Please check your Firebase configuration.");
            }
        }
        return auth;
    }

    /**
     * Gets the Firestore instance, initializing it if necessary.
     * @return The Firestore instance
     */
    @NonNull
    public static synchronized FirebaseFirestore getDb() {
        if (db == null) {
            try {
                db = FirebaseFirestore.getInstance();
                // Enable offline persistence
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build();
                db.setFirestoreSettings(settings);
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize Firestore", e);
                throw new RuntimeException("Failed to initialize Firestore", e);
            }
        }
        return db;
    }

    /**
     * Saves the FCM token to the user's document in Firestore.
     * @param uid The user ID
     */
    public static void saveFcmToken(@NonNull String uid) {
        if (uid.isEmpty()) {
            Log.w(TAG, "Attempted to save FCM token with empty UID");
            return;
        }

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful() || task.getResult() == null) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                String token = task.getResult();
                if (token == null || token.isEmpty()) {
                    Log.w(TAG, "FCM token is null or empty");
                    return;
                }

                DocumentReference userRef = getDb().collection("users").document(uid);
                userRef.update("fcmToken", token)
                    .addOnSuccessListener(aVoid -> 
                        Log.d(TAG, "FCM token updated successfully for user: " + uid)
                    )
                    .addOnFailureListener(e -> 
                        Log.e(TAG, "Error updating FCM token for user: " + uid, e)
                    );
            })
            .addOnFailureListener(e -> 
                Log.e(TAG, "Failed to get FCM token for user: " + uid, e)
            );
    }
    
    /**
     * Clears all Firebase instances. Useful for testing or when you need to reset the Firebase state.
     */
    public static synchronized void clearInstances() {
        auth = null;
        db = null;
        Log.d(TAG, "Cleared Firebase instances");
    }
}
