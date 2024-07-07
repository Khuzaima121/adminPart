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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class addDishes extends AppCompatActivity {

    ImageView ivPhoto;
    EditText etName, etPrice, etDescription;
    Button btnAdd;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference reference;
    StorageReference storageReference;

    final int IMAGE_CODE = 1;
    Uri selectedImageUri;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_dishes);
        init();
        key = getIntent().getStringExtra("key");

        ivPhoto.setOnClickListener(v -> {
            Intent PickImage = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(PickImage, IMAGE_CODE);
        });

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Please add the category");
                return;
            }
            if (price.isEmpty()) {
                etPrice.setError("Please add the category");
                return;
            }
            if (description.isEmpty()) {
                etDescription.setError("Please add the category");
                return;
            }
            uploadImageAndSaveData(name, price, description);
        });
    }

    private void uploadImageAndSaveData(String name, String price, String description) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference imageRef = storageReference.child("images/" + user.getUid() + "/" + name + ".jpg");
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
                    item.put("name", name);
                    item.put("price", price);
                    item.put("description", description);
                    reference.child("categories").child(key).child("Dish").push().setValue(item);
                    Toast.makeText(addDishes.this, "Dish added successfully", Toast.LENGTH_SHORT).show();
                    moveToDishHome();
                } else {
                    Toast.makeText(addDishes.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(addDishes.this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivPhoto.setImageURI(selectedImageUri);
        }
    }

    private void moveToDishHome() {
        Intent intent = new Intent(addDishes.this, dishes_home.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();
        ivPhoto = findViewById(R.id.ivphoto);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescrpition);
        btnAdd = findViewById(R.id.btnadd);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }
}
