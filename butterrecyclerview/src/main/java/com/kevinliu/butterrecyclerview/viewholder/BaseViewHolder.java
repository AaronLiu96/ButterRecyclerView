package com.kevinliu.butterrecyclerview.viewholder;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kevin Liu on 2016/7/19.
 * Function：在ViewHolder里面操作显示的数据
 *
 * 包括findviewbyid、XX.setText() ......等等
 *
 * 将ViewHolder解耦，减少了Adapter的代码量
 */
public class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }


    public BaseViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
}

    /**
     * 用来设置显示，被继承后直接复写就OK
     * @param data
     */
    public void setData(T data) {
    }


    /**
     * 简化findviewbyId
     * @param id R.id.XXXX
     * @param <T>
     * @return
     */
    protected <T extends View> T $find(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }
}
