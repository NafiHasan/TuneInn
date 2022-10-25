package com.example.tuneinn;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.UUID;

public class editProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView editUserName, editEmail, editPassword, editGenre;
    private Button goBackButton, uploadImageButton, saveProfileButton;
    private FirebaseStorage storage;
    private StorageReference storageReference;
//    DatabaseReference mUserRef;
//    FirebaseAuth mAuth;
//    FirebaseUser mUser;
    private String filepath, downloadURL, Uid;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {

                @Override
                public void onActivityResult(Uri uri) {
                    if(uri != null){
                        profileImageView.setImageURI(uri);
                        HomeActivity.imageURL = uri.toString();
                        downloadURL = uri.toString();
                        uploadImagetoDB(uri);
                    }
                }
            });

    // uploading image to firebase cloud storage
    private void uploadImagetoDB(Uri uri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference Ref = storageReference.child("image/" + randomKey);

        Ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
//                String id = mUserRef.push().getKey();
//                mUserRef.child(id).setValue(uri.toString());
                Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to Upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Progress : " + (int)progressPercent + "%");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        goBackButton = (Button) findViewById(R.id.goBackButton);
        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
        saveProfileButton = (Button) findViewById(R.id.saveProfileButton);

        profileImageView = findViewById(R.id.profileImageView);
        editUserName = findViewById(R.id.editUserName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editGenre = findViewById(R.id.editGenre);

//        mAuth = FirebaseAuth.getInstance();
//        mUser = mAuth.getCurrentUser();
//        mUserRef = FirebaseDatabase.getInstance().getReference().child("Image");
//        Uid = mUser.getUid();


        if(HomeActivity.userName != null){
            editUserName.setText(HomeActivity.userName);
            editEmail.setText(HomeActivity.userEmail);
            editPassword.setText(HomeActivity.userPassword);
            editGenre.setText(HomeActivity.favGenre);
            Picasso.get().load(HomeActivity.imageURL).into(profileImageView);
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(editProfileActivity.this, ProfileActivity.class));
                finish();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.updateProfile(editUserName.getText().toString(), editEmail.getText().toString(),
                        editPassword.getText().toString(), editGenre.getText().toString(), downloadURL);


                if(HomeActivity.done == true){
                    Toast.makeText(view.getContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(editProfileActivity.this, ProfileActivity.class));
//                    finish();
                }
            }
        });
    }
}