package cn.kq.indexedlistview;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * Created by zhaopan on 16/1/23 11:01
 * e-mail: kangqiao610@gmail.com
 */
public class IndexedListView extends ListView implements IndexBarPainter.OnSelectSectionListener {

    private boolean mIsFastScrollEnabled;
    private IndexBarPainter.Builder mBuilder;
    private IndexBarPainter mIndexBar;
    private SectionIndexer mIndexer;

    public interface IndexKey{
        String getIndexKey();
    }

    public IndexedListView(Context context) {
        super(context);
        init(null);
    }

    public IndexedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public IndexedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (mIsFastScrollEnabled) {
            mBuilder = new IndexBarPainter.Builder(getContext(), attrs);
            mIndexBar = mBuilder.setOnSelectSectionListener(this).build();
        }
    }

    public IndexBarPainter.Builder getIndexBarBuilder() {
        return mBuilder;
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
        mIsFastScrollEnabled = enabled;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (null != mIndexBar) {
            mIndexBar.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (null != mIndexBar && mIndexBar.onTouchEvent(ev)) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (null != mIndexBar && mIndexBar.contains(ev.getX(), ev.getY())) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (null != mIndexBar) {
            if(null == mIndexer) {
                setSectionIndexer(new DefaultAlphabetIndexer(adapter, mIndexBar.isSectionTextAtoZ()));
            }
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    if (null != mIndexBar) {
                        mIndexBar.setSections((String[]) mIndexer.getSections());
                    }
                }
            });
        }
    }

    public void setSectionIndexer(SectionIndexer indexer) {
        if(null != indexer) mIndexer = indexer;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (null != mIndexBar) {
            mIndexBar.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    public void onSelectSection(int section) {
        setSelection(mIndexer.getPositionForSection(section));
    }
}
