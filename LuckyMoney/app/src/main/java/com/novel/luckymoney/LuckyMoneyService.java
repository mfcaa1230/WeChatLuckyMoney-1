package com.novel.luckymoney;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

/**
 * Auther：sangshulong
 * Time：2015-12-21 15:14:56
 */

public class LuckyMoneyService extends AccessibilityService {

    private Context mContext;
    private static final String TAG = "LuckyMoney";

    /** 微信包名*/
    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    /** 消息关键字*/
    private static final String LUCKYMONEY_KEY = "[微信红包]";
    /** 领取红包关键字*/
    private static final String GET_LUCKYMONEY_KEY = "领取红包";
    /** 拆红包关键字*/
    private static final String OPEN_LUCKEYMONEY_KEY = "拆红包";
    /** 微信聊天界面*/
    private static final String WECHAT_CHATUI = "com.tencent.mm.ui.LauncherUI";
    /** 微信抢红包界面*/
    private static final String WECHAT_LUCKYMONEYUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    /** 微信红包信息界面*/
    private static final String WECHAT_LUCKYMONEY_DETAILUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    /** 微信红包金额id*/
    private static final String WECHAT_LOCUEYMONEY_ID = "com.tencent.mm:id/b02";

    public LuckyMoneyService(){

    }

    public LuckyMoneyService(Context context){
        this.mContext = context;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        final int eventType = event.getEventType();
        /** 通知栏事件改变*/
        if(eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED){
            List<CharSequence> eventText = event.getText();
            if(!eventText.isEmpty()) {
                for (CharSequence texts : eventText) {
                    String text = String.valueOf(texts);
                    if (text.contains(LUCKYMONEY_KEY)){
                        openNotify(event);
                        break;
                    }
                }
            }
        }else if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            openLuckyMoney(event);
        }
    }

    private void sendNotifyEvent(){
       AccessibilityManager acManager = (AccessibilityManager)getSystemService(ACCESSIBILITY_SERVICE);
        if(!acManager.isEnabled())
            return;

        AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
        event.setPackageName(WECHAT_PACKAGENAME);
        event.setClassName(Notification.class.getName());
        CharSequence charSequence = LUCKYMONEY_KEY;
        event.getText().add(charSequence);
        acManager.sendAccessibilityEvent(event);
    }

    private void openLuckyMoney(AccessibilityEvent event) {
        if(WECHAT_CHATUI.equals(event.getClassName())) {
            //聊天界面，点击红包
            selectLuckyMoney();
        }else if(WECHAT_LUCKYMONEYUI.equals(event.getClassName())){
            //抢红包界面，抢红包
            robLuckeyMoney();
        }else if(WECHAT_LUCKYMONEY_DETAILUI.equals(event.getClassName())){
            //计算红包金额
            countLuckyMoney();
        }


    }

    private void selectLuckyMoney() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo == null)
            return;

        List<AccessibilityNodeInfo> nodeInfosText = nodeInfo.findAccessibilityNodeInfosByText(GET_LUCKYMONEY_KEY);
        if(nodeInfosText.isEmpty()){
            nodeInfosText = nodeInfo.findAccessibilityNodeInfosByText(LUCKYMONEY_KEY);
            for(AccessibilityNodeInfo text : nodeInfosText){
                text.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }else{
            for(int i = nodeInfosText.size() - 1 ; i >= 0;--i){
                AccessibilityNodeInfo parent = nodeInfosText.get(i).getParent();
                if(parent == null)
                    return;
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }
    }

    private void robLuckeyMoney() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo == null)
            return;

        List<AccessibilityNodeInfo> listText = nodeInfo.findAccessibilityNodeInfosByText(OPEN_LUCKEYMONEY_KEY);
        if (listText.isEmpty())
            return;

        for (AccessibilityNodeInfo list : listText){
            list.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            break;
        }
    }

    private void countLuckyMoney() {

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo == null)
            return;

        List<AccessibilityNodeInfo> listID = nodeInfo.findAccessibilityNodeInfosByViewId(WECHAT_LOCUEYMONEY_ID);
        if(listID.isEmpty())
            return;

        for (AccessibilityNodeInfo id : listID)
        {
            Toast.makeText(mContext,id.getText().toString(),Toast.LENGTH_SHORT).show();
        }

    }

    //打开通知栏消息
    private void openNotify(AccessibilityEvent event) {
        if(event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification))
            return;

        //打开微信通知栏消息
        Notification notification = (Notification)event.getParcelableData();
        PendingIntent intent = notification.contentIntent;

        try {
            intent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onInterrupt() {
        Toast.makeText(mContext, mContext.getString(R.string.break_luckymoney_service),Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(mContext,mContext.getString(R.string.connect_luckymoney_service),Toast.LENGTH_LONG).show();
    }
}
