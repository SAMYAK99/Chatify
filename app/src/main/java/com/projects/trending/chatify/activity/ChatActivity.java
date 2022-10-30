package com.projects.trending.chatify.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.projects.trending.chatify.R;
import com.projects.trending.chatify.adapters.MessageAdapter;
import com.projects.trending.chatify.models.Messages;
import com.projects.trending.chatify.notification.Client;
import com.projects.trending.chatify.notification.Data;
import com.projects.trending.chatify.notification.MyResponse;
import com.projects.trending.chatify.notification.Sender;
import com.projects.trending.chatify.notification.Token;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String reciverImg , reciverUID , reciverName , senderUid ;
    TextView reciverNAME ;
    RecyclerView recyclerViewMessages ;
    CircleImageView reciverIMG ;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase ;
    Intent intent ;

    DatabaseReference dbReference ;
    // For getting a particular sender name
    DatabaseReference mref;


    // We are using these images in adapter
    public  static String sImage ;
    public  static String rImage ;

    CardView sendBtn ;
    EditText sendMessage ;

    ArrayList<Messages> messagesArrayList  =  new ArrayList<>();

    String senderRoom , reciverRoom ;
    String senderName ;

    APIService apiService;
    FirebaseUser fuser;
    boolean notify = false;
    String userid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        intent = getIntent();
        userid = intent.getStringExtra("userid");
//        scrollView = findViewById(R.id.scroll);

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
        senderUid = firebaseAuth.getUid();

        // Creating chat rooms for sender and reciver
        senderRoom = senderUid + reciverUID;
        reciverRoom = reciverUID + senderUid;

        MessageAdapter messageAdapter ;
        messageAdapter = new MessageAdapter(ChatActivity.this,messagesArrayList) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // for printing the message in reverse format
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(linearLayoutManager);

        // setting the adapter into layout
        recyclerViewMessages.setAdapter(messageAdapter);
//        recyclerViewMessages.smoothScrollToPosition(messageAdapter.getItemCount());

        dbReference = firebaseDatabase.getReference().child("user").child(senderUid);
        DatabaseReference chatReference = firebaseDatabase.getReference().child("chats").child(senderRoom)
                .child("messages");


        // Getting the chat references for displaying
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clearing array list so it doesn't repeat itself
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messages message = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // For getting the name of sender  : Push Notification
        senderName = "User" ;
        mref = FirebaseDatabase.getInstance().getReference("user").child(senderUid).child("name");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                senderName = dataSnapshot.getValue(String.class);
                //do what you want with the likes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Saving the data
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("imageUri").getValue().toString();
                rImage = reciverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = sendMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please enter any message", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage.setText("");
                Date date = new Date();
                Messages messages = new Messages(message, senderUid, date.getTime());

                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    sendNotifiaction(reciverUID, senderName, message);
//                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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

    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.profile_image, username+": "+message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            //Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentUser("none");
    }


}