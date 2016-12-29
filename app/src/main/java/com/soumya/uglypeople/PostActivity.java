package com.soumya.uglypeople;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import static android.R.attr.data;
import static android.R.attr.progress;
import static android.R.attr.start;
import static android.R.attr.title;


public class PostActivity extends Activity {


    private EditText postTitle;
    private EditText postDescription;
    private Button submitButton;
    private Uri imageUri=null;
    private StorageReference storage;
    private ProgressDialog progressDialogue;
    private DatabaseReference database;
    public  String titleVal;
    public String descriptionVal;
    private ImageView mImageView;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    private static final String TAG = "MyActivity";

    private static final int GALLERY_REQUEST=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        storage= FirebaseStorage.getInstance().getReference();
        database= FirebaseDatabase.getInstance().getReference().child("Posts");
        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        //Offline data disply code STARTS
        database.keepSynced(true);
        //Offline data disply code Ends




        mImageView=(ImageView) findViewById(R.id.imageView);

        postTitle=(EditText) findViewById(R.id.titleField);
        postDescription=(EditText)findViewById(R.id.descriptionField);
        submitButton=(Button) findViewById(R.id.submitPost);
        progressDialogue=new ProgressDialog(this);


        imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setImageURI(imageUri);





//What happens after post is SUBMITED.

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("titleVal", titleVal);
        outState.putString("descriptionVal",descriptionVal );

    }


    private void startPosting() {


        titleVal=postTitle.getText().toString().trim();
        descriptionVal=postDescription.getText().toString().trim();



        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descriptionVal) && imageUri!=null){
            progressDialogue.setMessage("আপলোড হচ্ছে......");
            progressDialogue.show();

            StorageReference filePath=storage.child("post_images").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    Picasso.with(PostActivity.this).load(downloadUrl).into(mImageView);
                    final DatabaseReference newPost=database.push();


                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("title").setValue(titleVal);
                            newPost.child("description").setValue(descriptionVal);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("profilename").setValue(mCurrentUser.getDisplayName());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue());
                            


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //After Authentication done the below line of COMMENT will be uncommented
//                    newPost.child("uid").setValue(FirebaseAuth.getCurrentUser().getUid());



                    progressDialogue.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully Uploaded", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(PostActivity.this,HomeActivity.class));
                }
            });

        }else{
            Toast.makeText(getApplicationContext(), "Please provide a Title", Toast.LENGTH_LONG).show();
        }


    }

}
