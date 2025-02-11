package com.javaprogramming.quiz.interfaces.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.javaprogramming.quiz.model.CategoryQuestionCount;
import com.javaprogramming.quiz.model.Quiz;
import com.javaprogramming.quiz.utilities.Helper;

import java.util.List;

@Dao
public interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Quiz quiz);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Quiz> quizzes);

    @Query("SELECT * FROM "+Helper.QUIZ_QUESTIONS_TABLE_NAME+" WHERE categoryID_Q = :categoryId")
    List<Quiz> getQuizzesByCategory(int categoryId);


    // Query to get the count of questions for each category
    @Query("SELECT categoryID_Q as categoryID_CQC, COUNT(*) AS totalQuestions_CQC FROM "+ Helper.QUIZ_QUESTIONS_TABLE_NAME+" GROUP BY categoryID_Q")
    List<CategoryQuestionCount> getCategoryQuestionCounts();

    @Query("SELECT * FROM "+Helper.QUIZ_QUESTIONS_TABLE_NAME+" WHERE categoryID_Q = :categoryId ORDER BY questionID_Q LIMIT 10 OFFSET :offset")
    List<Quiz> getQuizzesWithOffset(int categoryId, int offset);

}


