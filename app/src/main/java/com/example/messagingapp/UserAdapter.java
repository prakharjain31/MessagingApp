package com.example.messagingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mContext;
    private ArrayList<FirebaseUser> firebaseUserArrayList;

    public UserAdapter(Context mContext, ArrayList<FirebaseUser> firebaseUserArrayList) {
        this.mContext = mContext;
        this.firebaseUserArrayList = firebaseUserArrayList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item , parent , false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.usernameTextView.setText(firebaseUserArrayList.get(position).getDisplayName());
    }

    @Override
    public int getItemCount() {
        return firebaseUserArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.textView);
        }
    }
}
