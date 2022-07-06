package com.projects.trending.chatify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView tv_signUp , login_signIn ;
    EditText login_email , login_password  ;
    FirebaseAuth auth ;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_signUp = findViewById(R.id.tv_signup);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_signIn = findViewById(R.id.login_signIn) ;

        auth = FirebaseAuth.getInstance() ;

        login_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_email.getText().toString();
                String pass = login_password.getText().toString();

                // Email and Password Validation
                if(email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Email Or Password Can't be " +
                            "Empty" ,Toast.LENGTH_SHORT).show();
                }else  if (!email.matches(emailPattern)) {
                    login_email.setError("Invalid Email");
                    Toast.makeText(LoginActivity.this, "Please Enter valid email address",
                            Toast.LENGTH_SHORT).show();
                }
                else if(pass.length()<6){
                    login_password.setError("Invalid Password");
                    Toast.makeText(LoginActivity.this,"Password must be at least 6 " +
                            "characters" ,Toast.LENGTH_SHORT).show();
                }else{
                    // Firebase Auth Validation
                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Error in Login" ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }
}