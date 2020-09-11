package com.example.messagingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.messagingapp.GroupChatFragment.userArrayList;

public class PersonalChatActivity extends AppCompatActivity {

    public RecyclerView userRecyclerView;
    public UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);

        userRecyclerView = findViewById(R.id.user_recycler_view);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() , RecyclerView.VERTICAL , false));
        userRecyclerView.setHasFixedSize(true);
        userAdapter = new UserAdapter(getApplicationContext() , userArrayList);
        userRecyclerView.setAdapter(userAdapter);

    }
}