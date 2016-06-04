package com.geopublish.geoclient.ui.controls;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * Created by edgar on 11/20/2015.
 * Este gridView maneja el refresco correctamente cuando se actualiza cambian los datos en el DataSource
 */
public class ProperGridView extends GridView {

    private static final String TAG = ProperGridView.class.getName();

    @SuppressWarnings("unused")
    public ProperGridView(Context context) {
        super(context);
    }

    @SuppressWarnings("unused")
    public ProperGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public ProperGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();

            refreshVisibleViews();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();

            refreshVisibleViews();
        }
    }

    private DataSetObserver mDataSetObserver = new AdapterDataSetObserver();
    private Adapter mAdapter;

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;

        mAdapter.registerDataSetObserver(mDataSetObserver);
    }

    void refreshVisibleViews() {
        if (mAdapter != null) {
            for (int i = getFirstVisiblePosition(); i <= getLastVisiblePosition(); i ++) {
                final int dataPosition = i ;
                final int childPosition = i - getFirstVisiblePosition();
                if (dataPosition >= 0 && dataPosition < mAdapter.getCount()
                        && getChildAt(childPosition) != null) {
                    Log.v(TAG, "Refreshing view (data=" + dataPosition + ",child=" + childPosition + ")");
                    mAdapter.getView(dataPosition, getChildAt(childPosition), this);
                }
            }
        }
    }

}

