package com.javaprogramming.quiz.model;

public class CategoryQuestionCount {
    private int categoryID_CQC;
    private int totalQuestions_CQC;

    public CategoryQuestionCount(int categoryID_CQC, int totalQuestions_CQC) {
        this.categoryID_CQC = categoryID_CQC;
        this.totalQuestions_CQC = totalQuestions_CQC;
    }

    public int getCategoryID_CQC() {
        return categoryID_CQC;
    }

    public void setCategoryID_CQC(int categoryID_CQC) {
        this.categoryID_CQC = categoryID_CQC;
    }

    public int getTotalQuestions_CQC() {
        return totalQuestions_CQC;
    }

    public void setTotalQuestions_CQC(int totalQuestions_CQC) {
        this.totalQuestions_CQC = totalQuestions_CQC;
    }
}
