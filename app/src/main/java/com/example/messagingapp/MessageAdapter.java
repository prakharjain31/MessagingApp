package com.example.messagingapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private ArrayList<FriendlyMessage> friendlyMessageArrayList;
    private Context mContext;

    public MessageAdapter(ArrayList<FriendlyMessage> friendlyMessageArrayList, Context mContext) {
        this.friendlyMessageArrayList = friendlyMessageArrayList;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message , parent  , false) ;
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        FriendlyMessage message = friendlyMessageArrayList.get(position);

        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            holder.messageTextView.setVisibility(View.GONE);
            holder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(holder.photoImageView);
        } else {
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.photoImageView.setVisibility(View.GONE);
            holder.messageTextView.setText(message.getText());
        }
        holder.authorTextView.setText(message.getName());





    }

    @Override
    public int getItemCount() {
        return friendlyMessageArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView messageTextView;
        TextView authorTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             photoImageView =  itemView.findViewById(R.id.photoImageView);
             messageTextView = itemView.findViewById(R.id.messageTextView);
             authorTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
