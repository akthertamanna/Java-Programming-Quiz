package com.javaprogramming.quiz.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.javaprogramming.quiz.utilities.Helper;

import java.io.Serializable;

@Entity(tableName = Helper.CATEGORY_TABLE_NAME)
public class Category implements Serializable {

    @PrimaryKey (autoGenerate = true)
    private int categoryID_C;
    private String categoryTitle_C;
    private int levelsCompleted_C;

    public Category() {

    }

    @Ignore
    public Category(int categoryID_C, String categoryTitle_C, int levelsCompleted_C) {
        this.categoryID_C = categoryID_C;
        this.categoryTitle_C = categoryTitle_C;
        this.levelsCompleted_C = levelsCompleted_C;
    }

    public int getCategoryID_C() {
        return categoryID_C;
    }

    public void setCategoryID_C(int categoryID_C) {
        this.categoryID_C = categoryID_C;
    }

    public String getCategoryTitle_C() {
        return categoryTitle_C;
    }

    public void setCategoryTitle_C(String categoryTitle_C) {
        this.categoryTitle_C = categoryTitle_C;
    }

    public int getLevelsCompleted_C() {
        return levelsCompleted_C;
    }

    public void setLevelsCompleted_C(int levelsCompleted_C) {
        this.levelsCompleted_C = levelsCompleted_C;
    }
}

