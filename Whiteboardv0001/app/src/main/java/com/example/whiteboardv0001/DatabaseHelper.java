package com.example.whiteboardv0001;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class DatabaseHelper {
    // References to our database:
    // -Whole Database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // -Rooms:
    static private final String ROOMS_COLLECTION = "rooms";
    private String currentRoom;
    // -Messages:
    static private final String MESSAGES_COLLECTION = "messages";
    static private final String SENDER_NAME_KEY = "sender_name";
    static private final String SENDER_UID_KEY = "sender_uid";
    static private final String TIMESTAMP_KEY = "timestamp";
    static private final String MESSAGE_CONTENT_KEY = "message_content";
    CollectionReference messagesColRef;

    // Constructor
    public DatabaseHelper(String newCurrentRoom){
        firebaseTimestampFix();
        currentRoom = newCurrentRoom;
        updateDatabaseRefs();
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String newCurrentRoom) {
        currentRoom = newCurrentRoom;
        updateDatabaseRefs();
    }

    private void firebaseTimestampFix() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    private void updateDatabaseRefs() {
        messagesColRef = db.collection(ROOMS_COLLECTION).document(currentRoom).collection(MESSAGES_COLLECTION);
    }

    public void saveMessage(String senderUID, String senderName, String messageContent, Timestamp timestamp) {
        // Assemble a key/value pair object for a message.
        Map<String, Object> message = new HashMap<>();
        message.put(SENDER_UID_KEY, senderUID);
        message.put(SENDER_NAME_KEY, senderName);
        message.put(MESSAGE_CONTENT_KEY, messageContent);
        // Timestamp not used (buggy)

        // Send our key/value pair object "message" to the database.
        messagesColRef.document()
                .set(message)
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

    // Grab our room messages, convert them to a hashmap, then return them all in an array.
    public ArrayList<HashMap> loadMessages() {
        final ArrayList<HashMap> messagesArray = new ArrayList<>();
        // Database query
        messagesColRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Loop through each message from the room
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Place database values into a hashmap
                                HashMap<String, String> message = new HashMap<>();
                                message.put("senderUID", document.getString(SENDER_UID_KEY));
                                message.put("senderName", document.getString(SENDER_NAME_KEY));
                                message.put("messageContent", document.getString(MESSAGE_CONTENT_KEY));

                                messagesArray.add(message);
                                Log.d(TAG, "senderName: " + message.get("senderName"));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return messagesArray;
    }


}

