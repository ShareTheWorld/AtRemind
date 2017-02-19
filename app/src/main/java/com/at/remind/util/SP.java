package com.at.remind.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 16-12-19.
 */

public class SP {
    public static final String AT_REMIND="at_remind";
    public static final String AT_REMIND_VOLUME ="volume";
    public static final String TIPS_SCREEN_ON_OFF="tips_screen_on_off";
    public static final String NOT_CURRENT_APP_NOTIFICATION="not_current_app_notification";
    public static final String AT_REMIND_SOUND_URI="at_remind_sound_uri";
    public static void putString(Context context, String key,String value){
        SharedPreferences sp=context.getSharedPreferences(AT_REMIND,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static String getString(Context context,String key){
        SharedPreferences sp=context.getSharedPreferences(AT_REMIND,Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }

    public static void putBoolean(Context context,String key,boolean value){
        SharedPreferences sp=context.getSharedPreferences(AT_REMIND,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public static boolean getBoolean(Context context,String key){
        SharedPreferences sp=context.getSharedPreferences(AT_REMIND,Context.MODE_PRIVATE);
        return sp.getBoolean(key,true);
    }
}
