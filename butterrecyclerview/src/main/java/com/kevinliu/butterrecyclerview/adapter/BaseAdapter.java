package com.kevinliu.butterrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevinliu.butterrecyclerview.imp.OnLoadMoreListener;
import com.kevinliu.butterrecyclerview.imp.SpecialView;
import com.kevinliu.butterrecyclerview.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Kevin Liu on 2016/7/21.
 * Function：底层的Adapter，数据的增加删除都由这个来处理
 */
abstract public class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    /**
     * 该ArrayAdapter的数据对象的列表。
     */
    private List<T> mObjects;

    private ArrayList<SpecialView> headers = new ArrayList<>();
    private ArrayList<SpecialView> footers = new ArrayList<>();
    //加载更多的View
    private SpecialView moreView;
    //没有更多的View
    private SpecialView noMoreView;
    private OnLoadMoreListener onLoadMoreListener;

    private boolean isLoadingMore = false;

    //progress显示时不应该loadMore，默认为true，因为一开始在progress
    private boolean isProgressShow = true;

    /**
     * 锁定要修改的{@link #mObjects}
     */
    private final Object mLock = new Object();


    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;

    private Context mContext;

    /**
     * Constructor
     *
     * @param context The current context.
     */
    public BaseAdapter(Context context) {
        init(context, new ArrayList<T>());
    }


    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public BaseAdapter(Context context, T[] objects) {
        init(context, Arrays.asList(objects));
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public BaseAdapter(Context context, List<T> objects) {
        init(context, objects);
    }


    /**
     * Adds the specified object at the end of the array.
     * 新增了addData()进行处理
     * @param object
     */
    public void add(T object) {
        dealView();
        synchronized (mLock) {
            mObjects.add(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     * 新增了addData()进行处理
     * @param collection
     */
    public void addAll(Collection<? extends T> collection) {
        dealView();
        if (collection == null || collection.size() == 0) {
            return;
        }
        synchronized (mLock) {
            mObjects.addAll(collection);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the array.
     * 新增了addData()进行处理
     * @param items The items to add at the end of the array.
     */
    public void addAll(T... items) {
        dealView();
        if (items == null || items.length == 0) {
            return;
        }
        synchronized (mLock) {
            Collections.addAll(mObjects, items);

        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * 添加HeaderView
     * @param view HeaderView
     */
    public void addHeader(SpecialView view) {
        if (view != null)
            headers.add(view);
    }

    /**
     * 添加FooterView
     * @param view FooterView
     */
    public void addFooter(SpecialView view) {
        if (view != null)
            footers.add(view);
    }
    /**
     * 移除HeaderView
     * @param view HeaderView
     */
    public void removeHeader(SpecialView view) {
        headers.remove(view);
        notifyDataSetChanged();
    }
    /**
     * 移除FooterView
     * @param view FooterView
     */
    public void removeFooter(SpecialView view) {
        footers.remove(view);
        notifyDataSetChanged();
    }

    /**
     *处理一下加载更多结束后，footer和header的问题
     */
    private void dealView() {
        isLoadingMore = false;
        if (isProgressShow) {
            if (moreView == null && noMoreView != null && !footers.contains(noMoreView)) {
                footers.add(noMoreView);
            }

            if (moreView != null)
                footers.add(moreView);
            isProgressShow = false;
        }
    }

    /**
     * 设置加载更多
     * @param res
     * @param listener
     */
    public void setMore(final int res, final OnLoadMoreListener listener) {
        setMore(new SpecialView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return LayoutInflater.from(getContext()).inflate(res, parent, false);
            }

            @Override
            public void onBindView(View headerView) {
                askMoreView();
            }
        }, listener);
    }


    public void setMore(final View view, OnLoadMoreListener listener) {
        setMore(new SpecialView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return view;
            }

            @Override
            public void onBindView(View headerView) {
                askMoreView();
            }
        }, listener);
    }

    /**
     * 将{@link #moreView}和{@link #onLoadMoreListener}与设置进来的数据进行绑定
     * @param view
     * @param listener
     */
    private void setMore(SpecialView view, OnLoadMoreListener listener) {
        this.moreView = view;
        this.onLoadMoreListener = listener;
    }

    /**
     * 设置没有更多
     * @param res
     */
    public void setNoMore(final int res) {
        setNoMore(new SpecialView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return LayoutInflater.from(getContext()).inflate(res, parent, false);
            }

            @Override
            public void onBindView(View headerView) {
            }
        });
    }

    /**
     * 设置没有更多
     * @param view
     */
    public void setNoMore(final View view) {
        setNoMore(new SpecialView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return view;
            }

            @Override
            public void onBindView(View headerView) {
            }
        });
    }

    /**
     *将{@link #noMoreView}与设置进来的数据进行绑定
     * @param view
     */
    private void setNoMore(SpecialView view) {
        this.noMoreView = view;
    }

    private void askMoreView() {
        if (moreView != null & !isLoadingMore) {
            isLoadingMore = true;
            onLoadMoreListener.onLoadMore();
        }
    }

    public void stopMore() {
        isLoadingMore = false;
        if (moreView != null)
            footers.remove(moreView);
        if (noMoreView != null && !footers.contains(noMoreView))
            footers.add(noMoreView);
        notifyDataSetChanged();
    }


    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
            mObjects.add(index, object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        synchronized (mLock) {
            mObjects.remove(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        footers.remove(noMoreView);
        footers.remove(moreView);
        isProgressShow = true;
        synchronized (mLock) {
            mObjects.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *                   in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            Collections.sort(mObjects, comparator);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    /**
     * Control whether methods that change the list ({@link #add},
     * {@link #insert}, {@link #remove}, {@link #clear}) automatically call
     * {@link #notifyDataSetChanged}.  If set to false, caller must
     * manually call notifyDataSetChanged() to have the changes
     * reflected in the attached view.
     * <p/>
     * The default is true, and calling notifyDataSetChanged()
     * resets the flag to true.
     *
     * @param notifyOnChange if true, modifications to the list will
     *                       automatically call {@link
     *                       #notifyDataSetChanged}
     */
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    /**
     * 对数据的一些初始化操作
     * @param context
     * @param objects
     */
    private void init(Context context, List<T> objects) {
        mContext = context;
        mObjects = objects;
    }


    /**
     * Returns the context associated with this array adapter. The context is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return mContext;
    }


    /**
     * @return修改后这个函数包含了头部和尾部view的个数，不是真正的item个数。
     */
    @Deprecated
    @Override
    public final int getItemCount() {
        return mObjects.size() + headers.size() + footers.size();
    }

    /**
     * @return获取item个数而不是{@link #getItemCount()}
     */
    public int getCount() {
        return mObjects.size();
    }

    /**
     * 调用ItemView 的onCreateView的方法，返回一个view
     * @param parent
     * @param viewType
     * @return
     */
    private View createSpViewByType(ViewGroup parent, int viewType) {
        for (SpecialView headerView : headers) {
            if (headerView.hashCode() == viewType) {
                View view = headerView.onCreateView(parent);
                return view;
            }
        }
        for (SpecialView footerview : footers) {
            if (footerview.hashCode() == viewType) {
                View view = footerview.onCreateView(parent);
                return view;
            }
        }
        return null;
    }

    @Override
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createSpViewByType(parent, viewType);
        if (view != null) {
            return new StateViewHolder(view);
        }
        return OnCreateViewHolder(parent, viewType);
    }

    abstract public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType);


    @Override
    public final void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setId(position);
        if (headers.size() != 0 && position < headers.size()) {
            headers.get(position).onBindView(holder.itemView);
            return;
        }

        int i = position - headers.size() - mObjects.size();
        if (footers.size() != 0 && i >= 0) {
            footers.get(i).onBindView(holder.itemView);
            return;

        }
        OnBindViewHolder(holder, position - headers.size());
    }

    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    @Override
    public final int getItemViewType(int position) {
        if (headers.size() != 0) {
            if (position < headers.size()) return headers.get(position).hashCode();
        }
        if (footers.size() != 0) {

            int i = position - headers.size() - mObjects.size();
            if (i >= 0) {
                return footers.get(i).hashCode();
            }
        }
        return getViewType(position);
    }

    public int getViewType(int position) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public T getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(T item) {
        return mObjects.indexOf(item);
    }

    /**
     * {@inheritDoc}
     */
    public long getItemId(int position) {
        return position;
    }

    private class StateViewHolder extends BaseViewHolder {

        public StateViewHolder(View itemView) {
            super(itemView);
        }
    }
}

