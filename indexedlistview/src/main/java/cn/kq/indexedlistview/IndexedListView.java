package cn.kq.indexedlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * Created by zhaopan on 16/1/23 11:01
 * e-mail: kangqiao610@gmail.com
 */
public class IndexedListView extends ListView implements IndexBarPainter.OnSelectSectionInnerListener {
    public static final boolean DEFAULT_ISSHOW_ALL_SUPPORTED_SECTIONS = false;

    private boolean mIsFastScrollEnabled;
    private boolean mIsShowIndexBar;
    private Builder mBuilder;
    private IndexBarPainter mIndexBarPainter;
    private SectionIndexer mSectionIndexer;
    private SectionIndexerCreator mSectionIndexerCreator;
    private OnSelectSectionListener mOnSelectSectionListener;
    boolean isShowAllSupportedSections = DEFAULT_ISSHOW_ALL_SUPPORTED_SECTIONS;

    public interface IndexKey {
        String getIndexKey();
    }

    public interface SectionIndexerCreator {
        SectionIndexer createSectionIndexer(ListAdapter adapter, boolean isShowAllSupportedSections);
    }

    public interface OnSelectSectionListener {
        void onSelectSection(int section, int position);
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
        //if (true || mIsFastScrollEnabled) {
            mBuilder = new Builder(attrs);
            mBuilder.getIndexBarPainter().setOnSelectSectionInnerListener(this);
        //}
    }

    public boolean isShowIndexBar() {
        return mIsShowIndexBar && null != mIndexBarPainter;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
        mIsFastScrollEnabled = enabled;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isShowIndexBar()) {
            mIndexBarPainter.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isShowIndexBar() && mIndexBarPainter.onTouchEvent(ev)) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isShowIndexBar() && mIndexBarPainter.contains(ev.getX(), ev.getY())) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (isShowIndexBar()) {
            if (null == mSectionIndexer) {
                initSectionIndexer(isShowAllSupportedSections);
            }
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    updateSections();
                }
            });
        }
    }

    private void initSectionIndexer(boolean isShowAll) {
        if (null != getAdapter()) {
            if (null != mSectionIndexerCreator) {
                mSectionIndexer = mSectionIndexerCreator.createSectionIndexer(getAdapter(), isShowAll);
            } else {
                mSectionIndexer = new DefaultAlphabetIndexer(getAdapter(), isShowAll);
            }
            updateSections();
        }
    }

    private void updateSections() {
        if (isShowIndexBar() && null != mSectionIndexer) {
            mIndexBarPainter.setSections((String[]) mSectionIndexer.getSections());
            mIndexBarPainter.onSizeChanged(getWidth(), getHeight(), 0, 0);
        }
    }

    public void setSectionIndexerCreator(SectionIndexerCreator sectionIndexerCreator) {
        mSectionIndexerCreator = sectionIndexerCreator;
        if (null != getAdapter()) {
            initSectionIndexer(isShowAllSupportedSections);
        }
    }

    public SectionIndexer getSectionIndexer() {
        if (null == mSectionIndexer) {
            initSectionIndexer(isShowAllSupportedSections);
        }
        return mSectionIndexer;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isShowIndexBar()) {
            mIndexBarPainter.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public void setOnSelectSectionListener(OnSelectSectionListener listener) {
        mBuilder.setOnSelectSectionListener(listener);
    }

    @Override
    public void onSelectSection(int section) {
        int position = mSectionIndexer.getPositionForSection(section);
        setSelection(position);
        if(null != mOnSelectSectionListener){
            mOnSelectSectionListener.onSelectSection(section, position);
        }
    }

    public class Builder {
        private boolean mIsShowAll;

        /**
         * Default constructor for Builder.
         */
        public Builder() {
            this(null);
        }

        /**
         * Build {@link IndexBarPainter} give the current set of capabilities.
         */
        public Builder(AttributeSet attrs) {
            initAttrs(attrs);
            mIndexBarPainter = new IndexBarPainter(IndexedListView.this.getContext(), attrs);
        }

        private void initAttrs(AttributeSet attrs) {
            if (null != attrs) {
                TypedArray typedArray = IndexedListView.this.getContext().obtainStyledAttributes(attrs, R.styleable.IndexedListView);

                mIsShowIndexBar = typedArray.getBoolean(R.styleable.IndexedListView_indexBar_isShow, true);
                isShowAllSupportedSections = typedArray.getBoolean(R.styleable.IndexedListView_indexBar_isShowAll, isShowAllSupportedSections);

                typedArray.recycle();
            }
        }

        public void build() {
            if (isShowIndexBar()) {
                mIndexBarPainter.initPaint();
                if (isShowAllSupportedSections != mIsShowAll) {
                    isShowAllSupportedSections = mIsShowAll;
                    initSectionIndexer(mIsShowAll);
                }
                mIndexBarPainter.onSizeChanged(IndexedListView.this.getWidth(), IndexedListView.this.getHeight(), 0, 0);//???是否去掉
            }
            postInvalidate();
        }

        public Builder setShowIndexBar(boolean isShowIndexBar) {
            mIsShowIndexBar = isShowIndexBar;
            return this;
        }

        public IndexBarPainter getIndexBarPainter() {
            return mIndexBarPainter;
        }

        public Builder setIsShowAll(boolean isShowAll) {
            mIsShowAll = isShowAll;
            return this;
        }

        public Builder setSectionIndexerCreator(SectionIndexerCreator creator) {
            IndexedListView.this.setSectionIndexerCreator(creator);
            return this;
        }

        public Builder setIndexBarMargin(int margin) {
            mIndexBarPainter.indexBarMargin = dp2px(margin);
            return this;
        }

        public Builder setIndexBarMargin(float margin) {
            mIndexBarPainter.indexBarMargin = margin;
            return this;
        }

        public Builder setIndexBarBgColor(int color) {
            mIndexBarPainter.indexBarBgColor = color;
            return this;
        }

        public Builder setSectionTextPadding(int sectionTextPadding) {
            mIndexBarPainter.sectionTextPadding = dp2px(sectionTextPadding);
            return this;
        }

        public Builder setSectionTextPadding(float padding) {
            mIndexBarPainter.sectionTextPadding = padding;
            return this;
        }

        public Builder setSectionTextSize(int sectionTextSize) {
            mIndexBarPainter.sectionTextSize = dp2px(sectionTextSize);
            return this;
        }

        public Builder setSectionTextSize(float textSize) {
            mIndexBarPainter.sectionTextSize = textSize;
            return this;
        }

        public Builder setSectionTextColor(int sectionTextColor) {
            mIndexBarPainter.sectionTextColor = sectionTextColor;
            return this;
        }

        public Builder setIsShowPreview(boolean isShowPreview) {
            mIndexBarPainter.isShowPreview = isShowPreview;
            return this;
        }

        public Builder setPreviewBgColor(int previewBgColor) {
            mIndexBarPainter.previewBgColor = previewBgColor;
            return this;
        }

        public Builder setPreviewTextPadding(int previewTextPadding) {
            mIndexBarPainter.previewTextPadding = dp2px(previewTextPadding);
            return this;
        }

        public Builder setPreviewTextPadding(float padding) {
            mIndexBarPainter.previewTextPadding = padding;
            return this;
        }

        public Builder setPreviewTextSize(int previewTextSize) {
            mIndexBarPainter.previewTextSize = dp2px(previewTextSize);
            return this;
        }

        public Builder setPreviewTextSize(float textSize) {
            mIndexBarPainter.previewTextSize = textSize;
            return this;
        }

        public Builder setPreviewTextColor(int previewTextColor) {
            mIndexBarPainter.previewTextColor = previewTextColor;
            return this;
        }

        public Builder setOnSelectSectionListener(OnSelectSectionListener listener) {
            mOnSelectSectionListener = listener;
            return this;
        }

        private float dp2px(int dp) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, IndexedListView.this.getResources().getDisplayMetrics());
        }
    }
}