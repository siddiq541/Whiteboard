package com.example.whiteboardv0001;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public void saveMessage(String senderName, String roomName, String messageContent) {
        // These strings reference the table/document/key/value names in the database.
        String collectionName = "messages";
        String keySenderName = "sender";
        String keyMessageContent = "message_content";
        String keyRoomName = "room";

        // Assemble a key/value pair object for a message.
        Map<String, Object> message = new HashMap<>();
        message.put(keyMessageContent, messageContent);
        message.put(keySenderName, senderName);
        message.put(keyRoomName, roomName);

        // Send our key/value pair object "message" to the database
        db.collection(collectionName).document().set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Success logic here
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failure logic here
                    }
                });

    }

    // public void loadMessages(String roomName) { }


}

