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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class editProfileActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private TextView editUserName, editEmail, editPassword, editGenre;
    private Button goBackButton, uploadImageButton, saveProfileButton;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;

//    private String filepath, downloadURL, Uid;
    private String name, email, genre, url;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {

                @Override
                public void onActivityResult(Uri uri) {
                    if(uri != null){
                        profileImageView.setImageURI(uri);
//                        HomeActivity.imageURL = uri.toString();
//                        url = uri.toString();
//                        downloadURL = uri.toString();
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

                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url = uri.toString();
                    }
                });
                System.out.println("OHNO : "+ url);
//                }
//                else{
//                    System.out.println("OHNO");
//                }
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
        saveProfileButton = (Button) findViewById(R.id.addFriendButton);

        profileImageView = (CircleImageView) findViewById(R.id.profileImage);
        editUserName = findViewById(R.id.editUserName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editGenre = findViewById(R.id.editGenre);

//        downloadURL = HomeActivity.imageURL;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
//        mUserRef = FirebaseDatabase.getInstance().getReference().child("Image");
//        Uid = mUser.getUid();


        //getting info from previous intent
        Intent data = getIntent();
        name = data.getStringExtra("name");
        email = data.getStringExtra("email");
        genre = data.getStringExtra("genre");
        url = data.getStringExtra("url");

        if(name != null){
            editUserName.setText(name);
            editEmail.setText(email);
//            editPassword.setText(HomeActivity.userPassword);
            editGenre.setText(genre);
            if(url != null && !url.equals(""))Picasso.get().load(url).into(profileImageView);
//            Picasso.get().setLoggingEnabled(true);
        }
        else {
            System.out.println("failure error");
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(editProfileActivity.this, ProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("genre", genre);
                intent.putExtra("url", url);
                startActivity(intent);
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
                name = editUserName.getText().toString();
                email = editEmail.getText().toString();
                genre = editGenre.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                String userID = user.getUid();

                User userProfile = new User();
                userProfile.updateUser(name, email, "", genre, url);

                reference.child(userID).setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            name = userProfile.name;
                            email = userProfile.email;
//                            userPassword = userProfile.password;
                            genre = userProfile.genre;
                            url = userProfile.URL;

                            Toast.makeText(view.getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Intent intent = new Intent(editProfileActivity.this, ProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("genre", genre);
                intent.putExtra("url", url);
                startActivity(intent);
                finish();
            }
        });
    }
}