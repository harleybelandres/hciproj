package com.example.elderlycare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText etName, etEmail, etPassword, etPhone, etBirthday;
    private RadioGroup rgRole;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseUtils.getAuth();
        db = FirebaseUtils.getDb();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etBirthday = findViewById(R.id.etBirthday);
        rgRole = findViewById(R.id.rgRole);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = rgRole.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbSelected = findViewById(selectedId);
        String role = rbSelected.getText().toString().toLowerCase();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = auth.getCurrentUser().getUid();
                            FirebaseUtils.saveFcmToken(uid); // Save FCM token
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("role", role);
                            user.put("phone", phone);
                            user.put("birthday", birthday);
                            user.put("createdAt", Timestamp.now());

                            db.collection("users").document(uid).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "Error saving user", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(SignupActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
