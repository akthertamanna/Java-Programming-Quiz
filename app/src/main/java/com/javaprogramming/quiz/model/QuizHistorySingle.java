package com.javaprogramming.quiz.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.javaprogramming.quiz.utilities.Helper;

@Entity(
        tableName = Helper.QUIZ_HISTORY_QUESTION_TABLE_NAME,
        foreignKeys = {
                @ForeignKey(
                        entity = QuizHistory.class,
                        parentColumns = "quizHistoryID_QH",
                        childColumns = "quizHistoryID_QHQ",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Quiz.class,
                        parentColumns = "questionID_Q",
                        childColumns = "questionID_QHQ",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = "quizHistoryID_QHQ"),
                @Index(value = "questionID_QHQ")
        }
)
public class QuizHistorySingle {
    @PrimaryKey(autoGenerate = true)
    private int quizHistoryQuestionID_QHQ;
    private int quizHistoryID_QHQ;
    private int questionID_QHQ;
    private byte selectedAnswer_QHQ;

    public QuizHistorySingle() {
    }

    @Ignore
    public QuizHistorySingle(int quizHistoryID_QHQ, int questionID_QHQ, byte selectedAnswer_QHQ) {
        this.quizHistoryID_QHQ = quizHistoryID_QHQ;
        this.questionID_QHQ = questionID_QHQ;
        this.selectedAnswer_QHQ = selectedAnswer_QHQ;
    }

    public int getQuizHistoryQuestionID_QHQ() {
        return quizHistoryQuestionID_QHQ;
    }

    public void setQuizHistoryQuestionID_QHQ(int quizHistoryQuestionID_QHQ) {
        this.quizHistoryQuestionID_QHQ = quizHistoryQuestionID_QHQ;
    }

    public int getQuizHistoryID_QHQ() {
        return quizHistoryID_QHQ;
    }

    public void setQuizHistoryID_QHQ(int quizHistoryID_QHQ) {
        this.quizHistoryID_QHQ = quizHistoryID_QHQ;
    }

    public int getQuestionID_QHQ() {
        return questionID_QHQ;
    }

    public void setQuestionID_QHQ(int questionID_QHQ) {
        this.questionID_QHQ = questionID_QHQ;
    }

    public byte getSelectedAnswer_QHQ() {
        return selectedAnswer_QHQ;
    }

    public void setSelectedAnswer_QHQ(byte selectedAnswer_QHQ) {
        this.selectedAnswer_QHQ = selectedAnswer_QHQ;
    }
}
