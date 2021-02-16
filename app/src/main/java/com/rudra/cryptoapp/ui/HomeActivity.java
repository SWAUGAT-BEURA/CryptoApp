package com.rudra.cryptoapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rudra.cryptoapp.R;
import com.rudra.cryptoapp.adapter.RecyclerAdapter;
import com.rudra.cryptoapp.auth.Login;
import com.rudra.cryptoapp.models.Trade;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private TextView username;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuthhome;
    private SharedPreferences prefManager;
    private SharedPreferences.Editor editor;

    private RecyclerView recTrade;
    private DatabaseReference reference;
    private ArrayList<Trade> tradeList;
    private RecyclerAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        username=findViewById(R.id.username_display);

        //Authentication
        prefManager = getApplicationContext().getSharedPreferences("LOGIN", MODE_PRIVATE);
        editor = prefManager.edit();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            username.setText(user.getDisplayName());
        }

        //Firebase database
        recTrade = findViewById(R.id.rec_trade);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recTrade.setLayoutManager(layoutManager);

        reference = FirebaseDatabase.getInstance().getReference();
        tradeList = new ArrayList<>();

        //Clear Arraylist
        clearAll();

        //Getting Data From Firebase
        getDataFromFirebase();
    }

    private void getDataFromFirebase() {

        Query query = reference.child("invest");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Trade trade = new Trade();

                        trade.setName(snapshot.child("name").getValue().toString());
                        trade.setTime(snapshot.child("updated at").getValue().toString());
                        trade.setEntryPoint(snapshot.child("entry point").getValue().toString());

                        tradeList.add(trade);
                }
                recyclerAdapter = new RecyclerAdapter(getApplicationContext(),tradeList);
                recTrade.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearAll(){
        if(tradeList!=null){
            tradeList.clear();

            if (recyclerAdapter!=null){
                recyclerAdapter.notifyDataSetChanged();
            }
        }
        tradeList = new ArrayList<>();

    }

    public void onlogoutclicked(View view) {
        new AlertDialog.Builder(HomeActivity.this).setTitle("Alert")
                .setMessage("Are you sure you want to Logout")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.putBoolean("ISLOGGEDIN", false);

                        editor.apply();
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(HomeActivity.this, Login.class));
                        finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();

    }
}