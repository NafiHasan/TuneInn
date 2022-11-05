package com.example.tuneinn.profilePackage;

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

import com.example.tuneinn.HomeBNB;
import com.example.tuneinn.ProfileFragment;
import com.example.tuneinn.R;
import com.example.tuneinn.User;
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
    private Button uploadImageButton, saveProfileButton;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private String name, email, genre, url;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {

                @Override
                public void onActivityResult(Uri uri) {
                    if(uri != null){
                        profileImageView.setImageURI(uri);
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

        Ref.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();

            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
            task.addOnSuccessListener(uri1 -> url = uri1.toString());
            Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Failed to Upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setMessage("Progress : " + (int)progressPercent + "%");
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // set title of the page
        setTitle("Edit Your Profile");

        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
        saveProfileButton = (Button) findViewById(R.id.addFriendButton);

        profileImageView = (CircleImageView) findViewById(R.id.profileImage);
        editUserName = findViewById(R.id.editUserName);
        editEmail = findViewById(R.id.editEmail);
        editGenre = findViewById(R.id.editGenre);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
//        mUserRef = FirebaseDatabase.getInstance().getReference().child("Image");
//        Uid = mUser.getUid();


        name = HomeBNB.currentUser.getName();
        email = HomeBNB.currentUser.getEmail();
        genre = HomeBNB.currentUser.getGenre();
        url = HomeBNB.currentUser.getUrl();

        if(name != null){
            editUserName.setText(name);
            editEmail.setText(email);
            editGenre.setText(genre);
            if(url != null && !url.equals(""))Picasso.get().load(url).into(profileImageView);
//            Picasso.get().setLoggingEnabled(true);
        }
        else {
            System.out.println("failure error");
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        uploadImageButton.setOnClickListener(view -> mGetContent.launch("image/*"));

        saveProfileButton.setOnClickListener(view -> {
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
                        genre = userProfile.genre;
                        url = userProfile.URL;

                        Toast.makeText(view.getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Intent intent = new Intent(editProfileActivity.this, ProfileActivity.class);
            HomeBNB.currentUser.setName(name);
            HomeBNB.currentUser.setEmail(email);
            HomeBNB.currentUser.setGenre(genre);
            HomeBNB.currentUser.setUrl(url);
            startActivity(intent);
            finish();
        });
    }
}