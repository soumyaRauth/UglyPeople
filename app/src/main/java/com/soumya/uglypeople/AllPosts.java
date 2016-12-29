package com.soumya.uglypeople;

/**
 * Created by Soumya on 12/5/2016.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import static android.R.id.list;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.squareup.picasso.Picasso.with;


public class AllPosts extends Fragment{


    private RecyclerView postList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseAppriciate;
    private LinearLayoutManager layoutManager;
    private FirebaseAuth mAuth;
    //private static final String TAG = "MyActivity";

    private boolean mProcessAppriciate=false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        super.onSaveInstanceState(savedInstanceState);
        View rootView = inflater.inflate(R.layout.allposts, container, false);


        mDatabase= FirebaseDatabase.getInstance().getReference().child("Posts");
        mDatabaseAppriciate=FirebaseDatabase.getInstance().getReference().child("Appriciate");


        mDatabase.keepSynced(true);
        mDatabaseAppriciate.keepSynced(true);




        //EXPERIMENT WITH LISTVIEW STARTS
        postList=(RecyclerView) rootView.findViewById(R.id.postList);
        postList.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        postList.setHasFixedSize(true);
        postList.setLayoutManager(layoutManager);

//        layoutManager.scrollToPositionWithOffset(0,0);
        postList.getRecycledViewPool().clear();




        //EXPERIMENT WITH LISTVIEW ENDS
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Posts,PostViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Posts,PostViewHolder>(

                Posts.class,
                R.layout.single_post,
                PostViewHolder.class,
                mDatabase

        ) {

            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Posts model, final int position) {


                final String post_key=getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setProfileName(model.getProfilename());
                viewHolder.setImage(getContext(),model.getImage());
                viewHolder.getLayoutPosition();

//                layoutManager.scrollToPosition(0);

                //Appriciate button on click event below

                viewHolder.Appriciate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                           mProcessAppriciate=true;

                        if(mProcessAppriciate){

                                mDatabaseAppriciate.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                                            Toast.makeText(getApplicationContext(), "Blah Blah Blah", Toast.LENGTH_LONG).show();
                                        }else{
                                            mDatabaseAppriciate.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                        }


                    }
                });
              //Appriciate button click event CODE ENDS

            }

        };

        postList.setAdapter(firebaseRecyclerAdapter);


        //test code
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }


    public static class PostViewHolder extends RecyclerView.ViewHolder{

            View mView;
        Button Appriciate;
      //APPRICIATE BUTTON CODE STARTS BELOW







//APPRICIATE BUTTON CODE ENDS



        public PostViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            Appriciate=(Button) mView.findViewById(R.id.appriciate);


        }



        //Setting the title to the AllActivity
        public void setTitle(String title){
            TextView postTitle=(TextView) mView.findViewById(R.id.allPostTitle);
            postTitle.setText(title);

        }

        public void setDescription(String description){
            TextView postDescription=(TextView) mView.findViewById(R.id.allPostDescription);
            postDescription.setText(description);
        }
//PROFILE NAME EXPERIMENT
        public void setProfileName(String profilename){
            TextView postName=(TextView) mView.findViewById(R.id.profileName);
            postName.setText(profilename);
        }




        public void setImage(final Context context, final String image) {
            final ImageView postImage=(ImageView) mView.findViewById(R.id.allPostImage);
            with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(postImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {



                    Picasso.with(context).load(image).resize(500,700).into(postImage);

                }
            });

        }


        //THREADING EXPERIMENT CODE

    }
}
