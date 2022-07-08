package com.projects.trending.chatify.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.trending.chatify.R;
import com.projects.trending.chatify.adapters.MessageAdapter;
import com.projects.trending.chatify.models.Messages;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String reciverImg , reciverUID , reciverName , senderUid ;
    TextView reciverNAME ;
    RecyclerView recyclerViewMessages ;
    CircleImageView reciverIMG ;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase ;

    // We are using these images in adapter
    public  static String sImage ;
    public  static String rImage ;

    CardView sendBtn ;
    EditText sendMessage ;

    ArrayList<Messages> messagesArrayList ;

    String senderRoom , reciverRoom ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        reciverImg = getIntent().getStringExtra("ReciverImage");
        reciverUID = getIntent().getStringExtra("uid");
        reciverName = getIntent().getStringExtra("name");

        reciverNAME = findViewById(R.id.reciverName);
        reciverIMG = findViewById(R.id.reciverImg);
        reciverNAME.setText(reciverName);
        Picasso.get().load(reciverImg).into(reciverIMG);
        recyclerViewMessages = findViewById(R.id.recycle_MessageAdapter);

        sendBtn = findViewById(R.id.send_btn);
        sendMessage = findViewById(R.id.et_message);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        senderUid = firebaseAuth.getUid() ;

        // Creating chat rooms for sender and reciver
        senderRoom = senderUid + reciverUID ;
        reciverRoom = reciverUID + senderUid ;

        messagesArrayList = new ArrayList<>();

        MessageAdapter messageAdapter ;
        messageAdapter = new MessageAdapter(ChatActivity.this,messagesArrayList) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // for printing the message in reverse format
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(linearLayoutManager);

        // setting the adapter into layout
        recyclerViewMessages.setAdapter(messageAdapter);

        DatabaseReference dbReference =  firebaseDatabase.getReference().child("user").child(senderUid);
        DatabaseReference chatReference =  firebaseDatabase.getReference().child("chats").child(senderRoom)
                .child("messages");


        // Getting the chat references for displaying
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                   Messages message = dataSnapshot.getValue(Messages.class);
                     messagesArrayList.add(message);
               }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


         // Saving the data
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("imageUri").getValue().toString() ;
                rImage = reciverImg ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = sendMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this,"Please enter any message",Toast.LENGTH_SHORT).show();
                     return;
                }
                sendMessage.setText("");
                Date date = new Date();
                Messages messages = new Messages(message,senderUid,date.getTime());

                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     firebaseDatabase.getReference().child("chats")
                                             .child(reciverRoom)
                                             .child("messages")
                                             .push()
                                             .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {

                                         }
                                     });
                                 }
                            }
                        });
            }
        });



    }
}