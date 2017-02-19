package com.at.remind.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.at.remind.R;
import com.at.remind.db.AtRemindDb;
import com.at.remind.util.SP;
import com.at.remind.util.SoundPlayer;
import com.at.remind.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-12-16.
 * 写了一个AtRemindService类，继承系统提供的NotificationListenerService
 */

public class AtRemindService extends NotificationListenerService {
    //存放用户设置的所有关键字
    public static List<String> mKey;
    /**
     * 事件回调函数，当通知栏有变化就会回调onNotificationPosted方法，并且传入一个StatusBarNotification对象
     * 接着通过sbn这个对象获取到Notification，最后从Notification对象中去获取到消息，把获取到的消息传给doNotification去处理
     * @param sbn
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //通过状态栏对象得到状态栏上面最新的Notification
        Notification mNotification = sbn.getNotification();
        //判断这个对象向是否为null，如果不为null就继续往下走
        if (mNotification != null) {
            //进入到Notification的信息处理函数
            doNotification(mNotification.toString() + mNotification.extras.toString());
        }
    }

    /**
     * 处理Notification中消息的函数
     * 1、再得到屏幕当前的状态（是否锁屏）；2、再得到用户当前设置的模式；3、对比Notification中的消息和用户设置的关键字；
     * 最后通过上面三个信息判断是否提醒用户
     * @param content
     */
    public void doNotification(String content) {
        //获取电源管理器服务
        PowerManager powerManager = (PowerManager) this
                .getSystemService(getApplicationContext().POWER_SERVICE);
        //判断屏幕是否打开
        boolean ifOpen = powerManager.isScreenOn();
        //获取用户设置的提醒模式
        boolean tipsScreenOnOff= SP.getBoolean(getApplicationContext(),SP.TIPS_SCREEN_ON_OFF);
        if(!ifOpen||!tipsScreenOnOff) {//如果是黑屏的或者是在任意情况都提醒的模式，就进入到下一步
            L.i("atremind", content);
            //将消息中的英文字母全部转化为大写的
            content = content.toUpperCase();
            //从数据库中获取到用户设置的关键字
            mKey = AtRemindDb.getInstance(getApplicationContext()).selectAllKeyWord();
            //将Notification中的消息和用户设置的每一个关键字进行对比
            for (int i = 0; i < mKey.size(); i++) {
                L.i("atremind", "" + content.contains(mKey.get(i).toUpperCase()));
                //关键字对比
                if (content.contains(mKey.get(i).toUpperCase())) {
                    //对比成功了，播放用户提示的声音
                    SoundPlayer.playSouned(this);
                    L.i("atremind", "play at remind sound");
                    break;
                }
            }
        }
    }
    public String getTopApplication(){

        return null;
    }
    private String getTopApp() {
        Log.i("hongtao.fu",System.currentTimeMillis()+"   1");
        String topPackage="com.at.remind";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据

                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, now);
                L.i("atremind", "Running app number in last 60 seconds : " + stats.size());
                String topActivity = "";

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        UsageStats us=stats.get(i);
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    topActivity = stats.get(j).getPackageName();
                    topPackage=topActivity;
                }
                L.i("atremind", "top running app is : "+topActivity);
            }
        }
        Log.i("hongtao.fu",System.currentTimeMillis()+"   2");
        return topPackage;
    }
}
