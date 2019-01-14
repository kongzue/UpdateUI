package com.kongzue.updateui.interfaces;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/14 17:20
 */
public interface OnUpdateUIEventListener {
    
    void onShow(int status);
    
    void onStartDownload();
    
    void onIgnore(int status);
    
    void onInstallNow();
    
}
