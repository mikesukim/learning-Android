package edu.csce4623.ahnelson.uncleroyallaroundyou.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

//Don't touch unless you know what you are doing.
@Dao
public interface MarkItemDao {
    /**
     * Insert a markItem into the table
     * @return row ID for newly inserted data
     */
    @Insert
    long insert(MarkItem item);    /**
     * select all markItem
     * @return A {@link Cursor} of all markItems in the table
     */
    @Query("SELECT * FROM MarkItem")
    Cursor findAll();      /**
     * Delete a markItem by ID
     * @return A number of markItems deleted
     */
    @Query("DELETE FROM MarkItem WHERE id = :id ")
    int delete(long id);    /**
     * Update the markItem
     * @return A number of markItems updated
     */
    @Update
    int update(MarkItem markItem);
}