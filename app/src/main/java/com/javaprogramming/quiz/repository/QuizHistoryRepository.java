package com.javaprogramming.quiz.repository;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.javaprogramming.quiz.database.AppDatabase;
import com.javaprogramming.quiz.interfaces.dao.QuizHistoryDao;
import com.javaprogramming.quiz.interfaces.dao.QuizHistoryQuestionDao;
import com.javaprogramming.quiz.model.QuizHistory;
import com.javaprogramming.quiz.model.QuizHistorySingle;
import com.javaprogramming.quiz.model.QuizHistoryWithCategory;
import com.javaprogramming.quiz.model.QuizHistoryWithQuestion;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class QuizHistoryRepository {
    private final QuizHistoryDao quizHistoryDao;
    private final QuizHistoryQuestionDao quizHistoryQuestionDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public QuizHistoryRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        quizHistoryDao = db.quizHistoryDao();
        quizHistoryQuestionDao = db.quizHistoryQuestionDao();
    }

    // Insert QuizHistory
    public long insertQuizHistory(QuizHistory quizHistory) {
        FutureTask<Long> futureTask = new FutureTask<>(() -> quizHistoryDao.insert(quizHistory));
        executorService.execute(futureTask);

        try {
            return futureTask.get(); // Waits for the result and returns quizHistoryID
        } catch (Exception ignored) {
            Log.e("QuizHistoryRepository", "Error inserting QuizHistory", ignored);
            return -1; // Return -1 if an error occurs
        }
    }


    // Retrieve All QuizHistory
    public LiveData<List<QuizHistoryWithCategory>> getAllQuizHistory() {
        return quizHistoryDao.getAllQuizHistoryWithCategory();
    }

    // Retrieve Quiz History By ID
    public QuizHistory getQuizParticipationById(int id) {
        return quizHistoryDao.getQuizParticipationById(id);
    }

    // Insert QuizHistoryQuestion (User Answer)
    public void insertQuizHistoryQuestion(List<QuizHistorySingle> quizHistorySingle) {
        executorService.execute(() -> quizHistoryQuestionDao.insertQuizHistoryQuestions(quizHistorySingle));
    }

    // Get User's Answers for a Quiz
    public LiveData<List<QuizHistorySingle>> getSingleQuizQuestion(int quizHistoryID) {
        return quizHistoryQuestionDao.getSingleQuizQuestion(quizHistoryID);
    }


    public LiveData<List<QuizHistoryWithQuestion>> getQuizHistoryWithQuestions(int quizHistoryID) {
        return quizHistoryQuestionDao.getQuizHistoryWithQuestions(quizHistoryID);
    }




    // Get User's Answer for a Specific Question
    public Byte getSelectedAnswer(int quizHistoryID, int questionID) {
        return quizHistoryQuestionDao.getSelectedAnswer(quizHistoryID, questionID);
    }
}
