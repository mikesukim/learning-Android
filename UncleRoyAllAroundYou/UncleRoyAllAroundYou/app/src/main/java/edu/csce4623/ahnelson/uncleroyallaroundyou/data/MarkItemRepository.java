package edu.csce4623.ahnelson.uncleroyallaroundyou.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import util.AppExecutors;

import static android.net.Uri.parse;

/**
 * MarkItemRepository class - implements the MarkDataSource interface
 */
public class MarkItemRepository implements MarkListDataSource {

    //Memory leak here by including the context - Fix this at some point
    private static volatile MarkItemRepository INSTANCE;

    //Thread pool for execution on other threads
    private AppExecutors mAppExecutors;
    //Context for calling MarkProvider
    private Context mContext;

    /**
     * private constructor - prevent direct instantiation
     * @param appExecutors - thread pool
     * @param context
     */
    private MarkItemRepository(@NonNull AppExecutors appExecutors, @NonNull Context context){
        mAppExecutors = appExecutors;
        mContext = context;
    }

    /**
     * public constructor - prevent creation of instance if one already exists
     * @param appExecutors
     * @param context
     * @return
     */
    public static MarkItemRepository getInstance(@NonNull AppExecutors appExecutors, @NonNull Context context){
        if(INSTANCE == null){
            synchronized (MarkItemRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new MarkItemRepository(appExecutors, context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * getMarkItems runs query in a separate thread, and on success loads data from cursor into a list
     * @param callback
     */
    @Override
    public void getMarkItems(@NonNull final LoadMarkItemsCallback callback) {
        Log.d("REPOSITORY","Loading...");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String[] projection = {
                        MarkItem.MARKITEM_ID,
                        MarkItem.MARKITEM_TITLE,
                        MarkItem.MARKITEM_CONTENT,
                        MarkItem.MARKITEM_MARKDATE,
                        MarkItem.MARKITEM_IMAGE,
                        MarkItem.MARKITEM_LATITUDE,
                        MarkItem.MARKITEM_LATITUDE};
                final Cursor c = mContext.getContentResolver().query(parse("content://" + MarkProvider.AUTHORITY + "/" + MarkProvider.MARKITEM_TABLE_NAME), projection, null, null, null);
                final List<MarkItem> markItems = new ArrayList<MarkItem>(0);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(c == null){
                            callback.onDataNotAvailable();
                        } else{
                            while(c.moveToNext()) {
                                MarkItem item = new MarkItem();
                                item.setId(c.getInt(c.getColumnIndex(MarkItem.MARKITEM_ID)));
                                item.setTitle(c.getString(c.getColumnIndex(MarkItem.MARKITEM_TITLE)));
                                item.setContent(c.getString(c.getColumnIndex(MarkItem.MARKITEM_CONTENT)));
                                item.setMarkDate(c.getLong(c.getColumnIndex(MarkItem.MARKITEM_MARKDATE)));
                                item.setImage(c.getString(c.getColumnIndex(MarkItem.MARKITEM_IMAGE)));
                                item.setLongitude(c.getDouble(c.getColumnIndex(MarkItem.MARKITEM_LONGITUDE)));
                                item.setLatitude(c.getDouble(c.getColumnIndex(MarkItem.MARKITEM_LATITUDE)));
                                markItems.add(item);
                            }
                            c.close();
                            callback.onMarkItemsLoaded(markItems);
                        }
                    }
                });

            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Not implemented yet
     * @param markItemId
     * @param callback
     */
    @Override
    public void getMarkItem(@NonNull String markItemId, @NonNull GetMarkItemCallback callback) {
        Log.d("REPOSITORY","GetMarkItem");
    }

    /**
     * saveMarkItem runs contentProvider update in separate thread
     * @param markItem
     */
    @Override
    public void saveMarkItem(@NonNull final MarkItem markItem) {
        Log.d("REPOSITORY","SaveMarkItem");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                ContentValues myCV = new ContentValues();
                myCV.put(MarkItem.MARKITEM_ID,markItem.getId());
                myCV.put(MarkItem.MARKITEM_TITLE,markItem.getTitle());
                myCV.put(MarkItem.MARKITEM_CONTENT,markItem.getContent());
                myCV.put(MarkItem.MARKITEM_MARKDATE,markItem.getMarkDate());
                myCV.put(MarkItem.MARKITEM_IMAGE,markItem.getImage());
                myCV.put(MarkItem.MARKITEM_LONGITUDE,markItem.getLongitude());
                myCV.put(MarkItem.MARKITEM_LATITUDE,markItem.getLatitude());
                final int numUpdated = mContext.getContentResolver().update(parse("content://" + MarkProvider.AUTHORITY + "/" + MarkProvider.MARKITEM_TABLE_NAME), myCV,null,null);
                Log.d("REPOSITORY","Update Mark updated " + String.valueOf(numUpdated) + " rows");
            }
        };
        mAppExecutors.diskIO().execute(runnable);

    }

    /**
     * createMarkItem runs contentProvider insert in separate thread
     * @param markItem
     */
    @Override
    public void createMarkItem(@NonNull final MarkItem markItem) {
        Log.d("REPOSITORY","CreateMarkItem");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                ContentValues myCV = new ContentValues();
                myCV.put(MarkItem.MARKITEM_TITLE,markItem.getTitle());
                myCV.put(MarkItem.MARKITEM_CONTENT,markItem.getContent());
                myCV.put(MarkItem.MARKITEM_MARKDATE,markItem.getMarkDate());
                myCV.put(MarkItem.MARKITEM_IMAGE,markItem.getImage());
                myCV.put(MarkItem.MARKITEM_LONGITUDE,markItem.getLongitude());
                myCV.put(MarkItem.MARKITEM_LATITUDE,markItem.getLatitude());
                final Uri uri = mContext.getContentResolver().insert(parse("content://" + MarkProvider.AUTHORITY + "/" + MarkProvider.MARKITEM_TABLE_NAME), myCV);
                Log.d("REPOSITORY","Create Mark finished with URI" + uri.toString());
            }
        };
        mAppExecutors.diskIO().execute(runnable);

    }
    public void deleteMarkItem (@NonNull final MarkItem markItem){
        Log.d("REPOSITORY","DeleteMarkItem");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                mContext.getContentResolver().delete(parse("content://" + MarkProvider.AUTHORITY + "/" + MarkProvider.MARKITEM_TABLE_NAME + "/" + String.valueOf(markItem.getId())),null,null);
                Log.d("REPOSITORY","Delete Mark finished");
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
