package com.example.messagingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.messagingapp.ImageViewerFragment.imagesUrlList;

public class GroupChatFragment extends Fragment {

    public GroupChatFragment() {
        // Required empty public constructor
    }

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    private static final String MSG_LENGTH_KEY = "msg_length";

    private RecyclerView mMessageRecyclerView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUsername;
    private ArrayList<FriendlyMessage> friendlyMessageArrayList;
    public static ArrayList<FirebaseUser> userArrayList;
    private LinearLayoutManager linearLayoutManager;

    // Firebase variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private DatabaseReference userDatabaseReference;

    // Authentication variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // storage variables
    private FirebaseStorage mFirebaseStorage;
    public static StorageReference mChatPhotoStorageReference;

    // remote-config
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        mUsername = ANONYMOUS;

//        firebaseMessaging = FirebaseMessaging.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        /*qqq*/ userDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mChatPhotoStorageReference = mFirebaseStorage.getReference().child("chat_photos");
//
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

        mProgressBar = view.findViewById(R.id.progressBar);
        mMessageRecyclerView =  view.findViewById(R.id.messageListView);
        mPhotoPickerButton = view.findViewById(R.id.photoPickerButton);
        mMessageEditText = view.findViewById(R.id.messageEditText);
        mSendButton = view.findViewById(R.id.sendButton);

        friendlyMessageArrayList = new ArrayList<>();
        userArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext() , RecyclerView.VERTICAL , false);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(linearLayoutManager);
        mMessageAdapter = new MessageAdapter(friendlyMessageArrayList, getContext());
        mMessageRecyclerView.setAdapter(mMessageAdapter);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows  image picker to upload image
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY , true);
                startActivityForResult(Intent.createChooser(intent , "Complete action using") , RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString() , mUsername , null);
                mMessagesDatabaseReference.push().setValue(friendlyMessage);

                mMessageEditText.setText("");
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // user is signed in
                    onSignedInInitialise(firebaseUser.getDisplayName());
                    if (!userArrayList.contains(FirebaseAuth.getInstance().getCurrentUser())) {
                        userArrayList.add(FirebaseAuth.getInstance().getCurrentUser());
                    }
                } else {
                    // user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        Map<String , Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(MSG_LENGTH_KEY , DEFAULT_MSG_LENGTH_LIMIT);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultConfigMap);
        fetchConfig();



        return view;
    }

    private void fetchConfig() {
        long cacheExpiration = 3600;

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseRemoteConfig.activate();
                        applyLength();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                applyLength();

            }
        });

    }

    private void applyLength() {
        Long msg_length = mFirebaseRemoteConfig.getLong(MSG_LENGTH_KEY);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter((msg_length).intValue())});


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext() , "WELCOME!!!" , Toast.LENGTH_SHORT).show();
                if (!userArrayList.contains(FirebaseAuth.getInstance().getCurrentUser())) {
                    userArrayList.add(FirebaseAuth.getInstance().getCurrentUser());
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext() , "noooos" , Toast.LENGTH_SHORT).show();
//                finish();
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            Uri selectedImageUri = data.getData();
//            ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
//            Bundle args = new Bundle();
//            MainActivity.lastPathSegment = selectedImageUri.getLastPathSegment();
//            Log.e("qwerty" , MainActivity.lastPathSegment + "");
//            args.putString("path" , lastPathSegment);
//            imageViewerFragment.setArguments(args);
            final StorageReference photoref =
                    mChatPhotoStorageReference.child(selectedImageUri.getLastPathSegment());
            photoref.putFile(selectedImageUri).addOnSuccessListener
                    (getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            photoref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                    imagesUrlList.add(uri.toString());
                                    FriendlyMessage friendlyMessage = (new FriendlyMessage(null, mUsername, uri.toString()));
                                    mMessagesDatabaseReference.push().setValue(friendlyMessage);
                                }
                            });


                        }
                    });

        }
    }
    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        friendlyMessageArrayList.clear();
        detachDatabaseReadListener();

    }

    private void onSignedInInitialise(String displayName) {
        mUsername = displayName;
        User user = new User(mFirebaseAuth.getCurrentUser().getUid() , mUsername);
        userDatabaseReference.push().setValue(user);
        attachDatabaseReadListener();

    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    FriendlyMessage friendlyMessage = snapshot.getValue(FriendlyMessage.class);
                    friendlyMessageArrayList.add(friendlyMessage);

                    LayoutAnimationController layoutAnimation = AnimationUtils.loadLayoutAnimation(getContext() , R.anim.layout_animation);
                    Animation.AnimationListener layoutAnimationListener = new Animation.AnimationListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onAnimationStart(Animation animation) {
                            for (int i = 0 ; i < friendlyMessageArrayList.size() - 1 ; i++) {
                                try {
                                    Objects.requireNonNull(mMessageRecyclerView.findViewHolderForLayoutPosition(i)).itemView.clearAnimation();
                                } catch (Exception ignored) {

                                }
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    } ;
                    mMessageRecyclerView.setLayoutAnimation(layoutAnimation);
                    mMessageRecyclerView.setLayoutAnimationListener(layoutAnimationListener);
                    mMessageAdapter.notifyDataSetChanged();
                    mMessageRecyclerView.scheduleLayoutAnimation();


                    mMessageRecyclerView.scrollToPosition(friendlyMessageArrayList.size() - 1);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
        friendlyMessageArrayList.clear();
    }
}
