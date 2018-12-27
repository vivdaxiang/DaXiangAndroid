package com.daxiang.android.utils;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.daxiang.android.R;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;


/**
 * 无障碍服务Util；
 * Created by daxiang on 2018/11/8.
 */
public class AccessibilityServiceHelper {

    private static final String TAG = AccessibilityServiceHelper.class.getSimpleName();

    public static boolean hasAccessibilitySetting(Context mContext) {
        int accessibilityEnabled;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            // 0仅代表没有任何的服务被授权，只要有任何一个被授权，就返回1；出异常才是功能不存在
            LogUtils.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
//            if (accessibilityEnabled == 1) return true;
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * AccessibilitySetting是否是开启的；
     *
     * @param mContext 上下文
     * @return TRUE，已开启；
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {

        String service = mContext.getPackageName() + "/" + "自己注册的服务的名字";//AutoInstallCheckService.class.getCanonicalName();

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (hasAccessibilitySetting(mContext)) {
            //LogUtils.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    LogUtils.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        LogUtils.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            LogUtils.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    public static void performAction(AccessibilityService service, int action, String id, String text, boolean clickable) {
        AccessibilityNodeInfo nodeInfo = findViewById(service, id);
        if (nodeInfo != null) {
            nodeInfo.performAction(action);
        } else {
            nodeInfo = findViewByText(service, text, clickable);
            if (nodeInfo != null) {
                nodeInfo.performAction(action);
            }
        }
    }

    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return View
     */
    public static AccessibilityNodeInfo findViewByText(AccessibilityService service, String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable() == clickable)) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findViewById(AccessibilityService service, String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        try {

            boolean hasFindAccessibilityNodeInfosByViewIdMethod = false;
            Class<?> threadClazz = Class.forName("android.view.accessibility.AccessibilityNodeInfo");
            Method[] MethodList = threadClazz.getDeclaredMethods();
            for (Method tmp : MethodList) {
                if (tmp.getName().equals("findAccessibilityNodeInfosByViewId")) {
                    hasFindAccessibilityNodeInfosByViewIdMethod = true;
                    break;
                }
            }
            // 在某些手机上比如三星GT-N7000 会抛出java.lang.NoSuchMethodError，貌似是虚拟机抛出的，无法捕获；
            if (hasFindAccessibilityNodeInfosByViewIdMethod) {
                List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
                if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
                    for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                        if (nodeInfo != null) {
                            return nodeInfo;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString());
            return null;
        }
        return null;
    }

    /**
     * 适用于如下情况：同一个页面有多个id相同的控件，单纯通过id去找可能不准确，单纯通过text去找也可能不准确，此时就通过id和控件包含的文本的一部分共同确定
     *
     * @param service
     * @param id
     * @param text
     * @return
     */
    public static AccessibilityNodeInfo findViewContainText(AccessibilityService service, String id, String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        try {

            boolean hasFindAccessibilityNodeInfosByViewIdMethod = false;
            Class<?> threadClazz = Class.forName("android.view.accessibility.AccessibilityNodeInfo");
            Method[] MethodList = threadClazz.getDeclaredMethods();
            for (Method tmp : MethodList) {
                if (tmp.getName().equals("findAccessibilityNodeInfosByViewId")) {
                    hasFindAccessibilityNodeInfosByViewIdMethod = true;
                    break;
                }
            }
            // 在某些手机上比如三星GT-N7000 会抛出java.lang.NoSuchMethodError，貌似是虚拟机抛出的，无法捕获；
            if (hasFindAccessibilityNodeInfosByViewIdMethod) {
                List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
                if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
                    String nodeText;
                    for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                        if (nodeInfo != null) {
                            nodeText = nodeInfo.getText().toString();
                            if (!TextUtils.isEmpty(nodeText) && nodeText.contains(text)) {
                                return nodeInfo;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString());
            return null;
        }
        return null;
    }

    public static AccessibilityNodeInfo findViewContainTextArray(AccessibilityService service, String id, String[] textArray) {
        AccessibilityNodeInfo nodeInfo;
        for (String text : textArray) {
            nodeInfo = findViewContainText(service, id, text);
            if (nodeInfo != null) {
                return nodeInfo;
            }
        }
        return null;
    }


    /**
     * 获取节点的文本内容
     *
     * @param service
     * @param id
     * @return
     */
    public static String getNodeText(AccessibilityService service, String id) {
        AccessibilityNodeInfo nodeInfo = findViewById(service, id);
        if (nodeInfo != null) {
            return nodeInfo.getText().toString();
        }
        return "";
    }

    /**
     * 自动安装
     */
    public static void autoInstallFunction(AccessibilityService service) {
        //某些4.x系统的手机，安装和卸载都是com.android.packageinstaller，并且“确定”按钮的resource-id相同
        //所以要避开卸载；
        AccessibilityNodeInfo titleNode = AccessibilityServiceHelper.findViewById(service, "android:id/title");
        if (null != titleNode && titleNode.getClassName().equals("android.widget.TextView")) {
            String title = titleNode.getText().toString();
            if ("卸载应用".equals(title))
                return;
        }
        AccessibilityNodeInfo scrollViewNode = AccessibilityServiceHelper.findViewById(service, "com.android.packageinstaller:id/scrollview");
        if (null != scrollViewNode)
            scrollViewNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

        // ------start:华为某些型号手机，会针对非华为应用市场已上架的产品显示“官方推荐”按钮，要避免点击此按钮；
        AccessibilityNodeInfo checkBox = AccessibilityServiceHelper.findViewById(service, "com.android.packageinstaller:id/decide_to_continue");
        if (null != checkBox && checkBox.getClassName().equals("android.widget.CheckBox")) {
            checkBox.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        AccessibilityNodeInfo button1 = AccessibilityServiceHelper.findViewById(service, "android:id/button1");
        //防止这个button和卸载应用页面的确定按钮id相同；
        AccessibilityNodeInfo button1Parent = null;
        if (null != button1 && null != button1.getParent()) {
            button1Parent = button1.getParent().getParent();
        }
        if (null != button1 && button1Parent != null && "android.widget.LinearLayout".equals(button1Parent.getClassName().toString())) {
            button1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        // ------end

        AccessibilityNodeInfo nodeInfo1 = AccessibilityServiceHelper.findViewById(service, "com.android.packageinstaller:id/ok_button");//安装按钮；
        if (null != nodeInfo1) {
            nodeInfo1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            String text;
            //LogUtils.e(TAG, "当前系统语言环境-----------------------------" + Locale.getDefault().getLanguage());
            if (Locale.getDefault().getLanguage().equals(Locale.CHINESE.getLanguage())) {
                text = "安装";
            } else {
                text = service.getText(R.string.text_install).toString();
            }
            AccessibilityNodeInfo nodeInfo = AccessibilityServiceHelper.findViewByText(service, text, true);
            if (nodeInfo != null) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

        closeInstallFinishPage(service);

    }

    /**
     * 自动卸载应用
     */
    public static void autoUninstallFunction(AccessibilityService service) {
        // android:id/button1  com.google.android.packageinstaller
        AccessibilityNodeInfo button1 = AccessibilityServiceHelper.findViewById(service, "android:id/button1");
        if (button1 != null) {
            button1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
        }
    }

    /**
     * 开启授权后，自动finish掉设置页面功能；
     */
    public static void closeSystemSettingPage(AccessibilityService service, long serviceCreatedTime) {
        long intervalTime = System.currentTimeMillis() - serviceCreatedTime;//加时间间隔，避免用户自己手动打开设置页面时，被我们误判；
        if (intervalTime >= 1000) return;
        String label = service.getResources().getString(R.string.auto_install_service_label);
        AccessibilityNodeInfo titleParent = AccessibilityServiceHelper.findViewById(service, "com.android.settings:id/action_bar");
        if (null == titleParent) {//目前已知华为荣耀V8对原生系统的该页面做了修改
            AccessibilityNodeInfo title = AccessibilityServiceHelper.findViewById(service, "android:id/action_bar_title");
            if (title != null && !TextUtils.isEmpty(title.getText()) && label.equalsIgnoreCase(title.getText().toString())) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        } else {
            if (titleParent.getChildCount() > 1) {
                AccessibilityNodeInfo title = titleParent.getChild(1);
                if (!TextUtils.isEmpty(title.getText()) && label.equalsIgnoreCase(title.getText().toString())) {
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
            }
        }
    }

    /**
     * 关闭 系统的安装完成页面
     *
     * @param service
     */
    public static void closeInstallFinishPage(AccessibilityService service) {
        AccessibilityNodeInfo nodeInfo2 = AccessibilityServiceHelper.findViewById(service, "com.android.packageinstaller:id/done_button");//完成按钮；
        if (null != nodeInfo2) {
            nodeInfo2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else if (null != AccessibilityServiceHelper.findViewById(service, "com.google.android.packageinstaller:id/done_button")) {
            AccessibilityServiceHelper.findViewById(service, "com.google.android.packageinstaller:id/done_button").performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            String text;
            if (Locale.getDefault().getLanguage().equals(Locale.CHINESE.getLanguage())) {
                text = "完成";
            } else {
                text = service.getText(R.string.text_done).toString();
            }
            AccessibilityNodeInfo nodeInfo = AccessibilityServiceHelper.findViewByText(service, text, true);
            if (nodeInfo != null) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
}
