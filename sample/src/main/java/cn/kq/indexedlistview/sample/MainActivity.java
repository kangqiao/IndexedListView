package cn.kq.indexedlistview.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.kq.indexedlistview.IndexedListView;
import cn.kq.indexedlistview.RecycleBaseAdapter;
import cn.kq.indexedlistview.StringUtil;

/**
 * Created by zhaopan on 16/1/23 10:13
 * e-mail: kangqiao610@gmail.com
 */
public class MainActivity extends AppCompatActivity {

    private List<ItemStr> mItems;
    private IndexedListView mListView;
    private IndexedListView mListView2;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItems = new ArrayList<ItemStr>();
        mItems.add(new ItemStr("我想和你虚度时光 - 程璧"));
        mItems.add(new ItemStr("词：李元胜"));
        mItems.add(new ItemStr("曲：程璧 / 莫西子诗"));
        mItems.add(new ItemStr("我想和你虚度时光"));
        mItems.add(new ItemStr("比如低头看鱼"));
        mItems.add(new ItemStr("比如把茶杯留在桌子上离开"));
        mItems.add(new ItemStr("浪费它们好看的阴影"));
        mItems.add(new ItemStr("我还想连落日一起浪费"));
        mItems.add(new ItemStr("比如散步"));
        mItems.add(new ItemStr("一直消磨到星光满天"));
        mItems.add(new ItemStr("一起虚度短的沉默长的无意义"));
        mItems.add(new ItemStr("@程璧 程璧作品官方主页：http://www.annapatio.com"));
        mItems.add(new ItemStr("www.annapatio.com"));
        mItems.add(new ItemStr("China 中国大陆"));
        mItems.add(new ItemStr("http://www.xiami.com/song/1773990515?spm=a1z1s.6632057.350708705.1.znqF0v"));
        mItems.add(new ItemStr("1234567890"));
        mItems.add(new ItemStr("0987654321"));
        mItems.add(new ItemStr("$$$$$$$$$$"));
        mItems.add(new ItemStr("##########"));
        mItems.add(new ItemStr("----------"));
        mItems.add(new ItemStr("`!@39(<)"));
        mItems.add(new ItemStr("しいよあの子 (那位令我／日夜思念的人儿啊)"));
        mItems.add(new ItemStr("歌を歌よ (只剩一首歌)"));
        mItems.add(new ItemStr("鳥が鳴く (鸟儿鸣叫)"));
        mItems.add(new ItemStr("これがわが家 (这里是我的家)"));
        mItems.add(new ItemStr("河の音 (川之音)"));
        mItems.add(new ItemStr("鳥が鳴く (鸟儿鸣叫)"));
        mItems.add(new ItemStr("懐かしくて (让人心生眷恋)"));
        mItems.add(new ItemStr("／日子走远)"));
        mItems.add(new ItemStr("歌を歌よ (只剩一首歌)"));
        mItems.add(new ItemStr("知らずに日がすんでゆく (不知不觉间"));
        mItems.add(new ItemStr("あの子はいない (你究竟在哪里呢)"));
        Collections.sort(mItems, new Comparator<ItemStr>() {
            @Override
            public int compare(ItemStr lhs, ItemStr rhs) {
                return lhs.getIndexKey().compareTo(rhs.getIndexKey());
            }
        });

        final RecycleBaseAdapter<ItemStr> mAdapter = new RecycleBaseAdapter<ItemStr>() {
            @Override
            protected RecycleViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_str_layout, parent, false);
                return new ViewHolder(view, this);
            }
        };

        /*mListView = (IndexedListView) findViewById(R.id.indexed_listView);
        mListView.setAdapter(mAdapter);*/

        mListView2 = (IndexedListView) findViewById(R.id.indexed_listView2);
        mListView2.getBuilder()
                .setIsShowAll(true) //设置索引条是否全索引显示.
                .setIndexBarBgColor(this.getResources().getColor(R.color.indexBar_bg))
                .setPreviewBgColor(this.getResources().getColor(R.color.preview_bg))
                .setIndexBarMargin(5) //设置索引条外边距5dp. 若按像素处理请使用float类型数据.
                .setSectionTextSize(100)  //设置索引条文本字体大小, 如果由于索引条长度越界会自动按ListView高度/索引项总数确定每个索引文本的大小.
                .setPreviewTextPadding(30) //设置预览文本字体内边距, 整数30dp.
                .setPreviewTextSize(800) //设置预览文本字体的大小, 如果越界, 会自动按ListView的长宽最小值的2:8设置预览文本的大小.
                .build();
        mListView2.setAdapter(mAdapter);

        /*
        //设置SectionIndexer创建者.
        mListView2.setSectionIndexerCreator(new IndexedListView.SectionIndexerCreator() {
            @Override
            public SectionIndexer createSectionIndexer(ListAdapter adapter, boolean isShowAllSupportedSections) {
                return new DefaultAlphabetIndexer(adapter, isShowAllSupportedSections);
            }
        });*/

        mAdapter.setDataList(mItems);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*mListView2.getBuilder()
                        .setShowIndexBar(false)  //设置不显示索引条.
                        .build();*/
                mListView2.setVisibility(View.GONE);
            }
        }, 3000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView2.setVisibility(View.VISIBLE);
                mListView2.getBuilder()
                        .setShowIndexBar(true)  //设置显示索引条.
                        .setIndexBarBgColor(Color.argb(0, 0, 0, 0))  //设置索引条背景透明.
                        .setSectionTextSize(12)  //设置索引文本字体大小.
                        .setSectionTextColor(Color.GRAY)  //设置索引文本字体颜色.
                        .setIsShowPreview(false)
                        .setPreviewTextPadding(50)  //设置预览文本内边距.
                        .setPreviewTextColor(Color.BLUE)  //设置预览文本字体颜色.
                        .setIsShowAll(false)  //设置索引条是否全索引显示.
                        .build(); //构建你的索引条.
            }
        }, 8000);
    }

    private class ViewHolder extends RecycleBaseAdapter.RecycleViewHolder<ItemStr> {
        TextView tvItem;
        CardView cardLetter;
        TextView tvLetter;

        public ViewHolder(View view, RecycleBaseAdapter<ItemStr> adapter) {
            super(view, adapter);
            tvItem = findView(R.id.tv_item_str);
            cardLetter = findView(R.id.card_letter);
            tvLetter = findView(R.id.tv_letter);
        }

        @Override
        protected void update(ItemStr record, int position) {
            SectionIndexer indexer = mListView2.getSectionIndexer();
            if (null != indexer) {
                int section = indexer.getSectionForPosition(position);
                if (position == indexer.getPositionForSection(section)) {
                    cardLetter.setVisibility(View.VISIBLE);
                    tvLetter.setText(record.getIndexKey());
                } else {
                    cardLetter.setVisibility(View.GONE);
                }
            }
            tvItem.setText(record.getItem());
        }
    }

    public static class ItemStr implements IndexedListView.IndexKey {
        String item;

        public ItemStr() {
        }

        public ItemStr(String item) {
            this.item = item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getItem() {
            return item;
        }

        @Override
        public String getIndexKey() {
            return StringUtil.getFirstLetter(item);
        }

        @Override
        public String toString() {
            return item;
        }
    }
}
