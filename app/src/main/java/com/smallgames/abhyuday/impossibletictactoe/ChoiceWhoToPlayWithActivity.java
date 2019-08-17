package com.smallgames.abhyuday.impossibletictactoe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class ChoiceWhoToPlayWithActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private SharedPrefManager sharedPrefManager;

    private int currentCoins;
    private long TIME_CONSTANT_TO_INCREASE_COINS;

    private TextView tvCoinsLeft;
    private Button buttonComputer, buttonHuman;
    private ImageView buttonSpeaker;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;

    private AdManager adManager;

    private boolean goingToNewActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_who_to_play_with);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        takeCareOfSpeakerButton();

        currentCoins = sharedPrefManager.getCurrentCoins();

        if (currentCoins < getResources().getInteger(R.integer.against_computer_charge)) {
            adManager = new AdManager(this);
            adManager.getRewardedVideoAd().setRewardedVideoAdListener(this);
        }

        tvCoinsLeft = (TextView) findViewById(R.id.tvCoinsLeft);
        tvCoinsLeft.setText("" + currentCoins);

        TIME_CONSTANT_TO_INCREASE_COINS = getResources().getInteger(R.integer.time_const_to_credit_coins);

        if (sharedPrefManager.getLastLoginTime() != -1 && sharedPrefManager.getCurrentCoins() < 50 && System.currentTimeMillis() - sharedPrefManager.getLastLoginTime() > TIME_CONSTANT_TO_INCREASE_COINS) {
            int coinsToAdd = (int)((System.currentTimeMillis() - sharedPrefManager.getLastLoginTime())/TIME_CONSTANT_TO_INCREASE_COINS)*getResources().getInteger(R.integer.half_an_hour_reward);
            changeCoinsLeft(coinsToAdd);
            sharedPrefManager.setLastLoginTime(System.currentTimeMillis());
        }

        buttonComputer = (Button) findViewById(R.id.buttonComputer);
        buttonHuman = (Button) findViewById(R.id.buttonHuman);

        buttonComputer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play click sound
                MusicManager.playButtonPushMusic(sharedPrefManager.getMusicPlayState());

                if (currentCoins < getResources().getInteger(R.integer.against_computer_charge)) {
                    // inform user he does not have enough coins to play the game
                    // ask user to watch ad to gain coins
                    watchAdPopupWindow(v);
                } else {
                    changeCoinsLeft(0 - getResources().getInteger(R.integer.against_computer_charge));
                    // start activity for against computer
                    Intent intent = new Intent(getApplicationContext(), SelectSignActivity.class);
                    intent.putExtra(getString(R.string.playing_against), 'C');
                    goingToNewActivity = true;
                    startActivity(intent);
                }
            }
        });

        buttonHuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play click sound
                MusicManager.playButtonPushMusic(sharedPrefManager.getMusicPlayState());

                if (currentCoins < getResources().getInteger(R.integer.against_human_charge)) {
                    // inform user he does not have enough coins to play the game
                    // ask user to watch ad to gain coins
                    watchAdPopupWindow(v);
                } else {
                    changeCoinsLeft(0 - getResources().getInteger(R.integer.against_human_charge));
                    // start activity for against human
                    Intent intent = new Intent(getApplicationContext(), SelectSignActivity.class);
                    intent.putExtra(getString(R.string.playing_against), 'H');
                    goingToNewActivity = true;
                    startActivity(intent);
                }
            }
        });
    }

    private void takeCareOfSpeakerButton() {
        buttonSpeaker = (ImageView) findViewById(R.id.speaker_button);
        if (sharedPrefManager.getMusicPlayState())
            buttonSpeaker.setImageResource(R.drawable.speaker_on);
        else
            buttonSpeaker.setImageResource(R.drawable.speaker_off);
        buttonSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPrefManager.getMusicPlayState()) {
                    MusicManager.stopBGM(true);
                    sharedPrefManager.setMusicPlayState(false);
                    buttonSpeaker.setImageResource(R.drawable.speaker_off);
                } else {
                    sharedPrefManager.setMusicPlayState(true);
                    MusicManager.startBGM(true);
                    buttonSpeaker.setImageResource(R.drawable.speaker_on);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.startBGM(sharedPrefManager.getMusicPlayState());
        goingToNewActivity = false;
        currentCoins = sharedPrefManager.getCurrentCoins();
        tvCoinsLeft.setText("" + currentCoins);
    }

    private void changeCoinsLeft(int changeBy) {
        currentCoins += changeBy;
        if (currentCoins > 50)
            currentCoins = 50;
        sharedPrefManager.setCurrentCoins(currentCoins);
//        tvCoinsLeft.setText("" + currentCoins);
    }

    private void watchAdPopupWindow(View v) {
        layoutInflater = (LayoutInflater) ChoiceWhoToPlayWithActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_layout,null);
        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        Button watchAdButton = (Button) customView.findViewById(R.id.watchAdButton);
        watchAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicManager.stopBGM(sharedPrefManager.getMusicPlayState());
                //play ad
                if(!adManager.showRewardedVideoAd()) {
                    Toast.makeText(ChoiceWhoToPlayWithActivity.this, "Unable to connect Ad Server. Please try again", Toast.LENGTH_SHORT).show();
                    MusicManager.startBGM(sharedPrefManager.getMusicPlayState());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!goingToNewActivity)
            MusicManager.stopBGM(sharedPrefManager.getMusicPlayState());
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        adManager.loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        changeCoinsLeft(getResources().getInteger(R.integer.ad_reward));
        tvCoinsLeft.setText("" + currentCoins);
        popupWindow.dismiss();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
