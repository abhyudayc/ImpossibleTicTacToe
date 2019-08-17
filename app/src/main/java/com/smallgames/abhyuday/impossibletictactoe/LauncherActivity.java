package com.smallgames.abhyuday.impossibletictactoe;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {

    private ImageView launcherImageView;
    private Button buttonPlay;
    private ImageView buttonSpeaker;

    private SharedPrefManager sharedPrefManager;

    private boolean goingToNewActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        MusicManager.setContext(getApplicationContext());
        MusicManager.initializeSoundPool();
        MusicManager.startBGM(sharedPrefManager.getMusicPlayState());

        sharedPrefManager.setCountTillAppRaterShow(sharedPrefManager.getCountTillAppRaterShow() - 1);

        launcherImageView = (ImageView) findViewById(R.id.launcher_image_view);
        launcherImageView.setImageResource(R.drawable.hello_there);

        takeCareOfSpeakerButton();

        buttonPlay = (Button) findViewById(R.id.launcher_button);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // play click sound
                MusicManager.playButtonPushMusic(sharedPrefManager.getMusicPlayState());

                if (sharedPrefManager.getLastLoginTime() == -1)
                    sharedPrefManager.setLastLoginTime(System.currentTimeMillis());

                if (sharedPrefManager.isFirstTimeLogin()) {

                    sharedPrefManager.setFirstTimeFlag(false);
                    sharedPrefManager.setCurrentCoins(50);

                    LinearLayout launcherLayout = (LinearLayout) findViewById(R.id.launcher_layout);
                    launcherLayout.removeAllViews();

                    ImageView coinsCreditedImageView = new ImageView(getApplicationContext());
                    coinsCreditedImageView.setImageResource(R.drawable.coins_credited);
                    coinsCreditedImageView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    launcherLayout.setGravity(Gravity.CENTER);
                    launcherLayout.addView(coinsCreditedImageView);

                    TextView tvClickToContinue = new TextView(getApplicationContext());
                    tvClickToContinue.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    tvClickToContinue.setText("Tap to Continue");
                    tvClickToContinue.setTextColor(Color.argb(125, 255, 255, 255));
                    tvClickToContinue.setTextSize(20);

                    launcherLayout.addView(tvClickToContinue);

                    MusicManager.playCoinsCreditedMusic(sharedPrefManager.getMusicPlayState());

                    launcherLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ChoiceWhoToPlayWithActivity.class);
                            goingToNewActivity = true;
                            startActivity(intent);
                            finish();
                        }
                    });

                } else {
                    Intent intent = new Intent(getApplicationContext(), ChoiceWhoToPlayWithActivity.class);
                    goingToNewActivity = true;
                    startActivity(intent);
                    finish();
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
    protected void onPause() {
        super.onPause();
        if(!goingToNewActivity)
            MusicManager.stopBGM(sharedPrefManager.getMusicPlayState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.startBGM(sharedPrefManager.getMusicPlayState());
    }
}
