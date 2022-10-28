package com.projects.trending.chatify.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.trending.chatify.R;

public class ForgetPassActivity extends AppCompatActivity {

    TextView emailSendBtn ;
    EditText inputEmail ;
    FirebaseAuth auth ;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        inputEmail = findViewById(R.id.tv_forget_emailSend);
        emailSendBtn = findViewById(R.id.forget_emailBtn);
        auth = FirebaseAuth.getInstance();

        emailSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String nEmail = inputEmail.getText().toString();

                if(nEmail.isEmpty() ){
                    Toast.makeText(ForgetPassActivity.this,"Email Can't be " +
                            "Empty" ,Toast.LENGTH_SHORT).show();
                }
                else  if (!nEmail.matches(emailPattern)) {
                    inputEmail.setError("Invalid Email");
                    Toast.makeText(ForgetPassActivity.this, "Please Enter valid email address",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.sendPasswordResetEmail(nEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgetPassActivity.this, "Please Check your Email to set new Password",
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(ForgetPassActivity.this, "Email Not Found !!!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}