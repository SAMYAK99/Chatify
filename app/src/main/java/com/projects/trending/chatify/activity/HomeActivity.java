package com.projects.trending.chatify.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.trending.chatify.MainActivity;
import com.projects.trending.chatify.R;
import com.projects.trending.chatify.adapters.UserAdapter;
import com.projects.trending.chatify.models.Users;
import com.projects.trending.chatify.notification.Client;
import com.projects.trending.chatify.utils.PreferenceData;

import java.util.ArrayList;
import java.util.Iterator;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    RecyclerView mainRecyclerView;
    UserAdapter adapter;
    ArrayList<Users> userArrayList;
    ImageView imgLogut, settingsImg;
    private long pressedTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userArrayList = new ArrayList<Users>();
        imgLogut = findViewById(R.id.img_logout);
        settingsImg = findViewById(R.id.img_settings);

        TextView noBtn, yesBtn;
        noBtn = findViewById(R.id.no_btn);
        yesBtn = findViewById(R.id.yes_btn);



//        imgLogut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Dialog dialog = new Dialog(HomeActivity.this, R.style.Dialogue);
//                dialog.setContentView(R.layout.dialog_layout);
//
//
////                yesBtn.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        firebaseAuth.signOut();
////                        PreferenceData.setUserLoggedInStatus(HomeActivity.this, false);
////                        PreferenceData.clearLoggedInUserId(HomeActivity.this);
////                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
////                    }
////                });
//
////                noBtn.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        dialog.dismiss();
////                    }
////                });
//
//                dialog.show();
//            }
//        });
        imgLogut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLogoutDialog();
            }
        });

        DatabaseReference reference = firebaseDatabase.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (!PreferenceData.getLoggedInUserUid(HomeActivity.this).equals(user.getUid()))
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
        adapter = new UserAdapter(HomeActivity.this, userArrayList);
        mainRecyclerView.setAdapter(adapter);


        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
        }


        settingsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        }

    }

    public void customLogoutDialog() {
        // creating custom dialog
        final Dialog dialog = new Dialog(HomeActivity.this);

        // setting content view to dialog
        dialog.setContentView(R.layout.dialog_layout);

        // getting reference of TextView
        TextView dialogButtonYes = (TextView) dialog.findViewById(R.id.yes_btn);
        TextView dialogButtonNo = (TextView) dialog.findViewById(R.id.no_btn);

        // click listener for No
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog
                dialog.dismiss();

            }
        });
        // click listener for Yes
        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog and exit the exit
                firebaseAuth.signOut();
                PreferenceData.setUserLoggedInStatus(HomeActivity.this, false);
                PreferenceData.clearLoggedInUserId(HomeActivity.this);
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                HomeActivity.this.finish();

            }
        });

        // show the exit dialog
        dialog.show();
    }
}