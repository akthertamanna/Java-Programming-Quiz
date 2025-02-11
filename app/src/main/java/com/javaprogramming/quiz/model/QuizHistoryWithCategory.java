package com.javaprogramming.quiz.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import java.io.Serializable;

public class QuizHistoryWithCategory implements Serializable {

    @Embedded
    public QuizHistory quizHistory; // Represents the QuizHistory fields

    @ColumnInfo(name = "categoryTitle_C")
    public String categoryTitle_C; // The category name

    // Getter for quizHistory
    public QuizHistory getQuizHistory() {
        return quizHistory;
    }

    // Getter for categoryName
    public String getCategoryTitle() {
        return categoryTitle_C;
    }
}
