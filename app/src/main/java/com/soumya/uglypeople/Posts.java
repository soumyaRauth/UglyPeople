package com.soumya.uglypeople;

/**
 * Created by Soumya on 12/10/2016.
 */

public class Posts {


    public String title;
    public String description;
    public String image;
    public String profilename;


    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }

//    public String profilename;


    public Posts(){

    }

    public Posts(String title, String image, String description, String profilename) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.profilename=profilename;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
