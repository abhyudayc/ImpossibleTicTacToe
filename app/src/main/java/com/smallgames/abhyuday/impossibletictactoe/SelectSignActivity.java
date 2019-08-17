package com.smallgames.abhyuday.impossibletictactoe;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectSignActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;

    private TextView tvChooseSign, tvCoinsLeft;
    private Button playerSignX, playerSignO;
    private ImageView buttonSpeaker;

    private char playingAgainst;
    private long lastBackPressed = 0;

    private boolean goingToNewActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sign);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        takeCareOfSpeakerButton();

        takeCareOfAppRater();

        tvCoinsLeft = (TextView) findViewById(R.id.tvCoinsLeft);
        tvChooseSign = (TextView) findViewById(R.id.tvChooseSign);
        playerSignX = (Button) findViewById(R.id.playerSignX);
        playerSignO = (Button) findViewById(R.id.playerSignO);

        tvCoinsLeft.setText("" + sharedPrefManager.getCurrentCoins());

        playingAgainst = getIntent().getCharExtra(getString(R.string.playing_against), 'n');

        switch (playingAgainst) {
            case 'C':
                tvChooseSign.setText("Choose your sign");
                break;
            case 'H':
                tvChooseSign.setText("Choose Player1 sign");
                break;
        }

        playerSignX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play click sound
                MusicManager.playButtonPushMusic(sharedPrefManager.getMusicPlayState());

                if (playingAgainst == 'C') {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    intent.putExtra(getString(R.string.player1_sign), 'X');
                    intent.putExtra(getString(R.string.player2_sign), 'O');
                    goingToNewActivity = true;
                    startActivity(intent);
                    ActivityCompat.finishAffinity(SelectSignActivity.this);
                } else if (playingAgainst == 'H') {
                    Intent intent = new Intent(getApplicationContext(), GameActivityAgainstHuman.class);
                    intent.putExtra(getString(R.string.player1_sign), 'X');
                    intent.putExtra(getString(R.string.player2_sign), 'O');
                    goingToNewActivity = true;
                    startActivity(intent);
                    ActivityCompat.finishAffinity(SelectSignActivity.this);
                }
            }
        });

        playerSignO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play click sound
                MusicManager.playButtonPushMusic(sharedPrefManager.getMusicPlayState());

                if (playingAgainst == 'C') {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    intent.putExtra(getString(R.string.player1_sign), 'O');
                    intent.putExtra(getString(R.string.player2_sign), 'X');
                    goingToNewActivity = true;
                    startActivity(intent);
                    ActivityCompat.finishAffinity(SelectSignActivity.this);
                } else if (playingAgainst == 'H') {
                    Intent intent = new Intent(getApplicationContext(), GameActivityAgainstHuman.class);
                    intent.putExtra(getString(R.string.player1_sign), 'O');
                    intent.putExtra(getString(R.string.player2_sign), 'X');
                    goingToNewActivity = true;
                    startActivity(intent);
                    ActivityCompat.finishAffinity(SelectSignActivity.this);
                }
            }
        });
    }

    private void takeCareOfAppRater() {
        if(sharedPrefManager.getShowAppRaterAnyMore() && sharedPrefManager.getCountTillAppRaterShow()<=0) {
            sharedPrefManager.setCountTillAppRaterShow(getResources().getInteger(R.integer.number_of_launches_between_app_rater_dialogs));
            AppRater.showAppRaterDialog(SelectSignActivity.this, sharedPrefManager);
        }
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
    public void onBackPressed() {
        if(!isTaskRoot()) {
            switch (playingAgainst) {
                case 'C':
                    sharedPrefManager.setCurrentCoins(sharedPrefManager.getCurrentCoins() + getResources().getInteger(R.integer.against_computer_charge));
                    break;
                case 'H':
                    sharedPrefManager.setCurrentCoins(sharedPrefManager.getCurrentCoins() + getResources().getInteger(R.integer.against_human_charge));
                    break;
            }
            goingToNewActivity = true;
            super.onBackPressed();
        } else {
            goingToNewActivity = false;
            if (System.currentTimeMillis() - lastBackPressed <= getResources().getInteger(R.integer.max_delay_between_two_back_presses)) {
                super.onBackPressed();
                return;
            }
            lastBackPressed = System.currentTimeMillis();
            Toast.makeText(this, "Press BACK once again to exit", Toast.LENGTH_SHORT).show();
        }
//        tvCoinsLeft.setText("" + sharedPrefManager.getCurrentCoins());
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
