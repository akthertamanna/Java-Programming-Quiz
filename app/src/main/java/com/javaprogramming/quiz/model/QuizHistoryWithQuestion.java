package com.javaprogramming.quiz.model;

import androidx.room.Embedded;

public class QuizHistoryWithQuestion {
    @Embedded
    public QuizHistorySingle quizHistorySingle; // Represents quiz history details

    @Embedded
    public Quiz quiz; // Represents the question details
}

