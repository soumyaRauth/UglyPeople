package com.soumya.uglypeople;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.data;

public class HomeActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final int CAMERA_REQUEST=1888;
    private ViewPager mViewPager;
    private File imageFile;
    private Uri imageUri;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
//    private Button logOut;
    private TextView welcome;
    private String name;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);

        Bundle bundle = getIntent().getExtras();


        mAuth=FirebaseAuth.getInstance();




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "IMAGE_" + timeStamp + "_";

            //Adding Camera Intent when clicked on the FAB
            @Override
            public void onClick(View view) {


                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),imageFileName);
                imageUri=Uri.fromFile(imageFile);

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                cameraIntent.putExtra("imageUri",imageUri);


                    MediaScannerConnection.scanFile(HomeActivity.this,
                            new String[]{imageFile.getAbsolutePath()},
                            new String[]{"image/jpeg"}, null);


                //Experimental code ends

                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });

    }

    //What Happens After the Image is Captured

        protected void onActivityResult ( int requestCode, int resultCode, Intent data){


        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {



            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("imageUri", imageUri.toString());
            startActivity(intent);

            Toast.makeText(this, "File saved to" + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

        }
    }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            FacebookSdk.sdkInitialize(getApplicationContext());
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
            if(id==R.id.action_logout){



                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                intent.putExtra("logout",true);
                startActivity(intent);
                finish();

            }

        return super.onOptionsItemSelected(item);
    }


        public class SectionsPagerAdapter extends FragmentPagerAdapter {

            public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        AllPosts allposts = new AllPosts();
                        return allposts;
                    case 1:
                        MyPosts myposts = new MyPosts();
                        return myposts;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                // Show 2 total pages.
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "All Posts";
                    case 1:
                        return "My Posts";

                }
                return null;
            }
        }
    }


