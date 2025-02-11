package com.javaprogramming.quiz.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.javaprogramming.quiz.model.CategoryQuestionCount;
import com.javaprogramming.quiz.model.Quiz;
import com.javaprogramming.quiz.repository.QuizRepository;

import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private final QuizRepository quizRepository;

    public QuizViewModel(Application application) {
        super(application);
        quizRepository = new QuizRepository(application);
    }


    // Method to get question counts for categories
    public List<CategoryQuestionCount> getCategoryQuestionCounts() {
        return quizRepository.getCategoryQuestionCounts();
    }


    // Save a list of quizzes to the database
    public void saveQuizzes(List<Quiz> quizzes) {
        quizRepository.saveQuizzes(quizzes);
    }

    // Method to retrieve quizzes for a specific set number
    public List<Quiz> getQuizzesForSet(int categoryID, int offset) {
        // Calculate the offset based on the set number

        // Use the repository to fetch quizzes from the database with the calculated offset
        return quizRepository.getQuizzesByOffset(categoryID, offset);
    }
}

