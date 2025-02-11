package com.javaprogramming.quiz.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.javaprogramming.quiz.database.SaveData;
import com.javaprogramming.quiz.databinding.CompleteActivityBinding;
import com.javaprogramming.quiz.model.Quiz;
import com.javaprogramming.quiz.model.QuizHistory;
import com.javaprogramming.quiz.model.QuizHistorySingle;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.viewmodel.CategoryViewModel;
import com.javaprogramming.quiz.viewmodel.QuizHistoryViewModel;
import com.javaprogramming.quiz.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{

    private CompleteActivityBinding binding;
    private int wrong, userID = 1, reward;
    private Context context;
    private boolean isAlreadySaved, isCoinSaved;
    private UserViewModel userViewModel;
    private QuizHistoryViewModel quizHistoryViewModel;
    private ArrayList<Quiz> quizList = new ArrayList<>();

    HashMap<Integer, Integer> selectedAnswersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CompleteActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Restore isAlreadySaved from savedInstanceState
        if (savedInstanceState != null) {
            isAlreadySaved = savedInstanceState.getBoolean("isAlreadySaved", false);
            isCoinSaved = savedInstanceState.getBoolean("isCoinSaved", false);
            
            enableOrDisableClaimButton();
        }

        // Initialize the context variable
        context = TestActivity.this;

        // Get the UserDataViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                
                if(isAlreadySaved && isCoinSaved){
                    enableOrDisableClaimButton();
                    
                }
                
                binding.layoutShopFeatures.txtCoin.setText(String.valueOf(user.getCoins_U()));

            }
        });

        //The key argument here must match that used in the other activity
        Bundle data = getIntent().getExtras();
        if (data != null) {

            quizHistoryViewModel = new ViewModelProvider(this).get(QuizHistoryViewModel.class);

            int correct = data.getInt("correct");
            userID = data.getInt("userID");
            wrong = data.getInt("wrong");
            int total = data.getInt("total");
            long timeTaken = data.getLong("timeTaken");

            quizList = (ArrayList<Quiz>) data.getSerializable("quizList");

            selectedAnswersMap = (HashMap<Integer, Integer>) data.getSerializable("selectedAnswer");

            int reward = correct * 10;

            //userViewModel.updateCoins(reward, true);
            Log.d("DEBUG", "Reward: " + reward + " coins updated.");
            calculateWinOrLose(correct, total, timeTaken);
        }


        binding.layerBtnHome.setOnClickListener(this);

        binding.btnRetry.setOnClickListener(this);
        binding.btnCheckAnswer.setOnClickListener(this);

        // Register back-press callback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Call the method to show the exit dialog
                goToHome();
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

        binding.btnDoubleReward.setOnClickListener(this);

    }

    private void enableOrDisableClaimButton(){
        if (isAlreadySaved && !isCoinSaved){
            binding.btnClaim.setEnabled(false);
            binding.btnDoubleReward.setEnabled(false);
        }else{
            binding.btnClaim.setEnabled(true);
            binding.btnDoubleReward.setEnabled(true);
        }
    }
    
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void calculateWinOrLose(int correct, int total, long timeTaken){
        reward = correct * 10;
        binding.txtRewardGet.setText(String.valueOf(reward));

        binding.btnClaim.setText("Claim "+reward);

        binding.txtCorrectAnswer.setText(String.format("%d/%d", correct, total));
        int skippedQuestions;

        if (wrong==0){
            skippedQuestions = total-correct;
        }else if(correct==0){
            skippedQuestions = total-wrong;
        }else{
            skippedQuestions = correct + wrong;
            skippedQuestions = total - skippedQuestions;
        }
        
        binding.txtWrongAnswer.setText(String.format("%d/%d", wrong, total));
        binding.txtSkippedQuestion.setText(String.format("%d/%d", skippedQuestions, total));

        // Get the last clicked category id
        int categoryID = SaveData.getCategoryID(context);
        insertQuizParticipation(new QuizHistory(categoryID, userID, reward, correct, wrong, skippedQuestions,total, timeTaken));
        
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isAlreadySaved", isAlreadySaved);
        outState.putBoolean("isCoinSaved", isCoinSaved);
    }

    public void goToHome(){
        Helper.navigateToActivity(context, HomeActivity.class, null , true);
    }

    private void insertQuizParticipation(QuizHistory quizHistory) {
        if (!isAlreadySaved) {
            isAlreadySaved = true;
            isCoinSaved = true;

            if (SaveData.getIsSetsNew(context)){
                CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
                categoryViewModel.incrementLevelCompleted(quizHistory.getCategoryID_QH());
            }

            // Insert the QuizHistory and get the ID
            long quizHistoryID = quizHistoryViewModel.insertQuizHistory(quizHistory);

//            Log.d("quizID", "quizID: " + quizHistoryID);

            if (quizHistoryID != -1) {
                // Create a list to store QuizHistoryQuestion objects
                List<QuizHistorySingle> quizHistorySingles = new ArrayList<>();

                // Map quizList to QuizHistoryQuestion and set quizHistoryID
                for (Quiz question : quizList) {

                    byte selectedAnswer = selectedAnswersMap.containsKey(question.getQuestionID_Q()) ?
                            Objects.requireNonNull(selectedAnswersMap.get(question.getQuestionID_Q())).byteValue() : 0;

                    QuizHistorySingle answeredQuestion = new QuizHistorySingle(
                            (int) quizHistoryID,
                            question.getQuestionID_Q(),
                            selectedAnswer
                    );
                    quizHistorySingles.add(answeredQuestion); // Add to the list
                }

                // Now insert all the QuizHistoryQuestion objects at once
                quizHistoryViewModel.insertQuizHistoryQuestion(quizHistorySingles);
            }
        }
    }

    @Override
    public void onClick(View v) {
        
        if (v == binding.layerBtnHome){
            goToHome();
        }

        if (v == binding.btnCheckAnswer){
            Helper.navigateToActivity(context, QuizHistoryActivity.class, null , true);
        }

        if (v == binding.btnRetry){
            SaveData.saveIsSetsNew(false ,context);
            Helper.navigateToActivity(context, QuizPlayActivity.class, null , true);
        }
        
        if (v == binding.btnClaim){
            isCoinSaved = false;
            userViewModel.updateCoins(reward, true);
        }   
        
        if (v == binding.btnDoubleReward){
            loadAd();
        }
    }

    private void loadAd() {
        doubleReward();
    }

    private void doubleReward(){
        isCoinSaved = false;
        userViewModel.updateCoins((reward * 2), true);
    }
}