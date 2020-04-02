# Kongzue UpdateUI
使用 PopupWindow 为基础构建非打扰式更新提醒组件

<a href="https://github.com/kongzue/UpdateUI">
<img src="https://img.shields.io/badge/Kongzue%UpdateUI-1.0.3-green.svg" alt="Kongzue UpdateUI">
</a> 
<a href="https://bintray.com/myzchh/maven/UpdateUI/1.0.3/link">
<img src="https://img.shields.io/badge/Maven-1.0.3-blue.svg" alt="Maven">
</a> 
<a href="http://www.apache.org/licenses/LICENSE-2.0">
<img src="https://img.shields.io/badge/License-Apache%202.0-red.svg" alt="License">
</a> 
<a href="http://www.kongzue.com">
<img src="https://img.shields.io/badge/Homepage-Kongzue.com-brightgreen.svg" alt="Homepage">
</a>

![Kongzue UpdateUI](https://github.com/kongzue/Res/raw/master/app/src/main/res/mipmap-xxxhdpi/img_update_demo.png)

### Kongzue UpdateUI 是什么
· UpdateUI可以为您提供非打扰式的更新提示、更新过程以及下载完成三步界面；

· 这个框架是一个 UI 框架，并不包含更新或安装功能，要使用更新功能请移步 https://github.com/kongzue/KongzueUpdateSDK

· 提供亮色和暗色两种可选样式，方便实现更新 UI 逻辑。

### 开始使用
Maven仓库：
```
<dependency>
  <groupId>com.kongzue.updateui</groupId>
  <artifactId>updateui</artifactId>
  <version>1.0.3</version>
  <type>pom</type>
</dependency>
```
Gradle：
在dependencies{}中添加引用：
```
implementation 'com.kongzue.updateui:updateui:1.0.3'
```

在代码中构建：
```
UpdateUI updateUI;
//初始化
updateUI = new UpdateUI();
```

建议在 Activity 的 onDestroy() 中添加卸载方法：
```
@Override
protected void onDestroy() {
    super.onDestroy();
    if (updateUI != null) {
        updateUI.dismiss();
    }
}
```

### 预配置
修改默认提示文案
```
UpdateUI.beforeUpdateTitle = "有新版本可用";
UpdateUI.afterUpdateTitle = "安装包已就绪";
UpdateUI.buttonUpdateDetails = "查看详情";
UpdateUI.buttonUpdateNow = "立即更新";
UpdateUI.txtUpdateLogs = "- 这里是一些更新日志...\n- 这里是一些更新日志...\n- 这里是一些更新日志...";
UpdateUI.buttonInstallNow = "立即安装";
```

调整暗色模式：
```
UpdateUI.darkMode = true;
```

状态回调方法配置：
```
updateUI.setOnUpdateUIEventListener(new OnUpdateUIEventListener() {
    @Override
    public void onShow(int status) {
        //用于返回界面状态的回调
        //status 值解释如下：
        //status = STATUS_BEFORE_UPDATE：处于默认状态
        //status = STATUS_BEFORE_UPDATE_LOGS：打开更新日志详情的状态
        //status = STATUS_PROGRESSING：正在更新状态
        //status = STATUS_FINISH：完成更新状态
    }
    
    @Override
    public void onStartDownload() {
        //此方法回调时说明用户点击了“立即更新”按钮，应开始执行下载过程
        //此时可使用 updateUI.setProgress(进度); 来更新 updateUI 的进度显示，取值范围 0~100
        //当更新完成时请调用 updateUI.readyInstall(); 来变更 updateUI 为待安装界面
    }
    
    @Override
    public void onIgnore(int status) {
        //用户上划界面忽略更新提示时执行
        //if (status == UpdateUI.STATUS_PROGRESSING) {
        //    正在下载的状态时产生的忽略
        //    您可以在下载完成后调用以下语句重新弹出安装提示：
        //    updateUI.readyInstall();
        //    updateUI.showUpdateUI(me);
        //}
        //if (status == UpdateUI.STATUS_FINISH) {
        //    //完成后产生的忽略操作
        //}
    }
    
    @Override
    public void onInstallNow() {
        //点击立即安装后的回调
    }
});
```

### 显示一个更新提示：
```
updateUI.showUpdateUI(me);
```

## 开源协议
```
Copyright Kongzue UpdateUI

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 更新日志：
暂无