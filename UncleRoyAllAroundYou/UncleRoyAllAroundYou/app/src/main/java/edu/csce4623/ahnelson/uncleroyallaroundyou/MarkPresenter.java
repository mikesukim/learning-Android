package edu.csce4623.ahnelson.uncleroyallaroundyou;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import edu.csce4623.ahnelson.uncleroyallaroundyou.data.MarkItem;
import edu.csce4623.ahnelson.uncleroyallaroundyou.data.MarkItemRepository;
import edu.csce4623.ahnelson.uncleroyallaroundyou.data.MarkListDataSource;

public class MarkPresenter implements  MarkContract.Presenter {
    //Data repository instance
    private static MarkItemRepository mMarkItemRepository;
    //View instance
    private final MarkContract.View mMarkView;

    // Integer request codes for creating or updating through the result method
    private static final int CREATE_MARK_REQUEST = 0;
    private static final int UPDATE_MARK_REQUEST = 1;

    /**
     * MarkListPresenter constructor
     * @param markItemRepository - Data repository instance
     */
    public MarkPresenter(@NonNull MarkItemRepository markItemRepository , @NonNull MarkContract.View markView){
        mMarkItemRepository = markItemRepository;
        mMarkView = markView;

        mMarkView.setPresenter(this);
    }


    @Override
    public void loadMarkItems() {
        mMarkItemRepository.getMarkItems(new MarkListDataSource.LoadMarkItemsCallback() {
            @Override
            public void onMarkItemsLoaded(List<MarkItem> markItems) {
                mMarkView.showMarkItems(markItems);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void createMarkItem(MarkItem item) {
        mMarkItemRepository.createMarkItem(item);
        loadMarkItems();
    }

    @Override
    public void deleteMarkItem(MarkItem item) {
        mMarkItemRepository.deleteMarkItem(item);
        loadMarkItems();
    }


}
