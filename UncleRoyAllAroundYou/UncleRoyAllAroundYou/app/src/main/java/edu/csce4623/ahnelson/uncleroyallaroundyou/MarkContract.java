package edu.csce4623.ahnelson.uncleroyallaroundyou;

import android.util.Log;

import java.util.List;

import edu.csce4623.ahnelson.uncleroyallaroundyou.data.MarkItem;

/**
 * MarkListContract
 * Two inner interfaces, a View and a Presenter for the MarkListActivity
 */
public interface MarkContract {

    interface View{

        void setPresenter(MarkContract.Presenter presenter);
        void showMarkItems(List<MarkItem> markItems);
    }

    interface Presenter{

        void loadMarkItems();
        void createMarkItem(MarkItem item);
        void deleteMarkItem(MarkItem item);
    }

}
