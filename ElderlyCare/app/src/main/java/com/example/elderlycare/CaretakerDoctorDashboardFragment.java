package com.example.elderlycare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class CaretakerDoctorDashboardFragment extends Fragment {

    private Button btnPatients, btnAddPatient, btnMessages, btnSendUpdate, btnNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caretaker_doctor_dashboard, container, false);

        btnPatients = view.findViewById(R.id.btnPatients);
        btnAddPatient = view.findViewById(R.id.btnAddPatient);
        btnMessages = view.findViewById(R.id.btnMessages);
        btnSendUpdate = view.findViewById(R.id.btnSendUpdate);
        btnNotifications = view.findViewById(R.id.btnNotifications);

        btnPatients.setOnClickListener(v -> {
            // Open patients list
        });

        btnAddPatient.setOnClickListener(v -> {
            // Open add patient activity
        });

        btnMessages.setOnClickListener(v -> {
            // Open chat activity
        });

        btnSendUpdate.setOnClickListener(v -> {
            // Open send update activity
        });

        btnNotifications.setOnClickListener(v -> {
            // Open notifications
        });

        return view;
    }
}
