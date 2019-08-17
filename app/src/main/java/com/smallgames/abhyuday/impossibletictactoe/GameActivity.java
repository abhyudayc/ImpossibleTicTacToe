package com.smallgames.abhyuday.impossibletictactoe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class GameActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private SharedPrefManager sharedPrefManager;

    private TextView tvCoinsLeft, tvTurn;
    private Button[] boardButton;
    private Button buttonPlayAgain;
    private ImageView buttonSpeaker;

    private char player1Sign;

    private GameAgainstComputer gameAgainstComputer;
    private Thread computerThread;

    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;

    private long lastBackPressed = 0;
    private int currentCoins;

    private AdManager adManager;

    private boolean goingToNewActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        takeCareOfSpeakerButton();

        sharedPrefManager.setCountTillAppRaterShow(sharedPrefManager.getCountTillAppRaterShow() - 1);

        currentCoins = sharedPrefManager.getCurrentCoins();

        if (currentCoins < getResources().getInteger(R.integer.against_computer_charge)) {
            adManager = new AdManager(this);
            adManager.getRewardedVideoAd().setRewardedVideoAdListener(this);
        }

        player1Sign = getIntent().getCharExtra(getString(R.string.player1_sign), 'n');

        tvCoinsLeft = (TextView) findViewById(R.id.tvCoinsLeft);
        tvCoinsLeft.setText("" + sharedPrefManager.getCurrentCoins());

        tvTurn = (TextView) findViewById(R.id.tvTurn);
        buttonPlayAgain = (Button) findViewById(R.id.buttonPlayAgain);
        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play click sound
                MusicManager.playButtonPushMusic(sharedPrefManager.getMusicPlayState());

                if (sharedPrefManager.getCurrentCoins() < getResources().getInteger(R.integer.against_computer_charge)) {
                    // inform user he does not have enough coins to play the game
                    // ask user to watch ad to gain coins
                    watchAdPopupWindow(v);
                } else {
                    sharedPrefManager.setCurrentCoins(sharedPrefManager.getCurrentCoins() - getResources().getInteger(R.integer.against_computer_charge));
//                    tvCoinsLeft.setText("" + sharedPrefManager.getCurrentCoins());
                    // start activity for against computer
                    Intent intent = new Intent(getApplicationContext(), SelectSignActivity.class);
                    intent.putExtra(getString(R.string.playing_against), 'C');
                    goingToNewActivity = true;
                    startActivity(intent);
                    finish();
                }
            }
        });

        boardButton = new Button[10];
        boardButtonInit();

        gameAgainstComputer = new GameAgainstComputer();
        if (player1Sign == 'X')
            gameAgainstComputer.setSigns(GameAgainstComputer.sign.x, GameAgainstComputer.sign.o);
        else if (player1Sign == 'O')
            gameAgainstComputer.setSigns(GameAgainstComputer.sign.o, GameAgainstComputer.sign.x);


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

                    // change background
                    if (player1Sign == 'X')
                        v.setBackgroundResource(R.drawable.x_game);
                    else if (player1Sign == 'O')
                        v.setBackgroundResource(R.drawable.o_game);
                    // make button disabled
                    v.setEnabled(false);
                    // set button index as user choice
                    gameAgainstComputer.markUserChoice(j);

                    if (gameAgainstComputer.isWinningSetFormed())
                        someoneHasWon(gameAgainstComputer.getWinner());
                    else {
                        if (gameAgainstComputer.getEmptySpaces() > 0) {
                            tvTurn.setText("Computer's turn");
                            disableAllButtons();
                            runComputerThread();
                        }  else {
                            // set textview text to Game over
                            tvTurn.setText("That's a tie!");
                            showPlayAgainButton();
                        }
                    }
                }
            });
            i++;
        }
    }

    private void runComputerThread() {
        computerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // mark computer choice
                final int computerChoice = gameAgainstComputer.markComputerChoice();
                // change background
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (player1Sign == 'X')
                            boardButton[computerChoice].setBackgroundResource(R.drawable.o_game);
                        else if (player1Sign == 'O')
                            boardButton[computerChoice].setBackgroundResource(R.drawable.x_game);
                        // make button disabled
                        boardButton[computerChoice].setEnabled(false);
                        if (gameAgainstComputer.isWinningSetFormed())
                            someoneHasWon(gameAgainstComputer.getWinner());
                        else {
                            enableEmptyButtons();
                            tvTurn.setText("Your turn");
                        }
                    }
                });
            }
        });
        computerThread.start();
    }

    private void someoneHasWon(String winner) {
        // disable all the buttons
        disableAllButtons();

        // thread to control coloring and blinking
        if (winner.equals("computer")) {
            if (player1Sign == 'X') {
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(0, 1))].setBackgroundResource(R.drawable.o_game_win);
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(1, 2))].setBackgroundResource(R.drawable.o_game_win);
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(2))].setBackgroundResource(R.drawable.o_game_win);
            } else if (player1Sign == 'O') {
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(0, 1))].setBackgroundResource(R.drawable.x_game_win);
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(1, 2))].setBackgroundResource(R.drawable.x_game_win);
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(2))].setBackgroundResource(R.drawable.x_game_win);
            }
            // set textview text to You Lose
            tvTurn.setText("You lose!");
            // play lose game music
            MusicManager.playLoseGameMusic(sharedPrefManager.getMusicPlayState());
            // show play again button
            showPlayAgainButton();
        } else if (winner.equals("user")) {
            if (player1Sign == 'O') {
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(0, 1))].setBackgroundResource(R.drawable.o_game_win);
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(1, 2))].setBackgroundResource(R.drawable.o_game_win);
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(2))].setBackgroundResource(R.drawable.o_game_win);
            } else if (player1Sign == 'X') {
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(0, 1))].setBackgroundResource(R.drawable.x_game_win);
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(1, 2))].setBackgroundResource(R.drawable.x_game_win);
                boardButton[Integer.parseInt(gameAgainstComputer.getWinningSet().substring(2))].setBackgroundResource(R.drawable.x_game_win);
            }
            // set textview text to You Win
            tvTurn.setText("You win!");
            // play winning music
            MusicManager.playWinGameMusic(sharedPrefManager.getMusicPlayState());
            handle50CoinsThing();
        }
    }

    private void showPlayAgainButton() {
        buttonPlayAgain.setVisibility(View.VISIBLE);
    }

    private void disableAllButtons() {
        for (int i = 1; i < boardButton.length; i++) {
            boardButton[i].setEnabled(false);
        }
    }

    private void enableEmptyButtons() {
        String filledCells = gameAgainstComputer.getFilledCells().trim();
        for (int i = 1; i < boardButton.length; i++) {
            if (!filledCells.contains(""+i))
                boardButton[i].setEnabled(true);
        }
    }

    private void handle50CoinsThing() {
        sharedPrefManager.setCurrentCoins(50);

        final Thread userWinThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {e.printStackTrace();}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout launcherLayout = (LinearLayout) findViewById(R.id.gameAgainstComputerLayout);
                        launcherLayout.removeAllViews();

                        ImageView coinsCreditedImageView = new ImageView(getApplicationContext());
                        coinsCreditedImageView.setImageResource(R.drawable.coins_credited2);
                        coinsCreditedImageView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        launcherLayout.setGravity(Gravity.CENTER);
                        launcherLayout.addView(coinsCreditedImageView);

                        TextView tvClickToContinue = new TextView(getApplicationContext());
                        tvClickToContinue.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        tvClickToContinue.setText("Tap to return to home screen");
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
                    }
                });
            }
        });

        userWinThread.start();
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
        layoutInflater = (LayoutInflater) GameActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    Toast.makeText(GameActivity.this, "Unable to connect Ad Server. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeCoinsLeft(int changeBy) {
        int currentCoins = sharedPrefManager.getCurrentCoins();
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
