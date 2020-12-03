package edu.csce4623.ahnelson.uncleroyallaroundyou.data;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Room Database implementation
//Don't touch unless you know what you are doing.
@Database(entities = {MarkItem.class}, version = 1, exportSchema = false)
public abstract class MarkItemDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "mark_db";
    private static MarkItemDatabase INSTANCE;

    public static MarkItemDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,MarkItemDatabase.class,DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public abstract MarkItemDao getMarkItemDao();

}
