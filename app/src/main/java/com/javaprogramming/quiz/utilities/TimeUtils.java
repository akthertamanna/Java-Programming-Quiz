package com.javaprogramming.quiz.utilities;

import android.annotation.SuppressLint;

public class TimeUtils {
    public static long calculatePlayTime(long startTime, long endTime, long totalPausedDuration) {
        // Calculate playtime (in milliseconds)
        return (endTime - startTime) - totalPausedDuration;
    }

    @SuppressLint("DefaultLocale")
    public static String convertMillisecondsToTime(long milliseconds) {
        long hours = (milliseconds / (1000 * 60 * 60));  // Calculate hours
        long minutes = (milliseconds / (1000 * 60)) % 60;  // Calculate minutes
        long seconds = (milliseconds / 1000) % 60;  // Calculate seconds

        if (hours > 0) {
            // If hours are greater than 0, return the format "hours:minutes:seconds"
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            // If no hours, return the format "minutes:seconds"
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

}
