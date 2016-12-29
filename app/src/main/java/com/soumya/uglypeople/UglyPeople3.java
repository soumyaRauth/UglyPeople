package com.soumya.uglypeople;
import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Soumya on 12/16/2016.
 *
 *
 * OFFLINE CAPABILITIES
 */

public class UglyPeople3 extends MultiDexApplication {

   @Override
    public void onCreate(){
       super.onCreate();

       FirebaseDatabase.getInstance().setPersistenceEnabled(true);


       Picasso.Builder builder=new Picasso.Builder(this);
       builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
       Picasso built=builder.build();
       built.setIndicatorsEnabled(false);
       built.setLoggingEnabled(true);
       Picasso.setSingletonInstance(built);
   }

}
