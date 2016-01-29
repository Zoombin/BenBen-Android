package com.xunao.benben.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

/**
 * Created by ltf on 2016/1/29.
 */

public class HintUtil {
    public enum Platform{
        samsung,lg,htc,mi,sony
    };

    private static final String TAG = "BadgeUtil";

    public static void setBadgeCount(Context context, int count,Platform platform) {
        Intent badgeIntent = null;
        if(platform.equals(Platform.samsung)){
            Log.e(TAG, "samsung....");
            badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            badgeIntent.putExtra("badge_count", count);
            badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
            badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
        }

        if(platform.equals(Platform.mi)){
            Log.e(TAG, "xiaoMiShortCut....");
            badgeIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            badgeIntent.putExtra("android.intent.extra.update_application_component_name",getLauncherClassName(context));
            badgeIntent.putExtra("android.intent.extra.update_application_message_text", count);
            context.sendBroadcast(badgeIntent);
        }

        if(platform.equals(Platform.sony)){
            Log.e(TAG, "sony....");
            badgeIntent = new Intent();
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
            badgeIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", getLauncherClassName(context));
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", count);
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
        }
        if(platform.equals(Platform.htc)){
            Log.e(TAG, "htc....");
            badgeIntent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            badgeIntent.putExtra("packagename", getLauncherClassName(context));
            badgeIntent.putExtra("count", count);
        }

        if(platform.equals(Platform.lg)){
            Log.e(TAG, "lg....");
            badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
            badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
            badgeIntent.putExtra("badge_count", count);
        }
        context.sendBroadcast(badgeIntent);
    }


    public static void resetBadgeCount(Context context,Platform platform) {
        setBadgeCount(context, 0,platform);
    }


    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        Log.e("getLauncherClassName ", "name = "+ info.activityInfo.name);
        return info.activityInfo.name;
    }
}
