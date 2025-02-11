package com.javaprogramming.quiz.utilities;

import android.media.MediaPlayer;

public class AudioHelper {

    // Method to mute the MediaPlayer
    public static void muteMusic(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.setVolume(0f, 0f);  // Mute both left and right channels
        }
    }

    // Method to unmute the MediaPlayer
    public static void unmuteMusic(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {  // ✅ Ensure MediaPlayer is playing
            mediaPlayer.setVolume(1f, 1f);  // ✅ Use "1f" instead of "1"
        }
    }

    // Method to play music from the beginning
    public static void playMusic(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    // Method to release MediaPlayer resources
    public static void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
