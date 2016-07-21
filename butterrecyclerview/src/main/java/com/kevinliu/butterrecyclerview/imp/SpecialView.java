package com.kevinliu.butterrecyclerview.imp;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kevin Liu on 2016/7/21.
 * Function：特殊的View 如Footer或者Header
 */
public interface SpecialView {

    View onCreateView(ViewGroup parent);

    void onBindView(View headerView);
}
