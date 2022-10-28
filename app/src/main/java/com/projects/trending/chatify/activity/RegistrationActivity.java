package com.projects.trending.chatify.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.trending.chatify.R;
import com.projects.trending.chatify.models.Users;
import com.projects.trending.chatify.utils.PreferenceData;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView tv_signIn  , tv_SignUp;
    EditText signUp_email , signUp_Name , signUp_Password , signUp_ConfirmPassword ;
    CircleImageView profile_Image ;
    FirebaseAuth auth ;
    Uri imageUri ;
    FirebaseDatabase firebaseDatabase ;
    FirebaseStorage firebaseStorage ;
    String fbImageURI ;
    ProgressDialog progressDialog ;
    String userStatus = "Hi there! I'm Using Chatify";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        profile_Image = findViewById(R.id.profile_image);
        signUp_Name = findViewById(R.id.signUp_name);
        signUp_email = findViewById(R.id.signUp_email);
        signUp_Password = findViewById(R.id.signUp_password);
        signUp_ConfirmPassword = findViewById(R.id.signUp_ConfirmPassword);
        tv_SignUp= findViewById(R.id.tv_signUp);
        tv_signIn = findViewById(R.id.tv_signup);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        tv_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
              String email = signUp_email.getText().toString();
              String pass = signUp_Password.getText().toString();
              String name = signUp_Name.getText().toString();
              String confirmPass = signUp_ConfirmPassword.getText().toString() ;

              // name , email and password validation
              if(TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(pass)
              ||TextUtils.isEmpty(confirmPass)){
                  progressDialog.dismiss();
                  Toast.makeText(RegistrationActivity.this,"Please enter valid data" ,
                          Toast.LENGTH_SHORT).show();

              }else  if (!email.matches(emailPattern)) {
                  progressDialog.dismiss();
                  signUp_email.setError("Invalid Email");
                  Toast.makeText(RegistrationActivity.this, "Please Enter valid email address",
                          Toast.LENGTH_SHORT).show();
              }

              else if(pass.length()<6){
                  progressDialog.dismiss();
                  signUp_Password.setError("Invalid Password");
                  Toast.makeText(RegistrationActivity.this,"Password must be at least 6 " +
                          "characters" ,Toast.LENGTH_SHORT).show();
              }
              else if (!pass.equals(confirmPass)){
                  progressDialog.dismiss();
                  Toast.makeText(RegistrationActivity.this,"Password and Confirm Password should be same" +
                          "characters" ,Toast.LENGTH_SHORT).show();
              }
              else{
                  auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()){

                              // Referencing objects for storing name , email and password
                              DatabaseReference databaseReference = firebaseDatabase.getReference().child("user")
                                      .child(auth.getUid());
                              StorageReference storageReference = firebaseStorage.getReference().child("upload")
                                      .child(auth.getUid());

                              // Storing the image in Firebase Storage and getting its unique link
                              // to store in Database Reference
                              if(imageUri != null){

                                  storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                      @Override
                                      public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                          if(task.isSuccessful()){
                                              storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                  @Override
                                                  public void onSuccess(Uri uri) {
                                                      fbImageURI = uri.toString() ;

                                                      // Saving the User object to Firebase Database
                                                      Users users = new Users(auth.getUid(),name,email,fbImageURI,userStatus);
                                                      databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                PreferenceData.setUserLoggedInStatus(RegistrationActivity.this,true);
                                                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
                                                                PreferenceData.setLoggedInUserUid(RegistrationActivity.this,uid);
                                                                progressDialog.dismiss();
                                                                startActivity(new Intent(RegistrationActivity.this,
                                                                        HomeActivity.class));
                                                            }
                                                            else{
                                                                progressDialog.dismiss();
                                                                Toast.makeText(RegistrationActivity.this,"Error in Creating the User"
                                                                        ,Toast.LENGTH_SHORT).show();
                                                            }
                                                          }
                                                      });
                                                  }
                                              });
                                          }
                                      }
                                  });
                              }
                              else{
                                  fbImageURI = "https://firebasestorage.googleapis.com/v0/b/playstoreapps-e9008.appspot.com/o/profile_image.png?alt=media&token=1ce6082d-bbf7-4aa5-8ae3-26cf4813e03c" ;

                                  // Saving the User object to Firebase Database
                                  Users users = new Users(auth.getUid(),name,email,fbImageURI,userStatus);
                                  databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful()) {
                                              PreferenceData.setUserLoggedInStatus(RegistrationActivity.this,true);
                                              String uid = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
                                              PreferenceData.setLoggedInUserUid(RegistrationActivity.this,uid);
                                              progressDialog.dismiss();
                                              startActivity(new Intent(RegistrationActivity.this,
                                                      HomeActivity.class));
                                          }
                                          else{
                                              progressDialog.dismiss();
                                              Toast.makeText(RegistrationActivity.this,"Error in Creating the User"
                                                      ,Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  });
                              }

                              Toast.makeText(RegistrationActivity.this,"User Created Successfully" ,
                                      Toast.LENGTH_SHORT).show();

                          }else{
                              Toast.makeText(RegistrationActivity.this,"Something went Wrong! Please" +
                                              "Try again later" , Toast.LENGTH_SHORT).show();
                              progressDialog.dismiss();
                          }
                      }
                  });

              }
              }
        });


        // Taking the Image from Image Picker Library
        profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });
    }

    // Taking the picture from gallery and set it on profile Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            if(data != null){
                imageUri = data.getData() ;
                profile_Image.setImageURI(imageUri);
            }
        }
    }
}