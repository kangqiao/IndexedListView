package cn.kq.indexedlistview;


import android.widget.ListAdapter;
import android.widget.SectionIndexer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cn.kq.indexedlistview.IndexedListView.IndexKey;

/**
 * Created by zhaopan on 15/8/3 14:04
 * e-mail: kangqiao610@gmail.com
 */
public class DefaultAlphabetIndexer implements SectionIndexer {
    private static final String TAG = "DefaultAlphabetIndexer";

    /**
     * 定义字母表的排序规则
     */
    public static final CharSequence ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String DEFAULT_ALPHABET = "#";
    /**
     * Cached length of the alphabet array.
     */
    private int mAlphabetLength;

    /**
     * The section array converted from the alphabet string.
     */
    private String[] mAlphabetArray;

    private ListAdapter mAdapter;
    private boolean mIsShowAllSupportedSections;

    public DefaultAlphabetIndexer(ListAdapter adapter, boolean isShowAll) {
        mIsShowAllSupportedSections = isShowAll;
        mAdapter = adapter;
    }

    @Override
    public Object[] getSections() {
        if(mIsShowAllSupportedSections){
            mAlphabetLength = ALPHABET.length();
            mAlphabetArray = new String[mAlphabetLength];
            for (int i = 0; i < mAlphabetLength; i++) {
                mAlphabetArray[i] = Character.toString(ALPHABET.charAt(i));
            }
        }else if(null != mAdapter && !mAdapter.isEmpty()){
            Set<String> IndexKeySets = new HashSet<>();
            for(int i=0; i<mAdapter.getCount(); i++){
                Object item = mAdapter.getItem(i);
                String IndexKey = item instanceof IndexKey? ((IndexKey) item).getIndexKey(): item.toString();
                IndexKeySets.add(StringUtil.getFirstLetter(IndexKey));
            }
            mAlphabetArray = IndexKeySets.toArray(new String[]{});
            Arrays.sort(mAlphabetArray);
        }else {
            mAlphabetArray = new String[]{DEFAULT_ALPHABET};
        }
        return mAlphabetArray;
    }

    /**
     * 根据position找到应该显示的那个Section, 及定位IndexBar的位置.
     * @param position
     * @return
     */
    @Override
    public int getSectionForPosition(int position) {
        int size = mAdapter.getCount();
        if (size > 0) {
            if(null == mAlphabetArray || 1 < mAlphabetArray.length){
                mAlphabetArray = (String[]) getSections();
            }
            position = Math.max(0, Math.min(position, size - 1));
            Object item = mAdapter.getItem(position);
            String IndexKey = item instanceof IndexKey? ((IndexKey) item).getIndexKey(): item.toString();
            String key = StringUtil.getFirstLetter(IndexKey, DEFAULT_ALPHABET);
            if (!StringUtil.isTrimEmpty(key)) {
                return Math.max(0, Arrays.binarySearch(mAlphabetArray, key));
            }
        }
        return 0;
    }

    /**
     * 根据 section找到列表中第一个匹配的元素下标, 即将要定位的位置.
     * @param section
     * @return
     */
    @Override
    public int getPositionForSection(int section) {
        if(null == mAlphabetArray || 1 < mAlphabetArray.length){
            mAlphabetArray = (String[]) getSections();
        }
        section = Math.max(0, Math.min(section, mAlphabetArray.length - 1));
        char sectionKey = mAlphabetArray[section].charAt(0);
        char itemKey;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Object item = mAdapter.getItem(i);
            String IndexKey = item instanceof IndexKey? ((IndexKey) item).getIndexKey(): item.toString();
            itemKey = StringUtil.getFirstLetter(IndexKey, DEFAULT_ALPHABET).charAt(0);
            if (itemKey >= sectionKey) {
                return i;
            }
        }
        return 0;
    }
}
