package com.kongzue.updateui.util;

import android.view.View;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2018/7/5 17:06
 */
public class ViewWrapper {
    
    private View mTargetView;
    
    public ViewWrapper(View target) {
        mTargetView = target;
    }
    
    public int getWidth() {
        return mTargetView.getLayoutParams().width;
    }
    
    public int getHeight() {
        return mTargetView.getLayoutParams().height;
    }
    
    public void setWidth(int width) {
        mTargetView.getLayoutParams().width = width;
        mTargetView.requestLayout();
    }
    
    public void setWidth(float width) {
        mTargetView.getLayoutParams().width = (int) width;
        mTargetView.requestLayout();
    }
    
    public void setHeight(int height) {
        mTargetView.getLayoutParams().height = height;
        mTargetView.requestLayout();
    }
    
    public void setHeight(float height) {
        mTargetView.getLayoutParams().height = (int) height;
        mTargetView.requestLayout();
    }
}
