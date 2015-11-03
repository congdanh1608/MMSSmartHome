package com.thesis.mmtt2011.homemms.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Xpie on 11/3/2015.
 */
public class PreferencesHelper {
    //private static final String USER_PREFERENCES = "userPreferences";
    private static final String FIRST_RUN_PREFRENCES = "isFirstRun";
    /*private static final String PREFERENCE_FIRST_NAME = USER_PREFERENCES + ".nameDisplay";
    private static final String PREFERENCE_LAST_INITIAL = USER_PREFERENCES + ".lastInitial";
    private static final String PREFERENCE_AVATAR = USER_PREFERENCES + ".avatar";*/

    private PreferencesHelper() {
        //no instance
    }

    /**
     * Writes a boolean firstRun to preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @param isFirstRun The isFirstRun to write.
     */
    public static void writeToPreferences(Context context, Boolean isFirstRun) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(FIRST_RUN_PREFRENCES, isFirstRun);
        /*editor.putString(PREFERENCE_FIRST_NAME, player.getFirstName());
        editor.putString(PREFERENCE_LAST_INITIAL, player.getLastInitial());
        editor.putString(PREFERENCE_AVATAR, player.getAvatar().name());*/
        editor.apply();
    }

    public static boolean getIsFirstRun(Context context) {
        SharedPreferences preferences = getIsFirstRunSharedPreferences(context);
        return preferences.getBoolean(FIRST_RUN_PREFRENCES, true);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getIsFirstRunSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getIsFirstRunSharedPreferences(Context context) {
        return context.getSharedPreferences(FIRST_RUN_PREFRENCES, Context.MODE_PRIVATE);
    }
}
