package com.javaprogramming.quiz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.javaprogramming.quiz.utilities.Helper;

import java.io.Serializable;

@Entity(
        tableName = Helper.QUIZ_HISTORY_TABLE_NAME,
        foreignKeys = {
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "categoryID_C",
                        childColumns = "categoryID_QH",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userID_U",
                        childColumns = "userID_QH",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = "categoryID_QH"),
                @Index(value = "userID_QH")
        }
)

public class QuizHistory implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int quizHistoryID_QH;

    private int categoryID_QH;

    private int userID_QH;

    private int score_QH;

    private int correctAnswers_QH; // Number of correct answers

    private int wrongAnswers_QH;   // Number of wrong answers

    private int skippedQuestions_QH; // Number of skipped questions

    private int totalQuestions_QH; // Total number of questions in the quiz

    private long timeTaken_QH; // Time taken for the quiz in milliseconds

    // Constructor

    public QuizHistory() {}

    @Ignore
    public QuizHistory(int categoryID_QH, int userID_QH, int score_QH, int correctAnswers_QH, int wrongAnswers_QH, int skippedQuestions_QH, int totalQuestions_QH, long timeTaken_QH) {
        this.categoryID_QH = categoryID_QH;
        this.userID_QH = userID_QH;
        this.score_QH = score_QH;
        this.correctAnswers_QH = correctAnswers_QH;
        this.wrongAnswers_QH = wrongAnswers_QH;
        this.skippedQuestions_QH = skippedQuestions_QH;
        this.totalQuestions_QH = totalQuestions_QH;
        this.timeTaken_QH = timeTaken_QH;
    }

    public int getQuizHistoryID_QH() {
        return quizHistoryID_QH;
    }

    public void setQuizHistoryID_QH(int quizHistoryID_QH) {
        this.quizHistoryID_QH = quizHistoryID_QH;
    }

    public int getCategoryID_QH() {
        return categoryID_QH;
    }

    public void setCategoryID_QH(int categoryID_QH) {
        this.categoryID_QH = categoryID_QH;
    }

    public int getUserID_QH() {
        return userID_QH;
    }

    public void setUserID_QH(int userID_QH) {
        this.userID_QH = userID_QH;
    }

    public int getScore_QH() {
        return score_QH;
    }

    public void setScore_QH(int score_QH) {
        this.score_QH = score_QH;
    }

    public int getCorrectAnswers_QH() {
        return correctAnswers_QH;
    }

    public void setCorrectAnswers_QH(int correctAnswers_QH) {
        this.correctAnswers_QH = correctAnswers_QH;
    }

    public int getWrongAnswers_QH() {
        return wrongAnswers_QH;
    }

    public void setWrongAnswers_QH(int wrongAnswers_QH) {
        this.wrongAnswers_QH = wrongAnswers_QH;
    }

    public int getSkippedQuestions_QH() {
        return skippedQuestions_QH;
    }

    public void setSkippedQuestions_QH(int skippedQuestions_QH) {
        this.skippedQuestions_QH = skippedQuestions_QH;
    }

    public int getTotalQuestions_QH() {
        return totalQuestions_QH;
    }

    public void setTotalQuestions_QH(int totalQuestions_QH) {
        this.totalQuestions_QH = totalQuestions_QH;
    }

    public long getTimeTaken_QH() {
        return timeTaken_QH;
    }

    public void setTimeTaken_QH(long timeTaken_QH) {
        this.timeTaken_QH = timeTaken_QH;
    }
}
