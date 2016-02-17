package cn.kq.indexedlistview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        RecycleBaseAdapter<ItemStr> mAdapter = new RecycleBaseAdapter<ItemStr>() {
            @Override
            protected RecycleViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_str_layout, parent, false);
                return new ViewHolder(view, this);
            }
        };

        mListView = (IndexedListView) findViewById(R.id.indexed_listView);
        mListView.setAdapter(mAdapter);
        mAdapter.setDataList(mItems);
    }

    private class ViewHolder extends RecycleBaseAdapter.RecycleViewHolder<ItemStr> {
        TextView tvItem;

        public ViewHolder(View view, RecycleBaseAdapter<ItemStr> adapter) {
            super(view, adapter);
            tvItem = findView(R.id.tv_item_str);
        }

        @Override
        protected void update(ItemStr record, int position) {
            tvItem.setText(record.getItem());
        }
    }

    public static class ItemStr implements IndexedListView.IndexKey{
        String item;

        public ItemStr() {}

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
