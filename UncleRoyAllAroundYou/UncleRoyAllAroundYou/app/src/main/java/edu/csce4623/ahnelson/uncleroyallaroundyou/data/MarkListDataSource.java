package edu.csce4623.ahnelson.uncleroyallaroundyou.data;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Interface for any implementation of a MarkListDataSource
 * (Currently only have one - a local ContentProvider based implementation (@MarkItemRepository)
 */
public interface MarkListDataSource {

    /**
     * LoadMarkItemsCallback interface
     * Example of how to implement callback functions depending on the result of functions in interfaces
     * Currently, onDataNotAvailable is not implemented
     */
    interface LoadMarkItemsCallback {

        void onMarkItemsLoaded(List<MarkItem> markItems);

        void onDataNotAvailable();
    }

    /**
     * GetMarkItemsCallback interface
     * Not currently implementd
     */
    interface GetMarkItemCallback {

        void onMarkItemLoaded(MarkItem task);

        void onDataNotAvailable();
    }

    /**
     * getMarkItems loads all MarkItems, calls either success or failure fuction above
     * @param callback - Callback function
     */
    void getMarkItems(@NonNull LoadMarkItemsCallback callback);

    /**
     * getMarkItem - Get a single MarkItem - currently not implemented
     * @param markItemId - String of the current ItemID to be retrieved
     * @param callback - Callback function
     */
    void getMarkItem(@NonNull String markItemId, @NonNull GetMarkItemCallback callback);

    /**
     * SaveMarkItem saves a markItem to the database - No callback (should be implemented for
     * remote databases)
     * @param markItem
     */
    void saveMarkItem(@NonNull final MarkItem markItem);

    /**
     * CreateMarkItem adds a markItem to the database - No callback (should be implemented for
     * remote databases)
     * @param markItem
     */
    void createMarkItem(@NonNull MarkItem markItem);

}
