package android.bignerdranch.mycheckins;

import java.util.Date;
import java.util.UUID;

public class Checkin {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mPlace;
    private String mDetails;
    private double mLat;
    private double mLon;

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public Checkin() {
        this(UUID.randomUUID());
    }

    public Checkin(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String getImageFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
