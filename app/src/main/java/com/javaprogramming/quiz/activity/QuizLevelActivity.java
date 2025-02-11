package com.javaprogramming.quiz.activity;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.database.AppDatabase;
import com.javaprogramming.quiz.viewmodel.QuizViewModel;
import com.javaprogramming.quiz.database.SaveData;
import com.javaprogramming.quiz.databinding.ActivityQuizLevelBinding;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.utilities.TimeUtils;
import com.javaprogramming.quiz.interfaces.DialogCallback;
import com.javaprogramming.quiz.model.Quiz;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuizLevelActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityQuizLevelBinding activityBinding;

    private CountDownTimer countTimerForSingleQuestion;

    private SweetAlertDialog dialog;

    private QuizViewModel quizViewModel;

    private int quizIndex = 0;
    private int currentQuestionsProgress = 0, currentTimeProgress = 0;
    private int correctAnswer = 0, wrongAnswer = 0;
    private int totalQuiz = 0, currentQuiz = 0;
    private int maxTimeLevel = 30;
    private int hintAvailable = 0;
    private int displayTotalQuestions = 0;

    private String correctAnswerText;
    private byte levelOfQuiz;

    private boolean isCountOn = false;
    private boolean isPaused = false;

    private ArrayList<Quiz> quizzes;

    private Context context;
    private DialogCallback dialogCallback;

    // Define a flag to track pause state
    private boolean isPausedByUser = false;
    private boolean isPauseDialogShown = false;

    private static final long START_TIME_IN_MILLIS = 120000;
    private long mTimerLeftInMillis = START_TIME_IN_MILLIS;
    private long mFixedTimerForCurrentTime = 30000;

    // These variables are used to track the time taken by the user to finish the quiz
    private long startTime = 0; // When the quiz starts or resumed

    // The pausedTime has used outside the pauseTheQuiz separately to track the paused time
    // Because we had use the pauseTheQuiz method in the onResume method
    // If we use it inside the pauseTheQuiz method then it will reset the pausedTime and cannot track exact time
    private long pausedTime; // When the quiz is paused
    private long totalPausedDuration = 0; // Total time spent in paused state


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityQuizLevelBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        // Initialize the context variable
        context = QuizLevelActivity.this;

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        // Get the timer for the all questions based on the selected quiz mode
        setTimeForQuestion();

        activityBinding.txtQuizLevel.setText(Helper.quizLevelConvertToText(levelOfQuiz));
        activityBinding.txtQuizCategory.setText(SaveData.getClickedCategoryTitle(context));

        displayTotalQuestions = Helper.getNumberOfQuestionShouldDisplay(levelOfQuiz);

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
            hintAvailable =  savedInstanceState.getInt("hintAvailable",0);
            activityBinding.txtHintAvailable.setText(valueOf(hintAvailable));

        } else {

            // Get total hint available
            setTotalHint();

            // Initialize the empty quizLists array
            quizzes = new ArrayList<>();
            // Get the question and answers from the json file
            getQuestions();

        }

        activityBinding.txtQuizLeft.setText(format("%d/%d", currentQuiz, totalQuiz));

        // Call the handleCallback method to call the interfaces
        handleCallback();

    }

    private void initializeClickableUI() {
        activityBinding.imgBtnPause.setOnClickListener(this);
        activityBinding.btn1Lin.setOnClickListener(this);
        activityBinding.btn2Lin.setOnClickListener(this);
        activityBinding.btn3Lin.setOnClickListener(this);
        activityBinding.btn4Lin.setOnClickListener(this);
        activityBinding.imgBtnHint.setOnClickListener(this);
    }

    private void setTotalHint(){
        // Set total hint available based on quiz level for current quiz
        if (levelOfQuiz == Helper.EASY_MODE){
            hintAvailable = 3;
        }else if(levelOfQuiz == Helper.INTERMEDIATE_MODE){
            hintAvailable = 5;
        }else if(levelOfQuiz == Helper.ADVANCE_MODE){
            hintAvailable = 5;
        }

        activityBinding.txtHintAvailable.setText(valueOf(hintAvailable));
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
        Helper.navigateToActivity(context, MainActivity.class, bundle, true);
    }

    @Override
    public void onClick(View v) {

        // When the user click the pause button
        if (v.getId() == R.id.imgBtnPause){
            pausedTime = System.currentTimeMillis();
            pauseTheQuiz();
            displayPauseDialog();
        }

        // When the user click the hint button
        if (v.getId() == R.id.imgBtnHint){
            // Call the method to show the hint text
            // If displayHint argument is true, it will show the hint text
            isHintAvailable(quizIndex, true);
        }

        // Check if the clicked view is one of the specific buttons (btn1Lin, btn2Lin, btn3Lin, and btn4Lin)
        if (v.getId() == R.id.btn1Lin || v.getId() == R.id.btn2Lin || v.getId() == R.id.btn3Lin || v.getId() == R.id.btn4Lin) {

            // Reset the timer for the next question
            resetTimeForNextQuestion();

            // Disable the linearLayout to prevent multiple clicks for the same question
            enableOrDisableButton(false);

            // Ensure the clicked view is a LinearLayout
            if (v instanceof LinearLayout selectedLinearLayout) {

                // Get the first child of the LinearLayout (assuming it's the TextView)
                View possibleAnswerTxt = selectedLinearLayout.getChildAt(0);

                // Check if the child is a TextView
                if (possibleAnswerTxt instanceof TextView textView) {

                    // Get the text of the TextView
                    String selectedText = textView.getText().toString();

                    // Call the method to check and update the answer
                    checkAndUpdateAnswer(selectedLinearLayout, selectedText);
                }
            }

            updateQuizNum();

            // Wait few seconds for the next question to be displayed
            waitAfterEachQuestion();
        }

    }

    private void checkAndUpdateAnswer(LinearLayout selectedLinearLayout, String selectedText) {
        if (correctAnswerText.equals(selectedText)) {
            correctAnswer++; // Increase correct answer by 1
            // Change the selected linearLayout background to green (correct answer)
            selectedLinearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rightanswer));
        } else {
            wrongAnswer++; // Increase wrong answer by 1
            // Change the selected linearLayout background to red (wrong answer)
            selectedLinearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.wronganswer));

            // Now highlight the correct answer, but do not highlight the selected wrong answer
            highlightCorrectAnswer(selectedLinearLayout);
        }
    }

    private void highlightCorrectAnswer(LinearLayout selectedLinearLayout) {
        // Iterate over the layouts and set the background for the correct answer
        LinearLayout[] layouts = {activityBinding.btn1Lin, activityBinding.btn2Lin, activityBinding.btn3Lin, activityBinding.btn4Lin};
        for (LinearLayout layout : layouts) {
            // Check if the layout has been marked as wrong already
            if (layout != selectedLinearLayout) { // Skip the selected wrong answer
                View possibleAnswer = layout.getChildAt(0);
                if (possibleAnswer instanceof TextView answerTextView) {
                    if (correctAnswerText.equals(answerTextView.getText().toString())) {
                        layout.setBackground(ContextCompat.getDrawable(context, R.drawable.rightanswer)); // Highlight correct answer
                    }
                }
            }
        }
    }

    // Check the level of the quiz and set the timer accordingly
    private void setTimeForQuestion(){

        // Get the level of quiz from the SharedPreferences
        levelOfQuiz = SaveData.getQuizLevel(context);

        if (levelOfQuiz == Helper.EASY_MODE){
            mFixedTimerForCurrentTime = 30000;
            maxTimeLevel = 30;

        }else if(levelOfQuiz == Helper.INTERMEDIATE_MODE){
            mFixedTimerForCurrentTime = 20000;
            maxTimeLevel = 20;

        }else if(levelOfQuiz == Helper.ADVANCE_MODE){
            mFixedTimerForCurrentTime = 20000;
            maxTimeLevel = 20;
        }

        mTimerLeftInMillis = mFixedTimerForCurrentTime;

    }

    public void pauseTheQuiz() {
        activityBinding.imgBtnPause.setImageResource(R.drawable.play_circle_24);
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

        activityBinding.imgBtnPause.setImageResource(R.drawable.rounded_pause_circle_24);

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
        activityBinding.txtQuizLeft.setText(format("%d/%d", currentQuiz, totalQuiz));
        activityBinding.questionProgressID.setProgress(currentQuestionsProgress);
    }

    private void resetLinearLayoutBackgrounds() {

        // Create an array of LinearLayout views representing as a buttons
        LinearLayout[] layouts = {activityBinding.btn1Lin, activityBinding.btn2Lin, activityBinding.btn3Lin, activityBinding.btn4Lin};

        // Iterate through each LinearLayout in the array
        for (LinearLayout layout : layouts) {
            // Set the default background drawable to 'quiz_option_bg' for each layout
            layout.setBackground(ContextCompat.getDrawable(context, R.drawable.quiz_option_bg));
        }

    }

    private void enableOrDisableButton(boolean trueOrFalse) {
        // Prevent pausing the quiz when the user clicks on an answer button
        // It should only be enabled when the quiz is running and not when the timer is 0
        activityBinding.imgBtnPause.setEnabled(trueOrFalse);

        // LinearLayout for the possible question and act as a buttons
        activityBinding.btn1Lin.setEnabled(trueOrFalse);
        activityBinding.btn2Lin.setEnabled(trueOrFalse);
        activityBinding.btn3Lin.setEnabled(trueOrFalse);
        activityBinding.btn4Lin.setEnabled(trueOrFalse);

        // Hint button set to true or false
        activityBinding.imgBtnHint.setEnabled(trueOrFalse);

        // Set background of hint linear layout based on the boolean value
        if (trueOrFalse) {
            activityBinding.hintRelLayer.setBackground(ContextCompat.getDrawable(context, R.drawable.hint_bg));
        }else {
            activityBinding.hintRelLayer.setBackground(ContextCompat.getDrawable(context, R.drawable.hintbgdis));
        }
    }

    @SuppressLint("DefaultLocale")
    private void getQuestions(){
        int mainCategoryID = SaveData.getCategoryID(context);

        // Get the instance of SeenQuestionDao from your repository or database
//        SeenQuestionDao seenQuestionDao = AppDatabase.getDatabase(context).seenQuestionDao();
        // Call QuizService to fetch filtered quizzes from the cached data
//        quizLists = QuizRepository.getFilteredQuizzes(mainCategoryID, levelOfQuiz, displayTotalQuestions);

        if (!quizzes.isEmpty()){
            shuffleAndPrepareData(quizzes);
        }
    }

    private void shuffleAndPrepareData(ArrayList<Quiz> quizDataLists){
        Collections.shuffle(quizDataLists);
        // Trim the list to only the first 10 questions
        if (quizDataLists.size() >= displayTotalQuestions) {
            quizDataLists = new ArrayList<>(quizDataLists.subList(0, displayTotalQuestions));
        }

        loadQuizData(quizDataLists);
    }
    @SuppressLint("DefaultLocale")
    private void loadQuizData(ArrayList<Quiz> quizDataLists) {

        nextQuestion();

        totalQuiz = quizDataLists.size();

        activityBinding.questionProgressID.setProgress(currentQuestionsProgress);
        activityBinding.questionProgressID.setMax(totalQuiz);
        setCountTimerForSingleQuestion();

        activityBinding.circleProgressID.setMax(maxTimeLevel);
        activityBinding.txtQuizLeft.setText(format("%d/%d", currentQuiz, totalQuiz));
    }

    public void checkCanGo(){

        if (quizIndex == (totalQuiz - 1)){
            // Cancel the timer
            countTimerForSingleQuestion.cancel();
            // This is end of the quiz. Now prepare to go to the result page
            quizFinished();

        }else{
            // Question still left to display

            quizIndex++;

            // Reset the linear layout background to default
            resetLinearLayoutBackgrounds();

            // Reset the button states
            enableOrDisableButton(true);

            nextQuestion();
            resetProgressTime();
            setCountTimerForSingleQuestion();
        }
    }

    public void nextQuestion(){

        activityBinding.txtHint.setVisibility(View.GONE);

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
        activityBinding.txtQuizQuestion.setText(quizzes.get(quizIndex).getQuestion_Q());

        // Set possible answer text to the button UI
        activityBinding.txtOpt1.setText(possibleAnswers.get(0));
        activityBinding.txtOpt2.setText(possibleAnswers.get(1));
        activityBinding.txtOpt3.setText(possibleAnswers.get(2));
        activityBinding.txtOpt4.setText(possibleAnswers.get(3));

        isHintAvailable(quizIndex, false);

        // Start time to track how long it take the user to finish the quiz
        if (startTime == 0) { // Set only once
            startTime = System.currentTimeMillis();
        }

        // Save as already seen the question
//        quizViewModel.markQuestionAsSeen(quizzes.get(quizIndex).getCategoryID(), quizzes.get(quizIndex).getQuestionID(), levelOfQuiz);

    }

    public void waitAfterEachQuestion() {

        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) { }
            public void onFinish() {
                checkCanGo();
            }
        }.start();

    }

    public void resetProgressTime(){
        currentTimeProgress = 0;
        activityBinding.circleProgressID.setProgress(currentTimeProgress);
        activityBinding.circleProgressID.setMax(maxTimeLevel);
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

    private void isHintAvailable(int quizIndex, boolean displayHint) {
        // Get the hint text from the list based on the current quiz index
        String hintStringTxt = quizzes.get(quizIndex).getHint_Q();

        // Handle the scenario where no hints are available or hint is "0"
        if (hintAvailable <= 0 || hintStringTxt.equals("0")) {
            activityBinding.imgBtnHint.setEnabled(false);
            activityBinding.txtHint.setVisibility(View.GONE);
            activityBinding.hintRelLayer.setBackground(ContextCompat.getDrawable(context, R.drawable.hintbgdis));
            return;
        }

        // If hints are available
        activityBinding.imgBtnHint.setEnabled(true);
        activityBinding.hintRelLayer.setBackground(ContextCompat.getDrawable(context, R.drawable.hint_bg));

        // Handle display of the hint if requested
        if (displayHint) {
            hintAvailable--; // Decrease the number of available hints
            hintAvailable = Math.max(hintAvailable, 0); // make sure the hint never goes below 0
            activityBinding.txtHint.setText(hintStringTxt);
            activityBinding.txtHintAvailable.setText(valueOf(hintAvailable));
            activityBinding.txtHint.setVisibility(View.VISIBLE);
            activityBinding.imgBtnHint.setEnabled(false);
            activityBinding.hintRelLayer.setBackground(ContextCompat.getDrawable(context, R.drawable.hintbgdis));
        }
    }

    public void quizFinished(){

        Bundle data = new Bundle();
        data.putInt("correct", correctAnswer);
        data.putInt("wrong", wrongAnswer);
        data.putInt("total", totalQuiz);

        // Calculate playtime
        long timeTaken = TimeUtils.calculatePlayTime(startTime, System.currentTimeMillis(), totalPausedDuration);
        data.putLong("timeTaken", timeTaken);

//        Intent intent = new Intent(context, ResultActivity.class);
//        intent.putExtras(data);
//        startActivity(intent);
        finish();
    }

    public void setCountTimerForSingleQuestion(){
        countTimerForSingleQuestion = new CountDownTimer(mTimerLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerLeftInMillis = millisUntilFinished;
                currentTimeProgress = currentTimeProgress + 1;
                activityBinding.txtQuizTime.setText(valueOf(mTimerLeftInMillis / 1000));
                activityBinding.circleProgressID.setProgress(currentTimeProgress);
            }
            @Override
            public void onFinish() {
                isCountOn = false;
                updateQuizNum();
                resetProgressTime();
                checkCanGo();
            }
        }.start();

        isCountOn = true;  // Mark the timer as running
        isPaused = false;  // Reset the paused state
    }

    private void resetTimeForNextQuestion(){
        if (isCountOn) countTimerForSingleQuestion.cancel();
        activityBinding.txtHint.setVisibility(View.GONE);
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}