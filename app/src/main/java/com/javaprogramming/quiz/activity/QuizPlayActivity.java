package com.javaprogramming.quiz.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.String.format;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.viewmodel.QuizViewModel;
import com.javaprogramming.quiz.database.SaveData;

import com.javaprogramming.quiz.viewmodel.UserViewModel;
import com.javaprogramming.quiz.databinding.ActivityQuizPlayBinding;
import com.javaprogramming.quiz.databinding.BuyTimerLayoutBinding;
import com.javaprogramming.quiz.interfaces.DialogCallback;
import com.javaprogramming.quiz.model.Quiz;
import com.javaprogramming.quiz.model.User;
import com.javaprogramming.quiz.utilities.AudioHelper;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.utilities.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuizPlayActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityQuizPlayBinding binding;
    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;  // Track mute state
    
    private CountDownTimer countTimerForSingleQuestion;
    private SweetAlertDialog dialog;


    private UserViewModel userViewModel;

    private int userID = 1;

    private int quizIndex = 0;
    private int currentQuestionsProgress = 0, currentTimeProgress = 0;
    private int correctAnswer = 0, wrongAnswer = 0;
    private int totalQuiz = 0, currentQuiz = 0;
    private int hintAvailable =0, fiftyFiftyAvailable= 0, timerFeatureAvailable = 0, coin=0;

    private String correctAnswerText;

    private boolean isCountOn = false;
    private boolean isPaused = false;

    private ArrayList<Quiz> quizzes;

    private Context context;
    private DialogCallback dialogCallback;

    // Define a flag to track pause state
    private boolean isPausedByUser = false;
    private boolean isPauseDialogShown = false;

    private long mFixedTimerForCurrentTime = 15000;
    private long mTimerLeftInMillis = mFixedTimerForCurrentTime;

    // These variables are used to track the time taken by the user to finish the quiz
    private long startTime = 0; // When the quiz starts or resumed

    // The pausedTime has used outside the pauseTheQuiz separately to track the paused time
    // Because we had use the pauseTheQuiz method in the onResume method
    // If we use it inside the pauseTheQuiz method then it will reset the pausedTime and cannot track exact time
    private long pausedTime; // When the quiz is paused
    private long totalPausedDuration = 0; // Total time spent in paused state

    private boolean isClicked;

    private Dialog timerFeatureDialog;

    private HashMap<Integer, Integer> selectedAnswersMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the context variable
        context = QuizPlayActivity.this;

        isMuted = SaveData.getIsMuted(context);

        audioSoundHandler();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.txtHint.setVisibility(GONE);

        binding.txtLevelTitle.setText(SaveData.getClickedCategoryTitle(context));

        // Set onclickListener to the clickable element of the UI
        initializeClickableUI();

        if (savedInstanceState != null) {
            quizIndex = savedInstanceState.getInt("quiz_index", 0); // Restore index
            quizzes = (ArrayList<Quiz>) savedInstanceState.getSerializable("quiz_list"); // Restore quiz list
            loadQuizData(quizzes);

            correctAnswer =  savedInstanceState.getInt("correctAnswer",0);
            wrongAnswer =  savedInstanceState.getInt("wrongAnswer",0);

            currentQuiz =  savedInstanceState.getInt("currentQuiz",0);

            currentTimeProgress =  savedInstanceState.getInt("currentTimeProgress",0);
            currentQuestionsProgress =  savedInstanceState.getInt("currentQuestionsProgress",0);


            startTime =  savedInstanceState.getLong("startTime",0);
            pausedTime =  savedInstanceState.getLong("pausedTime",0);
            totalPausedDuration =  savedInstanceState.getLong("totalPausedDuration",0);

            mTimerLeftInMillis =  savedInstanceState.getLong("timerLeftToPlay",15000);

        } else {

            // Initialize the empty quizLists array
            quizzes = new ArrayList<>();
            // Get the question and answers from the json file
            getQuestions();

        }

        binding.txtTotalQuestions.setText(format("%d/%d", currentQuiz, totalQuiz));


        // Observe the LiveData to get updates whenever the user data changes
        userViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // Update UI with user data (e.g., display user coins, hints)
                if (user != null) {
                    userID = user.getUserID_U();
                    coin = user.getCoins_U();
                    hintAvailable = user.getHints_U();
                    fiftyFiftyAvailable = user.getFiftyFifty_U();
                    timerFeatureAvailable = user.getTimerFeature_U();

                    binding.layoutQuizCoin.txtCoin.setText(String.valueOf(user.getCoins_U()));
                    binding.txtHintAvailable.setText(String.valueOf(user.getHints_U()));
                    binding.txtFiftyFifty.setText(String.valueOf(user.getFiftyFifty_U()));
                    binding.txtTimerFeature.setText(String.valueOf(user.getTimerFeature_U()));

                    setQuizFeatures();
                }
            }
        });

        // Call the handleCallback method to call the interfaces
        handleCallback();

    }

    private void setupTimerFeatureUI() {
        if(timerFeatureAvailable <= 0){
            binding.txtTimerFeature.setVisibility(GONE);
            binding.imgTimerFeatureVideo.setVisibility(VISIBLE);
            binding.layoutTextTimer.setBackgroundResource(R.drawable.bg_round_with_border_white_1px);
        }else{
            binding.txtTimerFeature.setVisibility(VISIBLE);
            binding.imgTimerFeatureVideo.setVisibility(GONE);
            binding.layoutTextTimer.setBackgroundResource(R.drawable.bg_circle_white);
        }
    }

    private void setupFiftyFiftyLifeLineUI() {
        if(fiftyFiftyAvailable <= 0){
            binding.txtFiftyFifty.setVisibility(GONE);
            binding.imgFiftyFiftyVideo.setVisibility(VISIBLE);
            binding.layoutTextFiftyFifty.setBackgroundResource(R.drawable.bg_round_with_border_white_1px);
        }else{
            binding.txtFiftyFifty.setVisibility(VISIBLE);
            binding.imgFiftyFiftyVideo.setVisibility(GONE);
            binding.layoutTextFiftyFifty.setBackgroundResource(R.drawable.bg_circle_white);
        }
    }

    private void setQuizFeatures(){
        // Set total hint available based on quiz level for current quiz

        setupHintUI();
        setupTimerFeatureUI();
        setupFiftyFiftyLifeLineUI();
    }

    private void initializeClickableUI() {
        binding.layerHint.setOnClickListener(this);
        binding.layerBtnFiftyFifty.setOnClickListener(this);
        binding.layerBtnTimerFeature.setOnClickListener(this);

        // Possible answer button
        binding.btnOption1.setOnClickListener(this);
        binding.btnOption2.setOnClickListener(this);
        binding.btnOption3.setOnClickListener(this);
        binding.btnOption4.setOnClickListener(this);

        binding.layerBtnBack.setOnClickListener(this);
        binding.layerBtnRestart.setOnClickListener(this);
        binding.layerBtnSound.setOnClickListener(this);
        binding.layerBtnPauseResume.setOnClickListener(this);
    }

    private void handleCallback() {
        // DialogCallback
        dialogCallback = new DialogCallback() {
            @Override
            public void onConfirm(String purpose) {
                // When the user back press
                if (purpose.equals(Helper.getDialogPurposeText(Helper.DialogPurpose.EXIT))){
                    goToCategoryBack();
                }

                // When the user select the pause button
                if (purpose.equals(Helper.getDialogPurposeText(Helper.DialogPurpose.PAUSE))){
                    resumeTheQuiz(); // Resume the quiz
                }
            }

            @Override
            public void onCancel(String purpose) {
                // When the user back press to exit
                if (purpose.equals(Helper.getDialogPurposeText(Helper.DialogPurpose.EXIT))){
                    // Here the timer should again start as the user decide to continue the quiz
                    if (!isCountOn && countTimerForSingleQuestion != null) {
                        resumeTheQuiz();
                        isCountOn = true; // Update the state
                    }
                }
                // When the user select the pause button
                if (purpose.equals(Helper.getDialogPurposeText(Helper.DialogPurpose.PAUSE))){
                    goToCategoryBack();
                }
            }
        };

        // Register back-press callback
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Call the method to show the exit dialog
                pausedTime = System.currentTimeMillis();
                pauseTheQuiz();
                displayExitDialog();
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);

    }

    private void goToCategoryBack(){
        // Go back to the category activity if the user click exit from the dialog
        Bundle bundle = new Bundle();
        bundle.putString("frm", "quiz");
        Helper.navigateToActivity(context, CategoryActivity.class, bundle, true);
    }

    @Override
    public void onClick(View v) {
        // When the user click the sound button
        if (v.getId() == R.id.layer_btn_sound){
            toggleMute();
        }
        // When the user click the pause button
        if (v.getId() == R.id.layer_btn_pause_resume){
            pausedTime = System.currentTimeMillis();
            pauseTheQuiz();
            displayPauseDialog();
        }

        // When the user click the hint button
        if (v.getId() == R.id.layer_hint){
            // Call the method to show the hint text
            // If displayHint argument is true, it will show the hint text
            handleHintButtonClick();
        }

        // When the user click the back button
        if (v.getId() == R.id.layer_btn_back){
            displayExitDialog();
        }

        // When the user click the fifty fifty button
        if (v.getId() == R.id.layer_btn_fifty_fifty){
            handleFiftyFiftyButtonClick();
        }

        // When the user click the timer feature button
        if (v.getId() == R.id.layer_btn_timer_feature){
            if (timerFeatureAvailable > 0){
                timerExtend(false);
            }else{
//                pauseTheQuiz();
                displayBuyTimer(context,"Timer");
            }
        }

        // Check if the clicked view is one of the specific buttons (btn1Lin, btn2Lin, btn3Lin, and btn4Lin)
        if (v.getId() == R.id.btn_option1 || v.getId() == R.id.btn_option2 || v.getId() == R.id.btn_option3 || v.getId() == R.id.btn_option4) {

            // User clicked any 1 button from 4 buttons
            isClicked = true;

            binding.txtHint.setVisibility(GONE);
            // Reset the timer for the next question
            resetTimeForNextQuestion();


            // Disable the linearLayout to prevent multiple clicks for the same question
            enableOrDisableButton(false);

            // Ensure the clicked view is a LinearLayout
            if (v instanceof AppCompatButton selectedLinearLayout) {

                // Get the text of the TextView
                String selectedText = selectedLinearLayout.getText().toString();

                // Call the method to check and update the answer
                checkAndUpdateAnswer(selectedLinearLayout, selectedText);
            }

            updateQuizNum();

            // Wait few seconds for the next question to be displayed
            waitAfterEachQuestion();
        }

    }

    private void handleFiftyFiftyButtonClick() {
        if (fiftyFiftyAvailable > 0){
            userViewModel.updateFiftyFifty(1, false);
            binding.layerBtnFiftyFifty.setEnabled(false);
            setupFiftyFiftyButton();
        }else{
            displayBuyTimer(context,"Fifty");
        }
    }

    private void setupFiftyFiftyButton() {
        AppCompatButton[] layouts = {binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4};

        // Find the correct answer button
        AppCompatButton correctAnswer = null;
        for (AppCompatButton layout : layouts) {
            if (correctAnswerText.equals(layout.getText().toString())) {
                correctAnswer = layout;
                break; // Exit the loop once the correct answer is found
            }
        }

        // Create a new list excluding the correct answer
        List<AppCompatButton> filteredLayouts = new ArrayList<>();
        for (AppCompatButton layout : layouts) {
            if (!layout.equals(correctAnswer)) {
                filteredLayouts.add(layout);
            }
        }

        // Randomly select two buttons from the filtered list
        Random random = new Random();
        int firstIndex = random.nextInt(filteredLayouts.size()); // Random index
        int secondIndex;

        do {
            secondIndex = random.nextInt(filteredLayouts.size()); // Ensure indices are different
        } while (secondIndex == firstIndex);

        // Select the buttons
        AppCompatButton firstButton = filteredLayouts.get(firstIndex);
        AppCompatButton secondButton = filteredLayouts.get(secondIndex);

        // Disable the selected buttons
        firstButton.setEnabled(false);
        secondButton.setEnabled(false);

        firstButton.setBackgroundResource(R.drawable.primary_transparent_50_15px);
        secondButton.setBackgroundResource(R.drawable.primary_transparent_50_15px);

    }

    private void checkAndUpdateAnswer(AppCompatButton selectedLinearLayout, String selectedText) {

        selectedAnswersMap.put(quizzes.get(quizIndex).getQuestionID_Q(), getSelectedOption(selectedText, quizIndex));

        if (correctAnswerText.equals(selectedText)) {
            correctAnswer++; // Increase correct answer by 1
            // Change the selected linearLayout background to green (correct answer)

            mediaPlayer = MediaPlayer.create(context, R.raw.correct);

            selectedLinearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rightanswer));
        } else {
            mediaPlayer = MediaPlayer.create(context, R.raw.error);
            wrongAnswer++; // Increase wrong answer by 1
            // Change the selected linearLayout background to red (wrong answer)
            selectedLinearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.wronganswer));

            // Now highlight the correct answer, but do not highlight the selected wrong answer
            highlightCorrectAnswer(selectedLinearLayout);
        }

        if (!isMuted) {
            AudioHelper.playMusic(mediaPlayer);
        }

    }

    // This function will highlight the correct answer button but no matter what the user select
    private void highlightCorrectAnswer(AppCompatButton selectedLinearLayout) {
        // Iterate over the layouts and set the background for the correct answer
        AppCompatButton[] layouts = {binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4};
        for (AppCompatButton layout : layouts) {
            // Check if the layout has been marked as wrong already
            if (layout != selectedLinearLayout) { // Skip the selected wrong answer

                if (correctAnswerText.equals(layout.getText().toString())) {
                    layout.setBackground(ContextCompat.getDrawable(context, R.drawable.rightanswer)); // Highlight correct answer
                }
            }
        }
    }

    public void pauseTheQuiz() {
        binding.imgPauseResume.setImageResource(R.drawable.play_circle_24);
        if (isCountOn && countTimerForSingleQuestion != null) {

            // Cancel the timer if it's running
            countTimerForSingleQuestion.cancel();
            isCountOn = false; // Update the flag to indicate the timer is not running
            isPaused = true; // Update the flag to indicate the quiz is not in pause state

        }
    }

    public void displayPauseDialog() {
        dialog = Helper.displayDialog(
                context,
                SweetAlertDialog.WARNING_TYPE,
                "Quiz Paused",
                "You can resume the quiz whenever you're ready!",
                "Resume",
                "Exit",
                dialogCallback,
                Helper.getDialogPurposeText(Helper.DialogPurpose.PAUSE)

        );

        // Reset the flag when the dialog is dismissed
        dialog.setOnDismissListener(dialogInterface -> isPauseDialogShown = false);

        dialog.show();
        isPausedByUser = true;
        isPauseDialogShown = true;
    }

    public void resumeTheQuiz() {

        binding.imgPauseResume.setImageResource(R.drawable.rounded_pause_circle_24);

        // Record the time when the pause ended
        // When the quiz is resume or start again
        long resumeTime = System.currentTimeMillis();

        // Calculate and add the duration of this pause to the total paused time
        totalPausedDuration += resumeTime - pausedTime;

        // Restart the timer
        setCountTimerForSingleQuestion();

    }

    @SuppressLint("DefaultLocale")
    private void updateQuizNum(){
        currentQuiz++;
        currentQuestionsProgress++;
        binding.txtTotalQuestions.setText(format("%d/%d", currentQuiz, totalQuiz));
    }

    private void resetLinearLayoutBackgrounds() {

        // Create an array of LinearLayout views representing as a buttons
        AppCompatButton[] layouts = {binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4};

        // Iterate through each LinearLayout in the array
        for (AppCompatButton layout : layouts) {
            // Set the default background drawable to 'quiz_option_bg' for each layout
            layout.setBackground(ContextCompat.getDrawable(context, R.drawable.primary_15px));
        }
    }

    private void enableOrDisableButton(boolean trueOrFalse) {
        // Prevent pausing the quiz when the user clicks on an answer button
        // It should only be enabled when the quiz is running and not when the timer is 0
        binding.layerBtnPauseResume.setEnabled(trueOrFalse);

        // LinearLayout for the possible question and act as a buttons
        binding.btnOption1.setEnabled(trueOrFalse);
        binding.btnOption2.setEnabled(trueOrFalse);
        binding.btnOption3.setEnabled(trueOrFalse);
        binding.btnOption4.setEnabled(trueOrFalse);

        // Hint button set to true or false
        binding.layerHint.setEnabled(trueOrFalse);

        binding.layerBtnFiftyFifty.setEnabled(trueOrFalse);
        binding.layerBtnTimerFeature.setEnabled(trueOrFalse);
    }

    @SuppressLint("DefaultLocale")
    private void getQuestions(){
        int mainCategoryID = SaveData.getCategoryID(context);

        // Initialize QuizRepository
        QuizViewModel quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        quizzes = (ArrayList<Quiz>) quizViewModel.getQuizzesForSet(mainCategoryID, calculateOffset(SaveData.getSetsNumber(context)));

        // Now you can process the quizzes
        if (quizzes != null && !quizzes.isEmpty()) {
            shuffleAndPrepareData(quizzes);
        }
    }

    private void shuffleAndPrepareData(List<Quiz> quizDataLists){
        Collections.shuffle(quizDataLists);
        // Trim the list to only the first 10 questions - Here

        loadQuizData(quizDataLists);
    }

    @SuppressLint("DefaultLocale")
    private void loadQuizData(@NonNull List<Quiz> quizDataLists) {
        totalQuiz = quizDataLists.size();
        nextQuestion();
        setCountTimerForSingleQuestion();
        binding.txtTotalQuestions.setText(format("%d/%d", currentQuiz, totalQuiz));
    }

    public void checkCanGo(){
        if (quizIndex == (totalQuiz - 1)){
            // Cancel the timer
            if (countTimerForSingleQuestion != null){
                countTimerForSingleQuestion.cancel();
            }
            // This is end of the quiz. Now prepare to go to the result page
            quizFinished();

        }else{

            if (countTimerForSingleQuestion != null){
                countTimerForSingleQuestion.cancel();
            }

            // Question still left to display

            quizIndex++;

            // Reset the linear layout background to default
            resetLinearLayoutBackgrounds();

            // Reset the button states
            enableOrDisableButton(true);

            nextQuestion();
            resetProgressTime();
            setCountTimerForSingleQuestion();
            binding.btnOption1.setVisibility(VISIBLE);
            binding.btnOption2.setVisibility(VISIBLE);
            binding.btnOption3.setVisibility(VISIBLE);
            binding.btnOption4.setVisibility(VISIBLE);
        }
    }

    public void nextQuestion(){

        isClicked = false;

        // Get the correct answer number
        byte currentAnswer = quizzes.get(quizIndex).getAnswer_Q();

        // Get the current answer text according to the answer number
        switch (currentAnswer){
            case 1:
                correctAnswerText = quizzes.get(quizIndex).getOptionA_Q();
                break;
            case 2:
                correctAnswerText = quizzes.get(quizIndex).getOptionB_Q();
                break;
            case 3:
                correctAnswerText = quizzes.get(quizIndex).getOptionC_Q();
                break;
            default:
                correctAnswerText = quizzes.get(quizIndex).getOptionD_Q();
                break;
        }

        // Create a List of possible answers
        List<String> possibleAnswers = new ArrayList<>();
        possibleAnswers.add(quizzes.get(quizIndex).getOptionA_Q());
        possibleAnswers.add(quizzes.get(quizIndex).getOptionB_Q());
        possibleAnswers.add(quizzes.get(quizIndex).getOptionC_Q());
        possibleAnswers.add(quizzes.get(quizIndex).getOptionD_Q());

        // Shuffle the possible answer
        Collections.shuffle(possibleAnswers);

        // Set the current question to the textview UI
        binding.txtCurrentQuestion.setText(quizzes.get(quizIndex).getQuestion_Q());

        // Set possible answer text to the button UI
        binding.btnOption1.setText(possibleAnswers.get(0));
        binding.btnOption2.setText(possibleAnswers.get(1));
        binding.btnOption3.setText(possibleAnswers.get(2));
        binding.btnOption4.setText(possibleAnswers.get(3));

        setupHintUI();

        // Start time to track how long it take the user to finish the quiz
        if (startTime == 0) { // Set only once
            startTime = System.currentTimeMillis();
        }

    }

    private int getSelectedOption(String selectedAnswer, int quizIndex){
        if (selectedAnswer.equals(quizzes.get(quizIndex).getOptionA_Q())) {
            return 1; // Option A
        } else if (selectedAnswer.equals(quizzes.get(quizIndex).getOptionB_Q())) {
            return 2; // Option B
        } else if (selectedAnswer.equals(quizzes.get(quizIndex).getOptionC_Q())) {
            return 3; // Option C
        } else if (selectedAnswer.equals(quizzes.get(quizIndex).getOptionD_Q())) {
            return 4; // Option D
        }
        return 0;
    }

    public void waitAfterEachQuestion() {

        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) { }
            public void onFinish() {

                checkCanGo();
                binding.txtHint.setVisibility(View.GONE);
            }
        }.start();

    }

    public void resetProgressTime(){
        currentTimeProgress = 0;

        // Reset the timer here for next question
        mTimerLeftInMillis = mFixedTimerForCurrentTime;
    }

    public void displayExitDialog() {
        dialog = Helper.displayDialog(
                context,
                SweetAlertDialog.WARNING_TYPE,
                "Exit from Quiz!!!",
                "Are you sure you want to exit from quiz?",
                "Yes",
                null,
                dialogCallback,
                Helper.getDialogPurposeText(Helper.DialogPurpose.EXIT)
        );

        // Reset the flag when the dialog is dismissed
        dialog.setOnDismissListener(dialogInterface -> isPauseDialogShown = false);

        dialog.show();
        isPausedByUser = true;
        isPauseDialogShown = true;
    }

    private void setupHintUI(){

//        binding.txtHint.setVisibility(View.GONE);

        if (isHintAvailable()){
            binding.txtHintAvailable.setVisibility(VISIBLE);
            binding.imgHintVideo.setVisibility(GONE);
            binding.layoutTextHint.setBackgroundResource(R.drawable.bg_circle_white);
        }else{
            binding.txtHintAvailable.setVisibility(GONE);
            binding.imgHintVideo.setVisibility(VISIBLE);
            binding.layoutTextHint.setBackgroundResource(R.drawable.bg_round_with_border_white_1px);
        }
    }

    private void handleHintButtonClick(){
        if (isHintAvailable()){
            userViewModel.updateHints(1, false);
            binding.txtHint.setText(quizzes.get(quizIndex).getHint_Q());
            binding.txtHint.setVisibility(VISIBLE);
            binding.layerHint.setEnabled(false);
        }else{
            displayBuyTimer(context, "Hint");
        }
    }

    public void quizFinished(){
        Bundle data = new Bundle();
        data.putInt("correct", correctAnswer);
        data.putInt("userID", userID);
        data.putInt("wrong", wrongAnswer);
        data.putInt("total", totalQuiz);
        data.putSerializable("selectedAnswer", selectedAnswersMap);
        data.putSerializable("quizList", quizzes);

        // Calculate playtime
        long timeTaken = TimeUtils.calculatePlayTime(startTime, System.currentTimeMillis(), totalPausedDuration);
        data.putLong("timeTaken", timeTaken);

        Helper.navigateToActivity(context, QuizCompleteActivity.class, data, true);
    }

    public void setCountTimerForSingleQuestion(){
        countTimerForSingleQuestion = new CountDownTimer(mTimerLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerLeftInMillis = millisUntilFinished;
                binding.txtTimer.setText(valueOf(mTimerLeftInMillis / 1000));
            }
            @Override
            public void onFinish() {

                if (isClicked){
                    isCountOn = false;
                    updateQuizNum();
                    resetProgressTime();
                    checkCanGo();
                }else{
                    // Ask for extend timer
                    displayBuyTimer(context,"Timer");
                }

                // Reset isClicked flag to false
                isClicked = false;

            }
        }.start();

        isCountOn = true;  // Mark the timer as running
        isPaused = false;  // Reset the paused state
    }

    private void timerExtend(boolean instantUse){
        if (!instantUse){
            userViewModel.updateTimerFeature(1, false);
        }
        mTimerLeftInMillis += 10000;
        startNewTimer();  // Restart the timer with the updated time

        binding.txtTimer.setText(valueOf(mTimerLeftInMillis / 1000));
    }

    private void displayBuyTimer(Context context, String frm) {

        pauseTheQuiz();

        // Inflate the custom layout using ViewBinding
        LayoutInflater inflater = LayoutInflater.from(context);
        BuyTimerLayoutBinding bindingDialog = BuyTimerLayoutBinding.inflate(inflater);

        int itemPrice;

        String title = context.getString(R.string.timer_buy_title);
        String desc = context.getString(R.string.common_buy_description);

        if (frm.equals("Timer")){
            desc = context.getString(R.string.timer_extend_description);
        }
        if (frm.equals("Fifty")) {
            itemPrice = 30;
            title = context.getString(R.string.fifty_fifty_buy_title);

        } else if (frm.equals("Hint")) {
            itemPrice = 20;
            title = context.getString(R.string.hint_buy_title);
        } else {
            itemPrice = 10;
        }

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return; // Avoid crashes if the context is invalid or the activity is finishing
        }

        // Check if the dialog is already showing
        if (timerFeatureDialog != null && timerFeatureDialog.isShowing()) {
            return;
        }

        // Initialize the dialog and set its content view using binding
        timerFeatureDialog = new Dialog(context);
        timerFeatureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timerFeatureDialog.setContentView(bindingDialog.getRoot()); // Use ViewBinding root view
        timerFeatureDialog.setCancelable(false);
        timerFeatureDialog.setCanceledOnTouchOutside(false);

        // Set dialog window attributes
        if (timerFeatureDialog.getWindow() != null) {
            Window window = timerFeatureDialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        bindingDialog.txtBuyTitle.setText(title);
        bindingDialog.txtBuyDesc.setText(desc);
        // Error message should always be hidden initially
        bindingDialog.txtCustomErrorMessage.setVisibility(View.GONE);

        // Setup UI based on user selected button
        // Such as timer or fifty fifty or hint
        if (frm.equals("Timer")){

            bindingDialog.imgVideoItemLogo.setImageResource(R.drawable.round_timer_24);
            bindingDialog.imgBuyItemLogo.setImageResource(R.drawable.round_timer_24);


            // Set visibility based on timer availability
            // If timer available, display to use it otherwise display button to buy or view video ad
            if (timerFeatureAvailable > 0) {
                bindingDialog.notHaveEnoughTimerLayer.setVisibility(View.GONE);
                bindingDialog.btnCustomExtendTimer.setVisibility(View.VISIBLE);
            } else {
                bindingDialog.notHaveEnoughTimerLayer.setVisibility(View.VISIBLE);
                bindingDialog.btnCustomExtendTimer.setVisibility(View.GONE);
            }

        }else if (frm.equals("Hint")){
            bindingDialog.imgVideoItemLogo.setImageResource(R.drawable.baseline_lightbulb_24);
            bindingDialog.imgBuyItemLogo.setImageResource(R.drawable.baseline_lightbulb_24);
        }else{
            bindingDialog.imgVideoItemLogo.setImageResource(R.drawable.fifty_fifty);
            bindingDialog.imgBuyItemLogo.setImageResource(R.drawable.fifty_fifty);
        }

        bindingDialog.txtPrice.setText(String.valueOf(itemPrice));

        // Extend timer directly
        bindingDialog.btnCustomExtendTimer.setOnClickListener(v -> {
            timerExtend(false);
            //resumeTheQuiz();
            timerFeatureDialog.dismiss();
        });


        // Close dialog on clicking the close button
        bindingDialog.imgCloseDialog.setOnClickListener(v -> {
            resumeTheQuiz();
            timerFeatureDialog.dismiss();
        });

        // Buy with coins
        bindingDialog.layerBtnBuyWithCoin.setOnClickListener(v -> {

            // This part is common for every action
            if (!(coin >= itemPrice)){
                bindingDialog.txtCustomErrorMessage.setVisibility(View.VISIBLE);
                return;
            }

            // Buy features with coins
            switch (frm) {
                case "Timer" -> timerExtend(true);
                case "Hint" -> userViewModel.updateHints(1, true);
                case "Fifty" -> userViewModel.updateFiftyFifty(1, true);
            }

            userViewModel.updateCoins(itemPrice, false);
            timerFeatureDialog.dismiss();

            resumeTheQuiz();

        });

        // Watch video for extra time
        bindingDialog.layerBtnWatchVideo.setOnClickListener(v -> {
            Toast.makeText(context, "Ads loading", Toast.LENGTH_SHORT).show();
            callAdController(frm);
            timerFeatureDialog.dismiss();
        });

        // Go to the next question
        bindingDialog.btnCustomGoToNextQuestion.setOnClickListener(v -> {
            binding.imgPauseResume.setImageResource(R.drawable.rounded_pause_circle_24);
            updateQuizNum();
            resetProgressTime();
            checkCanGo();
            resumeTheQuiz(); // maybe remove it from here
            timerFeatureDialog.dismiss();
        });

        // Show dialog only if it's not already displayed
        if (!timerFeatureDialog.isShowing()) {
            timerFeatureDialog.show();
        }
    }

    private void callAdController(String frm){
        resumeTheQuiz();
        Toast.makeText(QuizPlayActivity.this, "Completed", Toast.LENGTH_SHORT).show();

        switch (frm) {
            case "Hint" -> userViewModel.updateHints(1, true);
            case "Timer" -> timerExtend(false);
            case "Fifty" -> userViewModel.updateFiftyFifty(1, true);
        }
    }

    private void adCompleted(){

    }

    private void startNewTimer() {
        if (countTimerForSingleQuestion != null) {
            countTimerForSingleQuestion.cancel();  // Cancel the old timer
            countTimerForSingleQuestion = null;
        }
        setCountTimerForSingleQuestion();  // Start a new timer with the updated time
    }

    private void resetTimeForNextQuestion(){
        if (isCountOn) countTimerForSingleQuestion.cancel();
        binding.txtHint.setVisibility(View.GONE);
        mTimerLeftInMillis = mFixedTimerForCurrentTime;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isPaused && mTimerLeftInMillis > 0) {
            // Resume the timer from where it was paused
            setCountTimerForSingleQuestion();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if the app is resuming from a pause state
        if (isPausedByUser) {

            isPausedByUser = false; // Reset the flag
            // Pause the quiz again
            pauseTheQuiz();

            // Display the pause dialog if it's not already displayed - the alert dialog can be either for exit or pause
            if (!isPauseDialogShown){
                displayPauseDialog();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Set the flag to indicate the app was paused and paused by the user (not come from another activity)
        isPausedByUser = true;
        // Pause the quiz again
        if (!isPaused) {
            pausedTime = System.currentTimeMillis();
            // If not already paused, pause the quiz
            pauseTheQuiz();
        }
    }

    // Method to toggle mute/unmute
    private void audioSoundHandler() {
        if (!isMuted) {
            binding.imgVolume.setImageResource(R.drawable.baseline_volume_up_24);
            AudioHelper.unmuteMusic(mediaPlayer);  // ✅ Unmute
        } else {
            binding.imgVolume.setImageResource(R.drawable.baseline_volume_off_24);
            AudioHelper.muteMusic(mediaPlayer);    // ✅ Mute
        }
    }

    private void toggleMute() {
        isMuted = !isMuted;
        SaveData.saveIsMuted(isMuted, context);

        audioSoundHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Release MediaPlayer when the activity is stopped
        AudioHelper.releaseMediaPlayer(mediaPlayer);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("quiz_list", quizzes);
        outState.putInt("quiz_index", quizIndex);
        outState.putInt("correctAnswer", correctAnswer);
        outState.putInt("wrongAnswer", wrongAnswer);
        outState.putInt("currentQuiz", currentQuiz);
        outState.putInt("currentTimeProgress", currentTimeProgress);
        outState.putInt("currentQuestionsProgress", currentQuestionsProgress);

        outState.putLong("startTime", startTime);
        outState.putLong("pausedTime", pausedTime);
        outState.putLong("totalPausedDuration", totalPausedDuration);
        outState.putInt("hintAvailable", hintAvailable);
        outState.putLong("timerLeftToPlay", mTimerLeftInMillis);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private boolean isHintAvailable() {
        return hintAvailable > 0 && !quizzes.get(quizIndex).getHint_Q().equals("0");
    }

    public int calculateOffset(int setNumber) {
        // Ensure setNumber is valid (should be >= 1)
        if (setNumber < 1) {
            return 0; // Return offset 0 if invalid setNumber is passed
        }
        return (setNumber - 1) * 10;
    }


    private void handleAnswerSelection(int questionID, int selectedAnswer) {
        // Store the selected answer in the HashMap
        selectedAnswersMap.put(questionID, selectedAnswer);
    }


}