package com.projects.trending.chatify.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.trending.chatify.R;
import com.projects.trending.chatify.models.Users;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView setting_image;
    EditText setting_status, setting_name;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    TextView done;
    Uri imageUri;
    String email;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setting_image = findViewById(R.id.settings_img);
        setting_status = findViewById(R.id.settings_status);
        done = findViewById(R.id.settings_done);
        setting_name = findViewById(R.id.setting_name);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        DatabaseReference databaseReference = firebaseDatabase.getReference().child("user")
                .child(firebaseAuth.getUid());
        StorageReference storageReference = firebaseStorage.getReference().child("upload")
                .child(firebaseAuth.getUid());


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String imageUri = snapshot.child("imageUri").getValue().toString();

                setting_name.setText(name);
                setting_status.setText(status);
                Picasso.get().load(imageUri).into(setting_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String _name = setting_name.getText().toString();
                String _status = setting_status.getText().toString();

                // Storing the image in Firebase Storage and getting its unique link
                // to store in Database Reference
                if (imageUri != null) {
                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                // getting the access token
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String fbImageURI = uri.toString();

                                        // Saving the User object to Firebase Database
                                        Users users = new Users(firebaseAuth.getUid(), _name, email, fbImageURI, _status);
                                        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(SettingsActivity.this,
                                                            HomeActivity.class));
                                                    Toast.makeText(SettingsActivity.this, "Data Successfully Updated"
                                                            , Toast.LENGTH_SHORT).show();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Error in Updating the User"
                                                            , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                } else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String fbImageURI = uri.toString();

                            // Saving the User object to Firebase Database
                            Users users = new Users(firebaseAuth.getUid(), _name, email, fbImageURI, _status);
                            databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(SettingsActivity.this,
                                                HomeActivity.class));
                                        Toast.makeText(SettingsActivity.this, "Data Successfully Updated"
                                                , Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Error in Updating the User"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                }


            }
        });


        setting_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });
    }


    // Taking the picture from gallery and set it on profile Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                imageUri = data.getData();
                setting_image.setImageURI(imageUri);
            }
        }
    }
}