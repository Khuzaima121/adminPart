package com.example.adminpart;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class CatagoriesAdapter extends FirebaseRecyclerAdapter<model_catagories, CatagoriesAdapter.adapterViewHolder>  {

    private Context context;
    private DatabaseReference categoriesRef;
    private Activity activity;
    private ImageView ivphotoDialog;
    public static CatagoriesAdapter currentAdapterInstance;

    public CatagoriesAdapter(@NonNull FirebaseRecyclerOptions<model_catagories> options, Context context, Activity activity) {
        super(options);
        this.context = context;
        this.activity = activity;
        categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");
    }

    @Override
    protected void onBindViewHolder(@NonNull adapterViewHolder holder, int position, @NonNull model_catagories modelCatagories) {
        String key = getRef(position).getKey();
        holder.tvcatagories.setText(modelCatagories.getCategory());

        Glide.with(holder.ivphoto.getContext())
                .load(modelCatagories.getImageUrl())
                .into(holder.ivphoto);

        holder.itemView.setOnClickListener(V->{
            Intent intent = new Intent(context, dishes_home.class);
            intent.putExtra("key", key);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            showUpdateDialog(key, modelCatagories);

        return false;
        });
    }

    private void showUpdateDialog(String key, model_catagories modelCatagories) {
        AlertDialog.Builder change = new AlertDialog.Builder(context);
        change.setTitle("Update Category");

        View view = LayoutInflater.from(context).inflate(R.layout.activity_add_catagories, null, false);
        change.setView(view);

        EditText etcatagories = view.findViewById(R.id.etCategory);
        ivphotoDialog = view.findViewById(R.id.ivphoto);

        etcatagories.setText(modelCatagories.getCategory());
        Glide.with(context)
                .load(modelCatagories.getImageUrl())
                .into(ivphotoDialog);

        ivphotoDialog.setOnClickListener(viewe -> {
            currentAdapterInstance = this;
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(galleryIntent, 1);

        });

        change.setPositiveButton("Update", (dialog, which) -> {
            String categories_name = etcatagories.getText().toString().trim();
            if (ivphotoDialog.getTag() != null) {
                Uri imageUri = (Uri) ivphotoDialog.getTag();
                uploadImageAndUpdateCategory(key, categories_name, imageUri);
            } else {
                updateCategoryName(key, categories_name, modelCatagories.getImageUrl());
            }
        }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoriesRef.child(key)
                        .removeValue()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Catagories Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        change.show();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            ivphotoDialog.setImageURI(selectedImageUri);
            ivphotoDialog.setTag(selectedImageUri);
        }
    }

    private void uploadImageAndUpdateCategory(String key, String categoryName, Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("category_images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            updateCategoryName(key, categoryName, imageUrl);
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateCategoryName(String key, String categoryName, String imageUrl) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("category", categoryName);
        data.put("imageUrl", imageUrl);
        categoriesRef.child(key).updateChildren(data)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Category Updated", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to update category", Toast.LENGTH_SHORT).show()
                );
    }

    @NonNull
    @Override
    public adapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_catagories, parent, false);
        return new adapterViewHolder(v);
    }

    public static class adapterViewHolder extends RecyclerView.ViewHolder {
        ImageView ivphoto;
        TextView tvcatagories;

        public adapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ivphoto = itemView.findViewById(R.id.ivlogo);
            tvcatagories = itemView.findViewById(R.id.tvcatagories);
        }
    }
}
