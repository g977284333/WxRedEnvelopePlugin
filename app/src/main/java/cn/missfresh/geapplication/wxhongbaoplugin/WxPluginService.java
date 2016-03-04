package cn.missfresh.geapplication.wxhongbaoplugin;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import cn.missfresh.geapplication.utils.Logger;

/**
 * Created by gchen on 16/3/3.
 */
public class WxPluginService extends AccessibilityService {
    private final String TAG = getClass().getSimpleName();

    private Status mCurStatus = Status.OUT_WE_CHAT;

    private volatile List<PendingIntent> mUnTreatedHongBaoList = new ArrayList<>();    // 待处理红包信息

    private List<AccessibilityNodeInfo> mCanOpenNode;       // 在聊天界面上有领取红包样式的红包列表
    private List<AccessibilityNodeInfo> mTrashOpenNode;     // 被删除或已经领过的红包列表

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.e(TAG, "微信红包插件服务已经开启");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handlerNotificationEvent(event);
                Logger.e(TAG, "通知栏消息...");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                handlerWindowStateChanged(event);
                Logger.e(TAG, "窗口变化...");
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                handlerWindowContentChanged(event);
                Logger.e(TAG, "窗口内容变化...");
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void handlerNotificationEvent(AccessibilityEvent event) {
        if (event != null) {
            List<CharSequence> contents = event.getText();
            if (contents != null && !contents.isEmpty()) {
                for (CharSequence charSequence : contents) {
                    String content = charSequence.toString();
                    if (!content.contains("[微信红包]")) {
                        continue;
                    }

                    // 找到微信红包
                    Parcelable parcelableData = event.getParcelableData();
                    if (parcelableData != null && parcelableData instanceof Notification) {
                        Notification notification = (Notification) parcelableData;
                        PendingIntent contentIntent = notification.contentIntent;
                        Logger.e(TAG, contentIntent.getCreatorPackage().toString());
                        if (contentIntent.getCreatorPackage().toString().equals("com.tencent.mm")) {

                            // 在home页面
                            if (mCurStatus != Status.ON_CHAT_ROOM || mCurStatus != Status.ON_HONG_BAO_RECEIVED
                                    && mUnTreatedHongBaoList.size() == 0) {
                                openNotificationIntent(contentIntent);
                            } else {
                                // 将接下来的红包通知放到未处理的集合中
                                mUnTreatedHongBaoList.add(0, contentIntent);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 开启微信中的聊天界面
     *
     * @param contentIntent
     */
    private void openNotificationIntent(PendingIntent contentIntent) {
        if (contentIntent != null) {
            try {
                contentIntent.send();
                mCurStatus = Status.ON_CHAT_ROOM;
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlerWindowStateChanged(AccessibilityEvent event) {
        if (event != null && event.getSource() != null) {
            String className = event.getClassName().toString();

            // 微信首界面
            if ("com.tencent.mm.ui.LauncherUI".equals(className)) {
                mCurStatus = Status.ON_CHAT_ROOM;
                if (mCanOpenNode == null) {
                    mCanOpenNode = event.getSource().findAccessibilityNodeInfosByText("领取红包");
                    mTrashOpenNode = new ArrayList<>();
                }
                openHongBao();
                // 红包接收界面
            } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(className)) {
                mCurStatus = Status.ON_HONG_BAO_RECEIVED;
                unPackHongBao(event.getSource());
                // 红包详情界面
            } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className)) {
                mCurStatus = Status.ON_HONG_BAO_DETAIL;
                back();
            }
        }
    }

    /**
     * 在红包界面拆开红包
     *
     * @param source
     */
    private void unPackHongBao(AccessibilityNodeInfo source) {
        if (source == null) {
            back();
            return;
        }

        // 打开红包，如果红包已抢完，遍历节点，如果匹配"红包详情”、“手慢了”、”过期“，则返回继续打开其他红包
        List<AccessibilityNodeInfo> packedList = new ArrayList<>();
        packedList.addAll(source.findAccessibilityNodeInfosByText("红包详情"));
        packedList.addAll(source.findAccessibilityNodeInfosByText("手慢了"));
        packedList.addAll(source.findAccessibilityNodeInfosByText("过期"));

        if (!packedList.isEmpty()) {
            back();
            return;
        }

        // 由于微信把红包界面的文字“拆红包”替换成了图片“開”，所以现在无法打开红包
        List<AccessibilityNodeInfo> unPackList = source.findAccessibilityNodeInfosByText("拆红包");
        if (unPackList.isEmpty()) {
            back();
            return;
        } else {
            AccessibilityNodeInfo accessibilityNodeInfo = unPackList.get(0);
            accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 在聊天界面中打开红包页面
     */
    private void openHongBao() {
        if (mCanOpenNode != null && mCanOpenNode.size() == 0) {
            backToHome();
            disposeHomeBaoList();
            mCanOpenNode = null;
            mTrashOpenNode = null;
        }

        if (mCanOpenNode == null) {
            backToHome();
            return;
        }

        AccessibilityNodeInfo canOpenNode = mCanOpenNode.remove(0);
        mTrashOpenNode.add(canOpenNode);
        if (canOpenNode.getParent() != null && canOpenNode.getParent().isClickable()) {
            canOpenNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 处理未处理的红包列表Notification列表上的
     */
    private void disposeHomeBaoList() {
        if (mUnTreatedHongBaoList.size() != 0) {
            PendingIntent pendingIntent = mUnTreatedHongBaoList.remove(0);
            openNotificationIntent(pendingIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void backToHome() {
        mCurStatus = Status.OUT_WE_CHAT;
        this.performGlobalAction(GLOBAL_ACTION_HOME);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void back() {
        this.performGlobalAction(GLOBAL_ACTION_BACK);
    }

    private void handlerWindowContentChanged(AccessibilityEvent event) {
        if (event != null && event.getSource() != null) {
            if (Status.ON_CHAT_ROOM == mCurStatus) {
                List<AccessibilityNodeInfo> newList = event.getSource().findAccessibilityNodeInfosByText("领取红包");
                if (newList != null && mCanOpenNode != null && !mCanOpenNode.isEmpty()) {
                    for (AccessibilityNodeInfo nodeInfo : newList) {
                        boolean isCanOpenNode = false;
                        for (AccessibilityNodeInfo coNode : mCanOpenNode) {
                            if (nodeInfo.equals(coNode)) {
                                isCanOpenNode = true;
                                break;
                            }
                        }

                        if (isCanOpenNode) {
                            continue;
                        }

                        boolean isInTrash = false;
                        for (AccessibilityNodeInfo coNode : mTrashOpenNode) {
                            if (nodeInfo.equals(coNode)) {
                                isInTrash = true;
                                break;
                            }
                        }

                        if (isInTrash) {
                            continue;
                        }

                        mCanOpenNode.add(nodeInfo);
                    }
                }

            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.e(TAG, "微信红包插件服务已经关闭");
    }

    public enum Status {
        OUT_WE_CHAT, ON_WE_CHAT_HOME, ON_CHAT_ROOM, ON_HONG_BAO_RECEIVED, ON_HONG_BAO_DETAIL
    }
}
