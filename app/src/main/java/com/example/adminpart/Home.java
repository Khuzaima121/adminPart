package com.example.adminpart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Home extends AppCompatActivity {

    RecyclerView rvCategories;
    FloatingActionButton fabAdd,fabMessage;
    DatabaseReference reference;
    CatagoriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        init();

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, addCatagories.class));
        });

        fabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, order.class));
            }
        });

        loadCategories();
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();
        fabAdd = findViewById(R.id.fabAddbtn);
        fabMessage=findViewById(R.id.fabMessage);
        rvCategories = findViewById(R.id.rvCatagories);
        rvCategories.setLayoutManager(new WrapContentGridLayoutManager(this, 2));

        reference = FirebaseDatabase.getInstance().getReference().child("categories");

        FirebaseRecyclerOptions<model_catagories> options =
                new FirebaseRecyclerOptions.Builder<model_catagories>()
                        .setQuery(reference, model_catagories.class)
                        .build();

        adapter = new CatagoriesAdapter(options, this, this); // Pass 'this' as activity context
        rvCategories.setAdapter(adapter);
    }

    private void loadCategories() {
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
