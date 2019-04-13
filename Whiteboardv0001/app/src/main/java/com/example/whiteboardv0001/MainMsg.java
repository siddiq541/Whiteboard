package com.example.whiteboardv0001;



import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.lang.reflect.Array;
import java.util.Random;

public class MainMsg extends AppCompatActivity implements RoomListener {

    // replace this with a real channelID from Scaledrone dashboard
    private String channelID = "QJuB2vugMoa06YpC";
    private String roomName = "observable-room";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private DatabaseHelper dbHelper;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_msg);

        editText = (EditText) findViewById(R.id.editText);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        MemberData data = new MemberData(getCurrentUsername(), getRandomColor());

        dbHelper = new DatabaseHelper(roomName);
        loadHistoricMessages();

        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                scaledrone.subscribe(roomName, MainMsg.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });
    }

    public void loadHistoricMessages(){
        // Somehow take the raw data from our firestore DB and populate the screen with it?
        // This method loads in raw key/value pairs from DB: dbHelper.loadMessages();
    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();
        String senderUID = FirebaseAuth.getInstance().getUid();
        String senderName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Timestamp timestamp = Timestamp.now();

        if (message.length() > 0) {
            scaledrone.publish(roomName, message);
            editText.getText().clear();

            dbHelper.saveMessage(senderUID, senderName, message, timestamp);
        }
    }

    @Override
    public void onOpen(Room room) {
        System.out.println("Connected to room");
    }

    @Override // This triggers when the connection is dropped
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
        // See scaledrone java docs for how to reconnect after connection drop.
        // https://github.com/ScaleDrone/scaledrone-java#reconnecting
    }

    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final MemberData data = mapper.treeToValue(receivedMessage.getMember().getClientData(), MemberData.class);
            boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
            final Message message = new Message(receivedMessage.getData().asText(), data, belongsToCurrentUser);
            // Update UI and send message?
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentUsername() {
        String displayName;

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            displayName = user.getDisplayName();
        } else {
            return "NoUsername";
        }

        return displayName;
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }
}

class MemberData {
    private String name;
    private String color;


    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
