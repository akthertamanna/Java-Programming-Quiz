package com.javaprogramming.quiz.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.javaprogramming.quiz.model.QuizHistory;
import com.javaprogramming.quiz.model.QuizHistorySingle;
import com.javaprogramming.quiz.model.QuizHistoryWithCategory;
import com.javaprogramming.quiz.model.QuizHistoryWithQuestion;
import com.javaprogramming.quiz.repository.QuizHistoryRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizHistoryViewModel extends AndroidViewModel {
    private final QuizHistoryRepository repository;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public QuizHistoryViewModel(Application application) {
        super(application);
        repository = new QuizHistoryRepository(application);
    }

    // Insert QuizHistory
    public long insertQuizHistory(QuizHistory quizHistory) {
        return repository.insertQuizHistory(quizHistory);
    }

    // Get All Quiz History
    public LiveData<List<QuizHistoryWithCategory>> getAllQuizHistory() {
        return repository.getAllQuizHistory();
    }

    // Get Quiz History By ID
    public QuizHistory getQuizParticipationById(int id) {
        return repository.getQuizParticipationById(id);
    }

    // Insert Answered Question
    public void insertQuizHistoryQuestion(List<QuizHistorySingle> quizHistorySingle) {
        repository.insertQuizHistoryQuestion(quizHistorySingle);
    }

    // Get User's Answers for a Quiz
    public LiveData<List<QuizHistorySingle>> getSingleQuizQuestion(int quizHistoryID) {
        return repository.getSingleQuizQuestion(quizHistoryID);
    }

    public LiveData<List<QuizHistoryWithQuestion>> getQuizHistoryWithQuestions(int quizHistoryID) {
        return repository.getQuizHistoryWithQuestions(quizHistoryID);
    }


    // Get User's Answer for a Specific Question
    public Byte getSelectedAnswer(int quizHistoryID, int questionID) {
        return repository.getSelectedAnswer(quizHistoryID, questionID);
    }
}
