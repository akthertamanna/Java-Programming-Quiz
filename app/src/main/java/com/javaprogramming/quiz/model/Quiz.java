package com.javaprogramming.quiz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.javaprogramming.quiz.utilities.Helper;

import java.io.Serializable;

@Entity(tableName = Helper.QUIZ_QUESTIONS_TABLE_NAME,
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "categoryID_C",
                childColumns = "categoryID_Q"),
        indices = @Index(value = "categoryID_Q"))
public class Quiz implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int questionID_Q;
    private int categoryID_Q;
    private String question_Q;
    private String optionA_Q;
    private String optionB_Q;
    private String optionC_Q;
    private String optionD_Q;
    private byte answer_Q;
    private String hint_Q;

    public Quiz(int questionID_Q, int categoryID_Q, String question_Q, String optionA_Q, String optionB_Q, String optionC_Q, String optionD_Q, byte answer_Q, String hint_Q) {
        this.questionID_Q = questionID_Q;
        this.categoryID_Q = categoryID_Q;
        this.question_Q = question_Q;
        this.optionA_Q = optionA_Q;
        this.optionB_Q = optionB_Q;
        this.optionC_Q = optionC_Q;
        this.optionD_Q = optionD_Q;
        this.answer_Q = answer_Q;
        this.hint_Q = hint_Q;
    }

    public int getQuestionID_Q() {
        return questionID_Q;
    }

    public void setQuestionID_Q(int questionID_Q) {
        this.questionID_Q = questionID_Q;
    }

    public int getCategoryID_Q() {
        return categoryID_Q;
    }

    public void setCategoryID_Q(int categoryID_Q) {
        this.categoryID_Q = categoryID_Q;
    }

    public String getQuestion_Q() {
        return question_Q;
    }

    public void setQuestion_Q(String question_Q) {
        this.question_Q = question_Q;
    }

    public String getOptionA_Q() {
        return optionA_Q;
    }

    public void setOptionA_Q(String optionA_Q) {
        this.optionA_Q = optionA_Q;
    }

    public String getOptionB_Q() {
        return optionB_Q;
    }

    public void setOptionB_Q(String optionB_Q) {
        this.optionB_Q = optionB_Q;
    }

    public String getOptionC_Q() {
        return optionC_Q;
    }

    public void setOptionC_Q(String optionC_Q) {
        this.optionC_Q = optionC_Q;
    }

    public String getOptionD_Q() {
        return optionD_Q;
    }

    public void setOptionD_Q(String optionD_Q) {
        this.optionD_Q = optionD_Q;
    }

    public byte getAnswer_Q() {
        return answer_Q;
    }

    public void setAnswer_Q(byte answer_Q) {
        this.answer_Q = answer_Q;
    }

    public String getHint_Q() {
        return hint_Q;
    }

    public void setHint_Q(String hint_Q) {
        this.hint_Q = hint_Q;
    }
}
