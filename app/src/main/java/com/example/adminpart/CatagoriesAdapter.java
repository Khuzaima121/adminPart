package com.example.adminpart;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CatagoriesAdapter extends FirebaseRecyclerAdapter<model_catagories, CatagoriesAdapter.adapterViewHolder> {

    public CatagoriesAdapter(@NonNull FirebaseRecyclerOptions<model_catagories> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull adapterViewHolder holder, int position, @NonNull model_catagories modelCatagories) {
        holder.tvcatagories.setText(modelCatagories.getCategory());

        Glide.with(holder.ivphoto.getContext())
                .load(modelCatagories.getImageUrl())
                .into(holder.ivphoto);
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
