package com.example.whiteboardv0001;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class DatabaseHelper {
    // Reference to the currently logged in user (used to access name/email/id etc)
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // References to our database:
    // -Whole Database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // -Messages:
    static private final String MESSAGES_COLLECTION = "messages";
    static private final String SENDER_NAME_KEY = "sender";
    static private final String MESSAGE_CONTENT_KEY = "message_content";
    static private final String ROOM_NAME_KEY = "room";
    CollectionReference messagesRef = db.collection(MESSAGES_COLLECTION);

    public void saveMessage(String senderName, String roomName, String messageContent) {
        // Assemble a key/value pair object for a message.
        Map<String, Object> message = new HashMap<>();
        message.put(MESSAGE_CONTENT_KEY, messageContent);
        message.put(SENDER_NAME_KEY, senderName);
        message.put(ROOM_NAME_KEY, roomName);

        // Send our key/value pair object "message" to the database.
        messagesRef.document().set(message)
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

    public void loadMessages(String roomName) {
        // Query the database for all messages within database where the room name matches our own
        messagesRef
                .whereEqualTo(ROOM_NAME_KEY, roomName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


}

