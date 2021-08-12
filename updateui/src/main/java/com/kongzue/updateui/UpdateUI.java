package com.kongzue.updateui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kongzue.updateui.interfaces.OnUpdateUIEventListener;
import com.kongzue.updateui.util.ViewWrapper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/13 15:06
 */
public class UpdateUI {
    
    public static final int STATUS_BEFORE_UPDATE = 0;                   //默认状态
    public static final int STATUS_BEFORE_UPDATE_LOGS = 1;              //打开更新日志详情的状态
    public static final int STATUS_PROGRESSING = 2;                     //正在更新状态
    public static final int STATUS_FINISH = 3;                          //完成更新状态
    
    private int status = STATUS_BEFORE_UPDATE;                          //当前状态
    
    public static String beforeUpdateTitle = "有新版本可用";
    public static String afterUpdateTitle = "安装包已就绪";
    public static String buttonUpdateDetails = "查看详情";
    public static String buttonUpdateNow = "立即更新";
    public static String txtUpdateLogs = "- 这里是一些更新日志...\n- 这里是一些更新日志...\n- 这里是一些更新日志...";
    public static String buttonInstallNow = "立即安装";
    public static boolean darkMode = false;                             //暗色模式
    
    private PopupWindow window;
    private WeakReference<Activity> activity;
    private View rootView;
    
    private OnUpdateUIEventListener onUpdateUIEventListener;            //事件监听器
    
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    
    private LinearLayout boxUpdateUIAllViews;
    private LinearLayout sysStatusBar;
    private LinearLayout boxUpdateUISimple;
    private TextView txtUpdateUITitle;
    private ProgressBar progressUpdateUI;
    private TextView btnUpdateUIDetails;
    private TextView btnUpdateUIInstall;
    private ImageView imgUpdateUIMore;
    private LinearLayout boxUpdateUIMore;
    private TextView txtUpdateUILogs;
    private TextView btnUpdateUIUpdateNow;
    
    private int moreHeight;
    private int simpleHeight;
    
    private void bindChildViews(View rootView) {
        boxUpdateUIAllViews = rootView.findViewById(R.id.box_updateUI_allViews);
        sysStatusBar = rootView.findViewById(R.id.sys_statusBar);
        boxUpdateUISimple = rootView.findViewById(R.id.box_updateUI_Simple);
        txtUpdateUITitle = rootView.findViewById(R.id.txt_updateUI_title);
        progressUpdateUI = rootView.findViewById(R.id.progress_updateUI);
        btnUpdateUIDetails = rootView.findViewById(R.id.btn_updateUI_details);
        btnUpdateUIInstall = rootView.findViewById(R.id.btn_updateUI_install);
        imgUpdateUIMore = rootView.findViewById(R.id.img_updateUI_more);
        boxUpdateUIMore = rootView.findViewById(R.id.box_updateUI_more);
        txtUpdateUILogs = rootView.findViewById(R.id.txt_updateUI_logs);
        btnUpdateUIUpdateNow = rootView.findViewById(R.id.btn_updateUI_updateNow);
    }
    
    public void showUpdateUI(Activity a) {
        dismiss();
        activity = new WeakReference<>(a);
        
        rootView = LayoutInflater.from(activity.get()).inflate(R.layout.layout_update_ui, null, false);
        
        //绑定组件
        bindChildViews(rootView);
        
        txtUpdateUITitle.setText(beforeUpdateTitle);
        btnUpdateUIDetails.setText(buttonUpdateDetails);
        btnUpdateUIUpdateNow.setText(buttonUpdateNow);
        txtUpdateUILogs.setText(txtUpdateLogs);
        btnUpdateUIInstall.setText(buttonInstallNow);
        
        boxUpdateUIAllViews.post(new Runnable() {
            @Override
            public void run() {
                simpleHeight = measureHeight(boxUpdateUIAllViews);
            }
        });
        
        //初始化
        window = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(false);
        window.setTouchable(true);
        window.setClippingEnabled(false);
        
        window.setAnimationStyle(R.style.popwin_anim_style);
        
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onUpdateUIEventListener != null) onUpdateUIEventListener.onIgnore(status);
            }
        });
        
        //全屏化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(window, true);
                
                Field mClipToScreen = PopupWindow.class.getDeclaredField("mClipToScreen");
                mClipToScreen.setAccessible(true);
                mClipToScreen.set(window, true);
            } catch (Exception e) {
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sysStatusBar.setVisibility(View.VISIBLE);
            int statusHeight = getStatusBarHeight();
            ViewGroup.LayoutParams params = sysStatusBar.getLayoutParams();
            params.height = statusHeight;
            sysStatusBar.setLayoutParams(params);
        }
        
        window.showAtLocation(activity.get().getWindow().getDecorView(), Gravity.TOP, 0, 0);
        
        switch (status) {
            case STATUS_PROGRESSING:
                progressUpdateUI.setVisibility(View.VISIBLE);
                txtUpdateUITitle.setVisibility(View.GONE);
                openMoreEnable = false;
                progressUpdateUI.setProgress(progress);
                break;
            case STATUS_FINISH:
                openMoreEnable = false;
                readyInstall();
                break;
        }
        
        if (onUpdateUIEventListener != null) onUpdateUIEventListener.onShow(status);
        
        if (darkMode) {
            boxUpdateUIAllViews.setBackgroundResource(R.drawable.img_update_ui_bkg_dark);
            txtUpdateUILogs.setTextColor(Color.WHITE);
            btnUpdateUIUpdateNow.setBackgroundResource(R.drawable.button_update_ui_button_dark);
            progressUpdateUI.setProgressDrawable(activity.get().getResources().getDrawable(R.drawable.progress_bar_update_ui_dark));
        }
        
        setEvents();
    }
    
    private boolean isTouchDown = false;
    private float touchDownY;
    
    private void setEvents() {
        boxUpdateUIAllViews.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDownY = event.getY();
                    isTouchDown = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (isTouchDown) {
                        float deltaY = event.getY() - touchDownY;
                        float aimY = boxUpdateUIAllViews.getY() + deltaY;
                        if (aimY <= 0) {
                            boxUpdateUIAllViews.setY(aimY);
                            if (imgUpdateUIMore.getVisibility() == View.VISIBLE)
                                imgUpdateUIMore.setVisibility(View.GONE);
                        } else {
                            if (imgUpdateUIMore.getVisibility() == View.GONE && openMoreEnable)
                                imgUpdateUIMore.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    isTouchDown = false;
                    float deltaY = event.getY() - touchDownY;
                    if (boxUpdateUIAllViews.getY() < -dip2px(15)) {
                        window.dismiss();
                    } else {
                        if (deltaY > dip2px(15)) {
                            showDetails(true);
                        }
                    }
                }
                return true;
            }
        });
        
        btnUpdateUIDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails(true);
            }
        });
        
        btnUpdateUIUpdateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = STATUS_PROGRESSING;
                progressUpdateUI.setVisibility(View.VISIBLE);
                txtUpdateUITitle.setVisibility(View.GONE);
                openMoreEnable = false;
                showDetails(false);
                if (onUpdateUIEventListener != null) onUpdateUIEventListener.onStartDownload();
            }
        });
        
        btnUpdateUIInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onUpdateUIEventListener != null) onUpdateUIEventListener.onInstallNow();
            }
        });
    }
    
    private Timer timer;
    private int progress;
    
    private void doCheckProgress() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progress = progress + 5;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progress < 100) {
                            progressUpdateUI.setProgress(progress);
                        } else {
                            readyInstall();
                        }
                    }
                });
            }
        }, 100, 100);
    }
    
    public void readyInstall() {
        status = STATUS_FINISH;
        txtUpdateUITitle.setText(afterUpdateTitle);
        txtUpdateUITitle.setVisibility(View.VISIBLE);
        btnUpdateUIDetails.setVisibility(View.GONE);
        btnUpdateUIInstall.setVisibility(View.VISIBLE);
        progressUpdateUI.setVisibility(View.GONE);
    }
    
    private boolean openMoreEnable = true;
    
    private Runnable runnable;
    
    private void showDetails(boolean visible) {
        if (visible) {
            if (!openMoreEnable) {
                return;
            }
            btnUpdateUIDetails.setVisibility(View.GONE);
            if (boxUpdateUIMore.getVisibility() == View.GONE) {
                boxUpdateUIMore.setVisibility(View.VISIBLE);
                
                measureHeight(txtUpdateUILogs);
                
                if (moreHeight == 0) {
                    txtUpdateUILogs.post(new Runnable() {
                        @Override
                        public void run() {
                            int height = getTextViewHeight(txtUpdateUILogs);
                            new ViewWrapper(txtUpdateUILogs).setHeight(height);
                            
                            boxUpdateUIMore.post(new Runnable() {
                                @Override
                                public void run() {
                                    boxUpdateUIMore.setAlpha(1f);
                                    moreHeight = measureHeight(boxUpdateUIMore);
    
                                    final ViewWrapper boxUpdateUIAllViewViewWrapper = new ViewWrapper(boxUpdateUIAllViews);
                                    moveAnimation(boxUpdateUIAllViewViewWrapper, "height",
                                            simpleHeight,
                                            simpleHeight + moreHeight,
                                            300, 0
                                    );
                                    
                                    new ViewWrapper(window.getContentView()).setHeight(simpleHeight + moreHeight + dip2px(60));
                                }
                            });
                        }
                    });
                } else {
                    boxUpdateUIMore.setAlpha(1f);
                    final ViewWrapper boxUpdateUIAllViewViewWrapper = new ViewWrapper(boxUpdateUIAllViews);
                    moveAnimation(boxUpdateUIAllViewViewWrapper, "height",
                            simpleHeight,
                            simpleHeight + moreHeight,
                            300, 0
                    );
                    
                    new ViewWrapper(window.getContentView()).setHeight(simpleHeight + moreHeight + dip2px(60));
                }
                
            }
            if (status < STATUS_PROGRESSING) status = STATUS_BEFORE_UPDATE_LOGS;
        } else {
            boxUpdateUIMore.setVisibility(View.GONE);
            final ViewWrapper boxUpdateUIAllViewViewWrapper = new ViewWrapper(boxUpdateUIAllViews);
            moveAnimation(boxUpdateUIAllViewViewWrapper, "height",
                    simpleHeight + moreHeight,
                    simpleHeight,
                    300, 0
            );
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boxUpdateUIAllViewViewWrapper.setHeight(simpleHeight);
                    new ViewWrapper(window.getContentView()).setHeight(simpleHeight + dip2px(60));
                }
            }, 300);
            if (status < STATUS_PROGRESSING) status = STATUS_BEFORE_UPDATE;
        }
    }
    
    private int getStatusBarHeight() {
        try {
            Resources resources = activity.get().getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int dip2px(float dpValue) {
        final float scale = activity.get().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    private ObjectAnimator moveAnimation(ViewWrapper obj, String perference, float fromValue, float aimValue, long time, long delay) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(obj, perference, fromValue, aimValue);
        objectAnimator.setDuration(time);
        objectAnimator.setStartDelay(delay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            objectAnimator.setAutoCancel(true);
        }
        objectAnimator.start();
        return objectAnimator;
    }
    
    private int measureHeight(View v) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(width, height);
        return v.getMeasuredHeight();
    }
    
    private int getTextViewHeight(TextView pTextView) {
        Layout layout = pTextView.getLayout();
        int desired = layout.getLineTop(pTextView.getLineCount());
        int padding = pTextView.getCompoundPaddingTop() + pTextView.getCompoundPaddingBottom();
        return desired + padding;
    }
    
    public void dismiss() {
        if (window != null) window.dismiss();
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setProgress(int progress) {
        if (progressUpdateUI != null) progressUpdateUI.setProgress(progress);
    }
    
    public OnUpdateUIEventListener getOnUpdateUIEventListener() {
        return onUpdateUIEventListener;
    }
    
    public UpdateUI setOnUpdateUIEventListener(OnUpdateUIEventListener onUpdateUIEventListener) {
        this.onUpdateUIEventListener = onUpdateUIEventListener;
        return this;
    }
    
    public void createNewUpdate(Activity a) {
        status = STATUS_BEFORE_UPDATE;
        showUpdateUI(a);
    }
}
