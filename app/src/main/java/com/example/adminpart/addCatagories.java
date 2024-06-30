package com.example.adminpart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class addCatagories extends AppCompatActivity {

    ImageView ivphoto;
    EditText etCatagories;
    Button btnadd;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference reference;
    StorageReference storageReference;

    final int IMAGE_CODE = 1;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_catagories);

        init();

        ivphoto.setOnClickListener(v -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImage, IMAGE_CODE);
        });

        btnadd.setOnClickListener(v -> {
            String categories = etCatagories.getText().toString().trim();
            if (categories.isEmpty()) {
                etCatagories.setError("Please add the category");
                return;
            }

            uploadImageAndSaveData(categories);
        });
    }

    private void uploadImageAndSaveData(String category) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String cat = category;
        StorageReference imageRef = storageReference.child("images/" + user.getUid() +"/" +cat +".jpg");

        UploadTask uploadTask = imageRef.putFile(selectedImageUri);
        uploadTask.continueWithTask(task -> {

            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                if (downloadUri != null) {
                    String imageUrl = downloadUri.toString();
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("imageUrl", imageUrl);
                    item.put("category", cat);
                    reference.child("categories").push().setValue(item);
                    Toast.makeText(addCatagories.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    moveToHome();
                } else {
                    Toast.makeText(addCatagories.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(addCatagories.this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveToHome() {
        startActivity(new Intent(addCatagories.this, Home.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivphoto.setImageURI(selectedImageUri);
        }
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        ivphoto = findViewById(R.id.ivphoto);
        etCatagories = findViewById(R.id.etCategory);
        btnadd = findViewById(R.id.btnadd);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }
}
