package ir.hatamiarash.ZiMia.model;

import android.graphics.Bitmap;

public class Resturan {
    private int id;
    private String Name;
    private String Picture;
    private int OpenHour;
    private int CloseHour;
    private Bitmap bitmap;

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getOpenHour() {
        return OpenHour;
    }

    public void setOpenHour(int OpenHour) {
        this.OpenHour = OpenHour;
    }

    public int getCloseHour() {
        return CloseHour;
    }

    public void setCloseHour(int CloseHour) {
        this.CloseHour = CloseHour;
    }
}