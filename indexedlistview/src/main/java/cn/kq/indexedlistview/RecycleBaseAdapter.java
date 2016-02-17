package cn.kq.indexedlistview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 复用的 ListView 的 BaseAdapter.
 * Created by zhaopan on 15/8/28 11:45
 * e-mail: kangqiao610@gmail.com
 */
public abstract class RecycleBaseAdapter<E> extends BaseAdapter {

    protected List<E> mDataList;

    @Override
    public int getCount() {
        return null == mDataList ? 0 : mDataList.size();
    }

    @Override
    public E getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        E record = mDataList.get(position);
        RecycleViewHolder holder = null;
        if (null == convertView) {
            holder = onCreateViewHolder(parent);
            convertView = holder.getConvertView();
        } else {
            holder = (RecycleViewHolder) convertView.getTag();
        }
        holder.update(record, position);
        return convertView;
    }

    protected abstract RecycleViewHolder onCreateViewHolder(ViewGroup parent);

    public void setDataList(List dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    /**
     * RecycleBaseAdapter增加对JSONArray的支持.
     */
    public void setDataList(JSONArray jarr) {
        if (null != jarr && jarr.length() > 0) {
            List<JSONObject> list = new ArrayList<>(jarr.length());
            for (int i = 0; i < jarr.length(); i++) {
                list.add(jarr.optJSONObject(i));
            }
            setDataList(list);
        }
    }

    public void addDataList(JSONArray jarr) {
        if (null != jarr && jarr.length() > 0) {
            List<JSONObject> list = new ArrayList<>(jarr.length());
            for (int i = 0; i < jarr.length(); i++) {
                list.add(jarr.optJSONObject(i));
            }
            addDataList(list);
        }
    }

    public void addDataList(List dataList) {
        if (null == mDataList) {
            mDataList = dataList;
        } else if (!dataList.isEmpty()) {
            mDataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    public List<E> getDataList() {
        return mDataList;
    }

    public static abstract class RecycleViewHolder<E> {
        protected RecycleBaseAdapter<E> adapter;
        protected View mParent;

        public RecycleViewHolder(View parent) {
            this(parent, null);
        }

        public RecycleViewHolder(View parent, RecycleBaseAdapter<E> adapter) {
            mParent = parent;
            mParent.setTag(this);
            this.adapter = adapter;
        }

        protected <T extends View> T findView(int id) {
            return (T) mParent.findViewById(id);
        }

        public View getConvertView() {
            return mParent;
        }

        protected void update(E record, int position) {
        }
    }
}
