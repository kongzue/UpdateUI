package com.kongzue.updatedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kongzue.baseframework.BaseActivity;
import com.kongzue.baseframework.interfaces.DarkStatusBarTheme;
import com.kongzue.baseframework.interfaces.Layout;
import com.kongzue.baseframework.util.JumpParameter;
import com.kongzue.updateui.UpdateUI;
import com.kongzue.updateui.interfaces.OnUpdateUIEventListener;

@Layout(R.layout.activity_main)
@DarkStatusBarTheme(true)
public class MainActivity extends BaseActivity {
    
    private Button btnShowUpdate;
    private UpdateUI updateUI;
    
    @Override
    public void initViews() {
        btnShowUpdate = findViewById(R.id.btn_showUpdate);
    }
    
    @Override
    public void initDatas(JumpParameter paramer) {
        UpdateUI.darkMode = false;
        UpdateUI.txtUpdateLogs="-\n-\n-\n-\n-\n-\n";
        updateUI = new UpdateUI();
        updateUI.setOnUpdateUIEventListener(new OnUpdateUIEventListener() {
            @Override
            public void onShow(int status) {
                log("onShow:" + status);
            }
            
            @Override
            public void onStartDownload() {
                log("onStartDownload");
                runOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateUI.setProgress(50);
                        runOnMainDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateUI.readyInstall();
                            }
                        }, 1000);
                    }
                }, 1000);
            }
            
            @Override
            public void onIgnore(int status) {
                log("onIgnore:" + status);
                //if (status == UpdateUI.STATUS_PROGRESSING) {
                //    //正在下载的状态时产生的忽略
                //    runOnMainDelayed(new Runnable() {
                //        @Override
                //        public void run() {
                //            updateUI.readyInstall();
                //            updateUI.showUpdateUI(me);
                //        }
                //    }, 2000);
                //}
                //if (status == UpdateUI.STATUS_FINISH) {
                //    //完成后产生的忽略操作
                //}
            }
            
            @Override
            public void onInstallNow() {
                log("onInstallNow");
            }
        });
        
        btnShowUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI.showUpdateUI(me);
            }
        });
    }
    
    @Override
    public void setEvents() {
    
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateUI != null) {
            updateUI.dismiss();
        }
    }
}
