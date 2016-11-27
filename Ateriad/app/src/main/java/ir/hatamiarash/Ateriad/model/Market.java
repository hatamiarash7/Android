package ir.hatamiarash.Ateriad.model;

import android.graphics.Bitmap;

public class Market {
    private int id;
    private String FirstName;
    private String LastName;
    private String phone;
    private String email;
    private String job;
    private String photo;
    private Bitmap bitmap;

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getjob() {
        return job;
    }

    public void setjob(String job) {
        this.job = job;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}