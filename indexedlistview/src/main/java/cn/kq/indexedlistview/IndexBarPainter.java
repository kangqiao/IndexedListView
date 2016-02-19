package cn.kq.indexedlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

/**
 * Created by zhaopan on 16/1/23 11:09
 * e-mail: kangqiao610@gmail.com
 */
public class IndexBarPainter {

    private static final float DEFAULT_INDEXBAR_MARGIN = 4;
    private static final int DEFAULT_INDEXBAR_BGCOLOR = Color.parseColor("#28000000");
    private static final int DEFAULT_INDEXBAR_RADIUS = 5;

    private static final float DEFAULT_SECTION_TEXT_SIZE = 12;
    private static final float DEFAULT_SECTION_TEXT_PADDING = 2;
    private static final int DEFAULT_SECTION_TEXT_COLOR = Color.BLACK;

    private static final float DEFAULT_PREVIEW_TEXT_SIZE = 28;
    private static final float DEFAULT_PREVIEW_TEXT_PADDING = 12;
    private static final float DEFAULT_PREVIEW_RADIUS = 5;
    private static final int DEFAULT_PREVIEW_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_PREVIEW_BG_COLOR = Color.parseColor("#28000000");

    private boolean indexBarIsAtoZ = false;
    private float indexBarMargin;
    private int indexBarBgColor;

    private float sectionTextPadding;
    private float sectionTextSize;
    private int sectionTextColor;

    private boolean isShowPreview;
    private int previewBgColor;
    private float previewTextPadding;
    private float previewTextSize;
    private int previewTextColor;

    private RectF indexBarRectF;
    private Paint indexBarBgPaint;

    private Paint sectionTextPaint;
    private float sectionSize;

    private RectF previewRectF;
    private Paint previewRectFPaint;

    private Paint previewTextPaint;
    private float previewSize;

    private float mDensity;
    private float mScaledDensity;

    private int mListViewWidth;
    private int mListViewHeight;

    private int mCurrentSection = -1;
    private boolean mIsIndexing = false;
    private String[] mSections = null;
    private AttributeSet mAttrs;
    private Context mContext;
    private OnSelectSectionListener mListener;

    public interface OnSelectSectionListener {
        void onSelectSection(int section);
    }

    private IndexBarPainter(Context context, AttributeSet attrs) {
        mContext = context;
        mAttrs = attrs;
        mDensity = mContext.getResources().getDisplayMetrics().density;
        mScaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        initAttrs();
        initPaint();
    }

    private void initAttrs() {
        if (null != mAttrs) {
            TypedArray typedArray = mContext.obtainStyledAttributes(mAttrs, R.styleable.IndexedListView);

            indexBarIsAtoZ = typedArray.getBoolean(R.styleable.IndexedListView_indexBar_isAtoZ, indexBarIsAtoZ);
            isShowPreview = typedArray.getBoolean(R.styleable.IndexedListView_preview_isShow, true);

            indexBarMargin = typedArray.getDimension(R.styleable.IndexedListView_indexBar_margin, DEFAULT_INDEXBAR_MARGIN * mDensity);
            indexBarBgColor = typedArray.getColor(R.styleable.IndexedListView_indexBar_bgColor, DEFAULT_INDEXBAR_BGCOLOR);

            sectionTextPadding = typedArray.getDimension(R.styleable.IndexedListView_section_textPadding, DEFAULT_SECTION_TEXT_PADDING * mDensity);
            sectionTextSize = typedArray.getDimension(R.styleable.IndexedListView_section_textSize, DEFAULT_SECTION_TEXT_SIZE * mScaledDensity);
            sectionTextColor = typedArray.getColor(R.styleable.IndexedListView_section_textColor, DEFAULT_SECTION_TEXT_COLOR);

            previewBgColor = typedArray.getColor(R.styleable.IndexedListView_preview_bgColor, DEFAULT_PREVIEW_BG_COLOR);
            previewTextPadding = typedArray.getDimension(R.styleable.IndexedListView_preview_padding, DEFAULT_PREVIEW_TEXT_PADDING * mDensity);
            previewTextSize = typedArray.getDimension(R.styleable.IndexedListView_preview_textSize, DEFAULT_PREVIEW_TEXT_SIZE * mScaledDensity);
            previewTextColor = typedArray.getColor(R.styleable.IndexedListView_preview_textColor, DEFAULT_PREVIEW_TEXT_COLOR);

        } else {
            //indexBarIsAtoZ = false;
            indexBarMargin = DEFAULT_INDEXBAR_MARGIN * mDensity;
            indexBarBgColor = DEFAULT_INDEXBAR_BGCOLOR;

            sectionTextPadding = DEFAULT_SECTION_TEXT_PADDING * mDensity;
            sectionTextSize = DEFAULT_SECTION_TEXT_SIZE * mScaledDensity;
            sectionTextColor = DEFAULT_SECTION_TEXT_COLOR;

            isShowPreview = true;
            previewBgColor = DEFAULT_PREVIEW_BG_COLOR;
            previewTextPadding = DEFAULT_PREVIEW_TEXT_PADDING * mDensity;
            previewTextSize = DEFAULT_PREVIEW_TEXT_SIZE * mScaledDensity;
            previewTextColor = DEFAULT_PREVIEW_TEXT_COLOR;
        }
    }

    private void initPaint() {
        //索引文字的绘制属性.
        sectionTextPaint = new Paint();
        sectionTextPaint.setColor(sectionTextColor);
        sectionTextPaint.setAntiAlias(true);
        sectionTextPaint.setTextSize(sectionTextSize);
        sectionSize = sectionTextPaint.descent() - sectionTextPaint.ascent() + 2 * sectionTextPadding;

        //索引条背景的绘制属性.
        indexBarBgPaint = new Paint();
        indexBarBgPaint.setColor(indexBarBgColor);
        indexBarBgPaint.setAntiAlias(true);

        //预览文字的绘制属性.
        previewTextPaint = new Paint();
        previewTextPaint.setColor(previewTextColor);
        previewTextPaint.setAntiAlias(true);
        previewTextPaint.setTextSize(previewTextSize);
        previewSize = previewTextPaint.descent() - previewTextPaint.ascent() + 2 * previewTextPadding;

        //预览区域背景的绘制属性.
        previewRectFPaint = new Paint();
        previewRectFPaint.setColor(previewBgColor);
        previewRectFPaint.setAntiAlias(true);
        previewRectFPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));
    }

    public void draw(Canvas canvas) {
        //绘制索引条背景区域.
        canvas.drawRoundRect(indexBarRectF
                , DEFAULT_INDEXBAR_RADIUS * mDensity
                , DEFAULT_INDEXBAR_RADIUS * mDensity
                , indexBarBgPaint);

        if (mSections != null && mSections.length > 0) {
            //绘制预览区域背景和文字.
            if (isShowPreview && mCurrentSection >= 0) {
                //绘制背景
                canvas.drawRoundRect(previewRectF
                        , DEFAULT_PREVIEW_RADIUS * mDensity
                        , DEFAULT_PREVIEW_RADIUS * mDensity
                        , previewRectFPaint);

                float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
                //绘制预览文本
                canvas.drawText(mSections[mCurrentSection]
                        , previewRectF.left + (previewSize - previewTextWidth) / 2
                        , previewRectF.top + previewTextPadding - previewTextPaint.ascent()
                        , previewTextPaint);
            }

            for (int i = 0; i < mSections.length; i++) {
                float sectionTextWidth = sectionTextPaint.measureText(mSections[i]);
                //绘制索引文字.
                canvas.drawText(mSections[i]
                        , indexBarRectF.left + (sectionSize - sectionTextWidth) / 2
                        , indexBarRectF.top + sectionSize * i + sectionTextPadding - sectionTextPaint.ascent()
                        , sectionTextPaint);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (contains(ev.getX(), ev.getY())) {
                    mIsIndexing = true;
                    mCurrentSection = getSectionByPoint(ev.getY());
                    if (null != mListener) {
                        mListener.onSelectSection(mCurrentSection);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing) {
                    mIsIndexing = false;
                    mCurrentSection = -1;
                }
                break;
        }
        return false;
    }

    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.length == 0)
            return 0;
        if (y < indexBarRectF.top)
            return 0;
        if (y >= indexBarRectF.top + indexBarRectF.height())
            return mSections.length - 1;
        return (int) ((y - indexBarRectF.top) / ((indexBarRectF.height()) / mSections.length));
    }

    public boolean contains(float x, float y) {
        return (x >= indexBarRectF.left && y >= indexBarRectF.top && y <= indexBarRectF.top + indexBarRectF.height());
    }

    public boolean isSectionTextAtoZ() {
        return indexBarIsAtoZ;
    }

    public void setSections(String[] sections) {
        mSections = sections;
    }

    public void setOnSelectSectionListener(OnSelectSectionListener listener) {
        mListener = listener;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;

        int count = 0;
        if (null != mSections) {
            count = mSections.length;
        }
        float indexBarHeight = sectionSize * count;
        if (0 < mListViewHeight && 0 < indexBarHeight && (mListViewHeight - 2 * indexBarMargin) < indexBarHeight) {
            indexBarMargin = 0;
            indexBarHeight = mListViewHeight - 2 * indexBarMargin;
            sectionSize = indexBarHeight / count;
            sectionTextPadding = sectionSize * 0.2f / 2;
            sectionTextSize = sectionSize * 0.8f;
            sectionTextPaint.setTextSize(sectionTextSize);
        }

        //初始化索引条背景区域
        indexBarRectF = new RectF(mListViewWidth - sectionSize - indexBarMargin
                , (mListViewHeight - indexBarHeight) / 2
                , mListViewWidth - indexBarMargin
                , (mListViewHeight - indexBarHeight) / 2 + indexBarHeight);

        float previewSizeMaxValue = Math.min(mListViewWidth, mListViewHeight);
        if (0 < previewSizeMaxValue && previewSizeMaxValue < previewSize) {
            previewSize = previewSizeMaxValue * 0.2f;
            previewTextPadding = previewSize * 0.2f / 2;
            previewTextSize = previewSize * 0.8f;
            previewTextPaint.setTextSize(previewTextSize);
        }

        //初始化预览背景区域
        previewRectF = new RectF((mListViewWidth - previewSize) / 2
                , (mListViewHeight - previewSize) / 2
                , (mListViewWidth - previewSize) / 2 + previewSize
                , (mListViewHeight - previewSize) / 2 + previewSize);
    }

    public static class Builder {
        private final IndexBarPainter mIndexBarPainter;

        /**
         * Default constructor for Builder.
         */
        public Builder(Context context) {
            this(context, null);
        }

        public Builder(Context context, AttributeSet attrs) {
            mIndexBarPainter = new IndexBarPainter(context, attrs);
        }

        /**
         * Build {@link IndexBarPainter} give the current set of capabilities.
         */
        public void build() {
            mIndexBarPainter.initPaint();
            mIndexBarPainter.onSizeChanged(mIndexBarPainter.mListViewWidth, mIndexBarPainter.mListViewHeight, 0, 0);//???是否去掉
        }

        public IndexBarPainter getIndexBarPainter() {
            return mIndexBarPainter;
        }

        public Builder setIndexBarIsAtoZ(boolean isAtoZ) {
            mIndexBarPainter.indexBarIsAtoZ = isAtoZ;
            return this;
        }

        public Builder setIndexBarMargin(int margin) {
            mIndexBarPainter.indexBarMargin = dp2px(mIndexBarPainter.mContext, margin);
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
            mIndexBarPainter.sectionTextPadding = dp2px(mIndexBarPainter.mContext, sectionTextPadding);
            return this;
        }

        public Builder setSectionTextPadding(float padding) {
            mIndexBarPainter.sectionTextPadding = padding;
            return this;
        }

        public Builder setSectionTextSize(int sectionTextSize) {
            mIndexBarPainter.sectionTextSize = dp2px(mIndexBarPainter.mContext, sectionTextSize);
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
            mIndexBarPainter.previewTextPadding = dp2px(mIndexBarPainter.mContext, previewTextPadding);
            return this;
        }

        public Builder setPreviewTextPadding(float padding) {
            mIndexBarPainter.previewTextPadding = padding;
            return this;
        }

        public Builder setPreviewTextSize(int previewTextSize) {
            mIndexBarPainter.previewTextSize = dp2px(mIndexBarPainter.mContext, previewTextSize);
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
            mIndexBarPainter.setOnSelectSectionListener(listener);
            return this;
        }

        private static float dp2px(Context context, int dp) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        }
    }
}