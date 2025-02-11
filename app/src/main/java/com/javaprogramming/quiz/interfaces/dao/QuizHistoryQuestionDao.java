package com.javaprogramming.quiz.interfaces.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.javaprogramming.quiz.model.QuizHistorySingle;
import com.javaprogramming.quiz.model.QuizHistoryWithQuestion;
import com.javaprogramming.quiz.utilities.Helper;

import java.util.List;

@Dao
public interface QuizHistoryQuestionDao {

    // Insert a new question-answer record
    @Insert
    void insertQuizHistoryQuestions(List<QuizHistorySingle> quizHistorySingles);

    // Get all questions for a specific quiz history ID
    @Query("SELECT * FROM "+ Helper.QUIZ_HISTORY_QUESTION_TABLE_NAME +" WHERE questionID_QHQ = :quizHistoryID")
    LiveData<List<QuizHistorySingle>> getSingleQuizQuestion(int quizHistoryID);

    // Get the selected answer for a specific question in a quiz history
    @Query("SELECT selectedAnswer_QHQ FROM "+ Helper.QUIZ_HISTORY_QUESTION_TABLE_NAME +" WHERE questionID_QHQ = :quizHistoryID AND questionID_QHQ = :questionID")
    Byte getSelectedAnswer(int quizHistoryID, int questionID);


    @Query("SELECT * FROM "+Helper.QUIZ_HISTORY_QUESTION_TABLE_NAME+" qhs " +
            "INNER JOIN "+Helper.QUIZ_QUESTIONS_TABLE_NAME+" q ON qhs.questionID_QHQ = q.questionID_Q " +
            "WHERE qhs.quizHistoryID_QHQ = :quizHistoryID")
    LiveData<List<QuizHistoryWithQuestion>> getQuizHistoryWithQuestions(int quizHistoryID);


}
