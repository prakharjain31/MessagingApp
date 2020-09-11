package com.example.messagingapp;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.messagingapp.GroupChatFragment.mChatPhotoStorageReference;

public class ImageViewerFragment extends Fragment {
    public ImageViewerFragment() {
        // Required empty public constructor
    }


    public RecyclerView recyclerView;
    public ImageAdapter imageAdapter;
    public static ArrayList<String> imagesUrlList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        recyclerView = view.findViewById(R.id.images_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, true);
        gridLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(gridLayoutManager);
//      TODO : Add all image urls from storage to list
        mChatPhotoStorageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> list = listResult.getItems();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (imagesUrlList.contains(uri.toString())) {

                            } else {
                                imagesUrlList.add(uri.toString());
                            }
                        }
                    });
                }
            }
        });
        imageAdapter = new ImageAdapter(getContext(), imagesUrlList);
        recyclerView.setAdapter(imageAdapter);

        recyclerView.scrollToPosition(0);

        return view;
    }
}