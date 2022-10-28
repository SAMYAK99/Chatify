package com.projects.trending.chatify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.projects.trending.chatify.activity.HomeActivity;
import com.projects.trending.chatify.activity.LoginActivity;
import com.projects.trending.chatify.utils.PreferenceData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if(PreferenceData.getLoggedInUserUid(MainActivity.this).isEmpty()) {
                    Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(mainIntent);
                }else{
                    Intent mainIntent = new Intent(MainActivity.this, HomeActivity.class);
                    MainActivity.this.startActivity(mainIntent);
                }
                MainActivity.this.finish();
            }
        }, 3000);
    }
}