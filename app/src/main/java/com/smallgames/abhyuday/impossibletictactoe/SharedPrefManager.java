package com.smallgames.abhyuday.impossibletictactoe;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPrefManager {

    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    protected SharedPrefManager (Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name), context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    protected boolean isFirstTimeLogin() {
        return sharedPreferences.getBoolean(context.getString(R.string.if_first_time), true);
    }

    protected void setFirstTimeFlag(boolean flag) {
        editor.putBoolean(context.getString(R.string.if_first_time), flag);
        editor.commit();
    }

    protected long getLastLoginTime() {
        return sharedPreferences.getLong(context.getString(R.string.last_login), -1);
    }

    protected void setLastLoginTime(long time) {
        editor.putLong(context.getString(R.string.last_login), time);
        editor.commit();
    }

    protected int getCurrentCoins() {
        return sharedPreferences.getInt(context.getString(R.string.current_coins), 0);
    }

    protected void setCurrentCoins(int currentCoins) {
        editor.putInt(context.getString(R.string.current_coins), currentCoins);
        editor.commit();
    }

    protected boolean getMusicPlayState() {
        return sharedPreferences.getBoolean(context.getString(R.string.music_play), true);
    }

    protected void setMusicPlayState(boolean bgmPlayState) {
        editor.putBoolean(context.getString(R.string.music_play), bgmPlayState);
        editor.commit();
    }

    protected boolean getShowAppRaterAnyMore() {
        return sharedPreferences.getBoolean(context.getString(R.string.show_app_rater_any_more), true);
    }

    protected void setShowAppRaterAnyMore(boolean choice) {
        editor.putBoolean(context.getString(R.string.show_app_rater_any_more), choice);
        editor.commit();
    }

    protected int getCountTillAppRaterShow() {
        return sharedPreferences.getInt(context.getString(R.string.count_till_app_rater_show), context.getResources().getInteger(R.integer.number_of_launches_between_app_rater_dialogs));
    }

    protected void setCountTillAppRaterShow(int count) {
        editor.putInt(context.getString(R.string.count_till_app_rater_show), count);
        editor.commit();
    }
}
