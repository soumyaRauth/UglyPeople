package com.soumya.uglypeople;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.R.attr.data;
import static android.R.attr.start;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button btnFb;
    private LoginButton loginButton;
    private String email;
    private String name;
    private String uId;
    private FirebaseUser user;
    Uri profilePic;
    private static final String TAG = "MyActivity";
    Intent fbIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());



        setContentView(R.layout.activity_login);


        //CUSTOM LOGIN BUTTON


        //EXPERIMENTAL CODE BELOW
        btnFb=(Button) findViewById(R.id.btnFacebook);
        if(getIntent().hasExtra("logout")){

            LoginManager.getInstance().logOut();

        }

        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            // user has logged in
//            LoginManager.getInstance().logOut();
            Intent homeIntent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(homeIntent);

        }


        //EXPERIMENTAL CODE ENDS ABOVE









        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                loginButton.performClick();
                loginButton.setVisibility(View.GONE);
            }
        });
//CUSTOM LOGIN BUTTON CODE ENDS
        mAuth= FirebaseAuth.getInstance();
        mCallbackManager=CallbackManager.Factory.create();

        //BELOW EXPERIMENTAL COMMENTING

//        if(getIntent().hasExtra("logout")){
//
//            LoginManager.getInstance().logOut();
//
//        }





        loginButton=(LoginButton)findViewById(R.id.fblogin);
        loginButton.setReadPermissions("email","public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());

//Code to be pasted here
                Intent theIntent=new Intent(LoginActivity.this,HomeActivity.class);
                theIntent.putExtra("NAME",name);
                theIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                theIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(theIntent);
                finish();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

      mAuthListener=new FirebaseAuth.AuthStateListener() {
          @Override
          public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
              user=firebaseAuth.getCurrentUser();
              if(user !=null){

                  email=user.getEmail();
                  name=user.getDisplayName();
                  uId=user.getUid();
                  profilePic=user.getPhotoUrl();


              }else{
                  Toast.makeText(LoginActivity.this,"OOPS!! something went wrong",Toast.LENGTH_LONG).show();

              }
          }
      };






    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Failed to Authenticate",Toast.LENGTH_LONG).show();
                }else{

                    if(mAuth.getCurrentUser()!=null) {

                        Intent theIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        theIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(theIntent);
                        finish();
                    }

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);




    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.removeAuthStateListener(mAuthListener);
    }

}
