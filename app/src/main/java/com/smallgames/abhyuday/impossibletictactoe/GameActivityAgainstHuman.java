package com.smallgames.abhyuday.impossibletictactoe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class GameActivityAgainstHuman extends AppCompatActivity implements RewardedVideoAdListener {

    private SharedPrefManager sharedPrefManager;

    private TextView tvCoinsLeft, tvTurn;
    private Button[] boardButton;
    private Button buttonPlayAgain;
    private ImageView buttonSpeaker;

    private char player1Sign, player2Sign, whoPlaysNext;
    private GameAgainstHuman gameAgainstHuman;

    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;

    private long lastBackPressed = 0;
    private int currentCoins;

    private AdManager adManager;

    private boolean goingToNewActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_against_human);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        takeCareOfSpeakerButton();

        sharedPrefManager.setCountTillAppRaterShow(sharedPrefManager.getCountTillAppRaterShow() - 1);

        currentCoins = sharedPrefManager.getCurrentCoins();

        if (currentCoins < getResources().getInteger(R.integer.against_computer_charge)) {
            adManager = new AdManager(this);
            adManager.getRewardedVideoAd().setRewardedVideoAdListener(this);
        }

        player1Sign = getIntent().getCharExtra(getString(R.string.player1_sign), 'n');
        player2Sign = getIntent().getCharExtra(getString(R.string.player2_sign), 'n');

        tvCoinsLeft = (TextView) findViewById(R.id.tvCoinsLeft);
        tvCoinsLeft.setText("" + sharedPrefManager.getCurrentCoins());

        tvTurn = (TextView) findViewById(R.id.tvTurn);
        buttonPlayAgain = (Button) findViewById(R.id.buttonPlayAgain);
        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play click sound
                MusicManager.playButtonPushMusic(sharedPrefManager.getMusicPlayState());

                if (sharedPrefManager.getCurrentCoins() < getResources().getInteger(R.integer.against_human_charge)) {
                    // inform user he does not have enough coins to play the game
                    // ask user to watch ad to gain coins
                    watchAdPopupWindow(v);
                } else {
                    sharedPrefManager.setCurrentCoins(sharedPrefManager.getCurrentCoins() - getResources().getInteger(R.integer.against_human_charge));
//                    tvCoinsLeft.setText("" + sharedPrefManager.getCurrentCoins());
                    // start activity for against computer
                    Intent intent = new Intent(getApplicationContext(), SelectSignActivity.class);
                    intent.putExtra(getString(R.string.playing_against), 'H');
                    goingToNewActivity = true;
                    startActivity(intent);
                    finish();
                }
            }
        });

        boardButton = new Button[10];
        boardButtonInit();

        gameAgainstHuman = new GameAgainstHuman();
        if (player1Sign == 'X')
            gameAgainstHuman.setSigns(GameAgainstHuman.sign.x, GameAgainstHuman.sign.o);
        else if (player1Sign == 'O')
            gameAgainstHuman.setSigns(GameAgainstHuman.sign.o, GameAgainstHuman.sign.x);

        whoPlaysNext = '1';
        setOnClickListenersForBoardButtons();
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
    private void takeCareOfAppRater() {
        if(sharedPrefManager.getShowAppRaterAnyMore() && sharedPrefManager.getCountTillAppRaterShow()<=0) {
            sharedPrefManager.setCountTillAppRaterShow(getResources().getInteger(R.integer.number_of_launches_between_app_rater_dialogs));
            AppRater.showAppRaterDialog(GameActivityAgainstHuman.this, sharedPrefManager);
        }
    }

    private void boardButtonInit() {
        boardButton[0] = null;
        boardButton[1] = (Button)findViewById(R.id.boardButton1);
        boardButton[2] = (Button)findViewById(R.id.boardButton2);
        boardButton[3] = (Button)findViewById(R.id.boardButton3);
        boardButton[4] = (Button)findViewById(R.id.boardButton4);
        boardButton[5] = (Button)findViewById(R.id.boardButton5);
        boardButton[6] = (Button)findViewById(R.id.boardButton6);
        boardButton[7] = (Button)findViewById(R.id.boardButton7);
        boardButton[8] = (Button)findViewById(R.id.boardButton8);
        boardButton[9] = (Button)findViewById(R.id.boardButton9);
    }

    private void setOnClickListenersForBoardButtons() {
        int i = 1;
        while (i <= 9) {
            final int j = i;
            boardButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // play click sound
                    MusicManager.playButtonPushMusic(sharedPrefManager.getMusicPlayState());

                    if (whoPlaysNext == '1') {
                        // change background
                        if (player1Sign == 'X')
                            v.setBackgroundResource(R.drawable.x_game);
                        else if (player1Sign == 'O')
                            v.setBackgroundResource(R.drawable.o_game);
                        // make button disabled
                        v.setEnabled(false);
                        // set button index as user choice
                        gameAgainstHuman.markPlayer1Choice(j);
                        whoPlaysNext = '2';
                        // set textview text to Player2's turn
                        tvTurn.setText("Player2's turn");
                    } else if (whoPlaysNext == '2') {
                        // change background
                        if (player2Sign == 'X')
                            v.setBackgroundResource(R.drawable.x_game);
                        else if (player2Sign == 'O')
                            v.setBackgroundResource(R.drawable.o_game);
                        // make button disabled
                        v.setEnabled(false);
                        // set button index as user choice
                        gameAgainstHuman.markPlayer2Choice(j);
                        whoPlaysNext = '1';
                        // set textview text to Player1's turn
                        tvTurn.setText("Player1's turn");
                    }

                    if (gameAgainstHuman.isWinningSetFormed())
                        someoneHasWon(gameAgainstHuman.getWinner());
                    else if (gameAgainstHuman.getEmptySpaces() <= 0) {
                        // set textview text to Game over
                        tvTurn.setText("That's a tie!");
                        showPlayAgainButton();
                    }
                }
            });
            i++;
        }
    }

    private void someoneHasWon(String winner) {
        // disable all the buttons
        disableAllButtons();

        // play winning music
        MusicManager.playWinGameMusic(sharedPrefManager.getMusicPlayState());

        // thread to control coloring and blinking
        if (winner.equals("player1")) {
            if (player1Sign == 'O') {
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(0, 1))].setBackgroundResource(R.drawable.o_game_win);
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(1, 2))].setBackgroundResource(R.drawable.o_game_win);
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(2))].setBackgroundResource(R.drawable.o_game_win);
            } else if (player1Sign == 'X') {
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(0, 1))].setBackgroundResource(R.drawable.x_game_win);
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(1, 2))].setBackgroundResource(R.drawable.x_game_win);
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(2))].setBackgroundResource(R.drawable.x_game_win);
            }
            // set textview text to Player1 wins
            tvTurn.setText("Player1 wins!");
        } else if (winner.equals("player2")) {
            if (player2Sign == 'O') {
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(0, 1))].setBackgroundResource(R.drawable.o_game_win);
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(1, 2))].setBackgroundResource(R.drawable.o_game_win);
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(2))].setBackgroundResource(R.drawable.o_game_win);
            } else if (player2Sign == 'X') {
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(0, 1))].setBackgroundResource(R.drawable.x_game_win);
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(1, 2))].setBackgroundResource(R.drawable.x_game_win);
                boardButton[Integer.parseInt(gameAgainstHuman.getWinningSet().substring(2))].setBackgroundResource(R.drawable.x_game_win);
            }
            // set textview text to Player2 wins
            tvTurn.setText("Player2 wins!");
        }

        // ask to rate the app
        takeCareOfAppRater();

        // show play again button
        showPlayAgainButton();
    }

    private void disableAllButtons() {
        for (int i = 1; i < boardButton.length; i++) {
            boardButton[i].setEnabled(false);
        }
    }

    private void showPlayAgainButton() {
        buttonPlayAgain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastBackPressed <= getResources().getInteger(R.integer.max_delay_between_two_back_presses)) {
            super.onBackPressed();
            return;
        }
        lastBackPressed = System.currentTimeMillis();
        Toast.makeText(this, "Press BACK once again to exit", Toast.LENGTH_SHORT).show();
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

    private void watchAdPopupWindow(View v) {
        layoutInflater = (LayoutInflater) GameActivityAgainstHuman.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                //play ad
                if(!adManager.showRewardedVideoAd())
                    Toast.makeText(GameActivityAgainstHuman.this, "Unable to connect Ad Server. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeCoinsLeft(int changeBy) {
        currentCoins += changeBy;
        if (currentCoins > 50)
            currentCoins = 50;
        sharedPrefManager.setCurrentCoins(currentCoins);
        tvCoinsLeft.setText("" + currentCoins);
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
