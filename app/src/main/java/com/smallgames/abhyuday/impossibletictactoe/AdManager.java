package com.smallgames.abhyuday.impossibletictactoe;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

class AdManager {

    private Context context;
    private RewardedVideoAd rewardedVideoAd;

    AdManager(Context context) {
        this.context = context;
        MobileAds.initialize(context, context.getResources().getString(R.string.admob_app_id));
//        MobileAds.initialize(context, "ca-app-pub-3940256099942544~3347511713");

        initializeRewardedVideoAd();
        loadRewardedVideoAd();
    }

    private void initializeRewardedVideoAd() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
    }

    void loadRewardedVideoAd() {
        if(!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(context.getResources().getString(R.string.admob_ad_id), new AdRequest.Builder().build());
//        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
        }
    }

    boolean showRewardedVideoAd() {
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
            return true;
        }
        else
            return false;
    }

    void pauseRewardedVideoAd() {
        rewardedVideoAd.pause(context);
    }

    void resumeRewardedVideoAd() {
        rewardedVideoAd.resume(context);
    }

    RewardedVideoAd getRewardedVideoAd () {
        return rewardedVideoAd;
    }
}
