package kr.co.core.financial_tech.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    /* string */
    public static final String PREF_MIDX = "midx";
    public static final String PREF_FCM = "fcm";
    public static final String PREF_UNIQ = "uniq";
    public static final String PREF_ID = "id";
    public static final String PREF_PW = "pw";
    public static final String PREF_NICK = "nick";
    public static final String PREF_NAME = "name";
    public static final String PREF_VERSION = "version";


    /* int */
    public static final String PREF_PAGER_HEIGHT = "p_height";

    /* boolean */
    public static final String AUTO_LOGIN = "auto";
    public static final String LOGIN_STATE = "login_state";

    // profile string
    public static void setPrefString(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("p_string", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getPrefString(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("p_string", context.MODE_PRIVATE);
        return pref.getString(key, null);
    }


    // profile boolean
    public static void setPrefBoolean(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences("p_boolean", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static boolean getPrefBoolean(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("p_boolean", context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }


    // profile string
    public static void setPrefInt(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences("p_int", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getPrefInt(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("p_int", context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

}
