package com.javaprogramming.quiz.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.javaprogramming.quiz.database.AppDatabase;
import com.javaprogramming.quiz.database.QuizDataHandler;
import com.javaprogramming.quiz.interfaces.dao.QuizDao;
import com.javaprogramming.quiz.model.CategoryQuestionCount;
import com.javaprogramming.quiz.model.Quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class QuizRepository {

    private final QuizDao quizDao;
    private final ExecutorService executorService; // Background thread pool

    public QuizRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        quizDao = db.quizDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Method to save a list of quizzes to the database
    public void saveQuizzes(List<Quiz> quizzes) {
        // This operation needs to be run on a background thread
        executorService.execute(()-> quizDao.insertAll(quizzes));
    }

    // Method to get the question counts for each category
    public List<CategoryQuestionCount> getCategoryQuestionCounts() {
        Future<List<CategoryQuestionCount>> future = executorService.submit(new Callable<List<CategoryQuestionCount>>() {
            @Override
            public List<CategoryQuestionCount> call() throws Exception {
                return quizDao.getCategoryQuestionCounts();
            }
        });

        try {
            return future.get(); // This will block and wait for the result
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // In case of an error, return null or handle appropriately
    }



    public List<Quiz> getQuizzesByOffset(int categoryID, int offset) {
        Future<List<Quiz>> future = executorService.submit(new Callable<List<Quiz>>() {
            @Override
            public List<Quiz> call() {
                return quizDao.getQuizzesWithOffset(categoryID ,offset);
            }
        });

        try {
            return future.get(); // This will block and wait for the result
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // In case of an error, return null or handle appropriately
    }

}
