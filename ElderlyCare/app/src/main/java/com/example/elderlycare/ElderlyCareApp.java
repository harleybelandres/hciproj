package com.example.elderlycare;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class ElderlyCareApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}
