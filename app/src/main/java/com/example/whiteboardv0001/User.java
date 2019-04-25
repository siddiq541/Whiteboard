package com.example.whiteboardv0001;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {
    private DatabaseReference mFirebaseDatabaseReference;
    public String email;
    public String role;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.email = email;
        this.role = role;
    }
    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
}
