package com.javaprogramming.quiz.interfaces.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.javaprogramming.quiz.model.QuizHistory;
import com.javaprogramming.quiz.model.QuizHistoryWithCategory;

import java.util.List;

@Dao
public interface QuizHistoryDao {

    // Insert a new quiz participation record
    @Insert
    long insert(QuizHistory quizHistory);

    // Get all quiz participation's for the single user (though only one will exist)
    @Query("SELECT qh.*, c.categoryTitle_C FROM QuizHistory qh " +
            "JOIN Category c ON qh.categoryID_QH = c.categoryID_C " +
            "ORDER BY qh.quizHistoryID_QH DESC")
    LiveData<List<QuizHistoryWithCategory>> getAllQuizHistoryWithCategory();



    // Get quiz participation by its ID
    @Query("SELECT * FROM QuizHistory WHERE quizHistoryID_QH = :id")
    QuizHistory getQuizParticipationById(int id);

    // Get the latest quiz participation (assuming the user only participates in one set of quizzes at a time)
    @Query("SELECT * FROM QuizHistory ORDER BY quizHistoryID_QH DESC")
    QuizHistory getLatestQuizParticipation();

}
