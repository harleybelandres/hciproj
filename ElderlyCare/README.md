# ElderlyCare Android App

An elder-friendly Android app for medical updates, role-based access, realtime updates, chat, and caregiver/doctor coordination.

## Features

- **Landing Page**: App title, tagline, Login/Sign Up buttons.
- **Sign Up**: Form with name, email, password, phone, birthday, role selection (Senior/Caretaker/Doctor).
- **Login**: Email/password with forgot password.
- **Role-based Dashboards**:
  - **Senior**: Medical updates, messages, caretakers, emergency SOS, quick links to DOH/PhilHealth/DSWD.
  - **Caretaker/Doctor**: Patients, add patient, messages, send update, notifications.
- **Realtime Medical Updates**: Caretakers/Doctors send updates, Seniors receive in realtime.
- **Chat**: 1:1 chat between linked users.
- **Notifications**: FCM for important updates.
- **Emergency Button**: SOS for Seniors to alert caretakers.
- **Accessibility**: Large fonts, high contrast, simple navigation.

## Firebase Setup

1. Create a Firebase project at https://console.firebase.google.com/.
2. Enable Authentication (Email/Password).
3. Create Firestore database (test mode).
4. Enable Cloud Messaging.
5. Add Android app: package name `com.example.elderlycare`, download `google-services.json` and place in `app/` folder.
6. Set Firestore rules (see below).

## Dependencies

Add to `app/build.gradle`:

```gradle
dependencies {
    implementation 'com.google.firebase:firebase-auth:22.3.1'
    implementation 'com.google.firebase:firebase-firestore:24.10.1'
    implementation 'com.google.firebase:firebase-messaging:23.4.1'
    // Other existing deps
}
```

Apply plugin: `apply plugin: 'com.google.gms.google-services'` at bottom.

## Firestore Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow create: if request.auth != null && request.auth.uid == userId;
      allow update: if request.auth != null && request.auth.uid == userId;
    }

    match /medical_updates/{updateId} {
      allow create: if request.auth != null && (get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role in ['doctor','caretaker']);
      allow read: if request.auth != null && (
          resource.data.authorId == request.auth.uid ||
          (exists(/databases/$(database)/documents/users/$(request.auth.uid)) &&
           (request.auth.uid in get(/databases/$(database)/documents/users/$(resource.data.patientId)).data.caretakers)) ||
          request.auth.uid == resource.data.patientId
        );
    }

    match /chats/{chatId} {
      allow read, write: if request.auth != null && (request.auth.uid in resource.data.members);
    }

    match /invites/{inviteId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## Cloud Function for Notifications (Node.js)

```javascript
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notifyOnMedicalUpdate = functions.firestore
  .document('medical_updates/{updateId}')
  .onCreate(async (snap, context) => {
    const update = snap.data();
    const patientId = update.patientId;
    const patientDoc = await admin.firestore().doc(`users/${patientId}`).get();
    const caretakers = patientDoc.data().caretakers || [];
    const payload = {
      notification: {
        title: `New update for ${patientDoc.data().name}`,
        body: update.title || "New medical update",
      },
      data: {
        type: "medical_update",
        patientId: patientId,
        updateId: context.params.updateId
      }
    };
    const tokens = [];
    for (const uid of caretakers) {
      const d = await admin.firestore().doc(`users/${uid}`).get();
      if (d.exists && d.data().fcmToken) tokens.push(d.data().fcmToken);
    }
    if (tokens.length > 0) {
      await admin.messaging().sendToDevice(tokens, payload);
    }
  });
```

## Test Accounts

- **Senior**: email: senior@example.com, password: password123
- **Caretaker**: email: caretaker@example.com, password: password123
- **Doctor**: email: doctor@example.com, password: password123

## Sample Data (Firestore JSON)

```json
{
  "users": {
    "seniorUid": {
      "name": "Juan Dela Cruz",
      "email": "senior@example.com",
      "role": "senior",
      "phone": "+63...",
      "birthday": "1950-01-01",
      "caretakers": ["caretakerUid"],
      "createdAt": "2023-01-01T00:00:00Z"
    },
    "caretakerUid": {
      "name": "Maria Santos",
      "email": "caretaker@example.com",
      "role": "caretaker",
      "phone": "+63...",
      "birthday": "1980-01-01",
      "patients": ["seniorUid"],
      "createdAt": "2023-01-01T00:00:00Z"
    }
  },
  "medical_updates": {
    "update1": {
      "patientId": "seniorUid",
      "authorId": "caretakerUid",
      "title": "Blood Pressure Check",
      "message": "BP is 120/80, normal.",
      "timestamp": "2023-01-01T00:00:00Z"
    }
  }
}
```

## Running the App

1. Clone repo.
2. Open in Android Studio.
3. Add Firebase dependencies and google-services.json.
4. Build and run on device/emulator.
5. Sign up or use test accounts.

## Folder Structure

- `app/src/main/java/com/example/elderlycare/`: Activities, Fragments, Models, Services.
- `app/src/main/res/layout/`: XML layouts.
- `app/src/main/res/values/`: Colors, themes, strings.

## Notes

- No Gradle/SDK/JDK changes made.
- App uses Java.
- Elder-friendly UI with large elements.
- Realtime via Firestore listeners.
- FCM for push notifications.
