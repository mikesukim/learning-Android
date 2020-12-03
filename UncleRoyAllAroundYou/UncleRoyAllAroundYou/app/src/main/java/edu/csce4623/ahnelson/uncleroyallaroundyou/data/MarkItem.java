package edu.csce4623.ahnelson.uncleroyallaroundyou.data;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * MarkItem class
 * Implements serializable for easy pass through between intents
 * Includes Room annotations for five columns for each of five private members
 */
@Entity
public class MarkItem implements Serializable {

    //Static strings for the column names usable by other classes
    public static final String MARKITEM_ID = "id";
    public static final String MARKITEM_TITLE = "title";
    public static final String MARKITEM_CONTENT = "content";
    public static final String MARKITEM_MARKDATE = "markDate";
    public static final String MARKITEM_IMAGE = "image";
    public static final String MARKITEM_LONGITUDE = "longitude";
    public static final String MARKITEM_LATITUDE = "latitude";


    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "markDate")
    private long markDate;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "latitude")
    private double latitude;


    //Following are getters and setters for all five member variables
    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getMarkDate() {
        return markDate;
    }

    public void setMarkDate(long markDate) {
        this.markDate = markDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    //Create a MarkItem from a ContentValues object
    public static MarkItem fromContentValues(ContentValues contentValues){
        MarkItem item = new MarkItem();
        if(contentValues.containsKey(MARKITEM_ID)){
            item.setId(contentValues.getAsInteger(MARKITEM_ID));
        }
        if(contentValues.containsKey(MARKITEM_TITLE)){
            item.setTitle(contentValues.getAsString(MARKITEM_TITLE));
        }
        if(contentValues.containsKey(MARKITEM_CONTENT)){
            item.setContent(contentValues.getAsString(MARKITEM_CONTENT));
        }
        if (contentValues.containsKey(MARKITEM_MARKDATE)){
            item.setMarkDate(contentValues.getAsLong(MARKITEM_MARKDATE));
        }
        if (contentValues.containsKey(MARKITEM_IMAGE)){
            item.setImage(contentValues.getAsString(MARKITEM_IMAGE));
        }
        if (contentValues.containsKey(MARKITEM_LATITUDE)){
            item.setLatitude(contentValues.getAsDouble(MARKITEM_LATITUDE));
        }
        if (contentValues.containsKey(MARKITEM_LONGITUDE)){
            item.setLongitude(contentValues.getAsDouble(MARKITEM_LONGITUDE));
        }
        return item;
    }


}
