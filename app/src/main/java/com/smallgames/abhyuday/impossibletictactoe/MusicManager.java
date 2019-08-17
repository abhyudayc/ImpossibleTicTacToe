package com.smallgames.abhyuday.impossibletictactoe;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

class MusicManager {

    private static Context context;
    private static MediaPlayer mediaPlayer;
    private static SoundPool soundPool;
    private static float soundPoolVolume = 1.0f;

    private static int streamCoinsCredited, streamWinGame, streamLoseGame, streamButtonPush;

    static void setContext(Context applicationContext) {
        context = applicationContext;
    }

    static void initializeSoundPool() {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        streamCoinsCredited = soundPool.load(context, R.raw.coins_credited_music_mix1, 1);
        streamWinGame = soundPool.load(context, R.raw.win_game_music_mix1, 1);
        streamLoseGame = soundPool.load(context, R.raw.lose_game_music_mix1, 1);
        streamButtonPush = soundPool.load(context, R.raw.button_push, 1);
    }

    static void playCoinsCreditedMusic(boolean musicPlayState) {
        if(musicPlayState) {
            try {
                soundPool.play(streamCoinsCredited, soundPoolVolume, soundPoolVolume, 0, 0, 1.0f);
            } catch (Exception e) {}
        }
    }

    static void playWinGameMusic(boolean musicPlayState) {
        if(musicPlayState) {
            try {
                soundPool.play(streamWinGame, soundPoolVolume, soundPoolVolume, 0, 0, 1.0f);
            } catch (Exception e) {}
        }
    }

    static void playLoseGameMusic(boolean musicPlayState) {
        if(musicPlayState) {
            try {
                soundPool.play(streamLoseGame, soundPoolVolume, soundPoolVolume, 0, 0, 1.0f);
            } catch (Exception e) {}
        }
    }

    static void playButtonPushMusic(boolean musicPlayState) {
        if(musicPlayState) {
            try {
                soundPool.play(streamButtonPush, soundPoolVolume, soundPoolVolume, 0, 0, 1.0f);
            } catch (Exception e) {}
        }
    }

    static void startBGM(boolean musicPlayState) {
        if (musicPlayState) {
            try {
                if (mediaPlayer.isPlaying()) ;
            } catch (Exception e) {
                stopBGM(musicPlayState);
                mediaPlayer = MediaPlayer.create(context, R.raw.bg_music_mix1);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }

    }

    static void stopBGM(boolean musicPlayState) {
        if (musicPlayState) {
            if (mediaPlayer != null)
                mediaPlayer.release();
        }
    }
}
