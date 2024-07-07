package com.example.adminpart;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class dishAdapter extends FirebaseRecyclerAdapter<model_dishes,dishAdapter.dishViewHolder> {

DatabaseReference reference;
String Child_key;
    String Parent_key;
    Context context;
    public dishAdapter(@NonNull FirebaseRecyclerOptions<model_dishes> options, String Parent_key, Context context) {
        super(options);
        this.Parent_key=Parent_key;
        this.context=context;



        assert Parent_key != null;
        reference= FirebaseDatabase.getInstance().getReference().child("categories").child(Parent_key);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull dishViewHolder holder, int i, @NonNull model_dishes modelDishes) {

        Child_key =getRef(i).getKey();
        holder.dishName.setText(modelDishes.getName());
        holder.dishPrice.setText(modelDishes.getPrice());
        holder.dishDescription.setText(modelDishes.getDescription());

        Glide.with(holder.dishImage.getContext())
                .load(modelDishes.getImageUrl())
                .into(holder.dishImage);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder change=new AlertDialog.Builder(v.getContext());
                change.setTitle("Confirm choice");
                View view=LayoutInflater.from(v.getContext()).inflate(R.layout.update_dishes,null,false);
                change.setView(view);
                EditText etPrice=view.findViewById(R.id.etPrice);
                EditText etDescription=view.findViewById(R.id.etDescrpition);
                EditText etName=view.findViewById(R.id.etName);
                change.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     reference.child("Dish").child(Child_key).removeValue()
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {
                                     Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                 }
                             });
                    }
                });
                change.show();
                return false;
            }
        });
    }

    @NonNull
    @Override
    public dishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dishes_adapter_design,parent,false);
        return new dishViewHolder(v);
    }

    public static class dishViewHolder extends RecyclerView.ViewHolder{
        ImageView dishImage;
        TextView dishName;
        TextView dishPrice;
        TextView dishDescription;
        public dishViewHolder(@NonNull View itemView) {
            super(itemView);

            dishImage=itemView.findViewById(R.id.dish_image);
            dishDescription=itemView.findViewById(R.id.dish_description);
            dishPrice=itemView.findViewById(R.id.dish_price);
            dishName=itemView.findViewById(R.id.dish_name);
        }
    }
}
