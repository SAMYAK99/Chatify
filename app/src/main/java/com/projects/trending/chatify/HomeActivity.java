package com.projects.trending.chatify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance() ;

        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(HomeActivity.this ,RegistrationActivity.class));
        }
    }
}