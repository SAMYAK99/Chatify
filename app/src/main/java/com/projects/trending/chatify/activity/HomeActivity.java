package com.projects.trending.chatify.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.trending.chatify.R;
import com.projects.trending.chatify.adapters.UserAdapter;
import com.projects.trending.chatify.models.Users;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth ;
    FirebaseDatabase firebaseDatabase ;
    RecyclerView mainRecyclerView ;
    UserAdapter adapter ;
    ArrayList<Users> userArrayList ;
    ImageView imgLogut ;
    TextView noBtn ,yesBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance() ;
        firebaseDatabase = FirebaseDatabase.getInstance() ;
        userArrayList = new ArrayList<Users>();
        imgLogut = findViewById(R.id.img_logout);



        imgLogut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.dialog_layout);

                noBtn = findViewById(R.id.no_btn);
                yesBtn = findViewById(R.id.yes_btn);

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    }
                });

                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        DatabaseReference reference = firebaseDatabase.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users user = dataSnapshot.getValue(Users.class);
                    userArrayList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mainRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(HomeActivity.this,userArrayList);
        mainRecyclerView.setAdapter(adapter);


        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(HomeActivity.this ,RegistrationActivity.class));
        }
    }
}