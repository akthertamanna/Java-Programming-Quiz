//package com.javaprogramming.quiz.activity;
//
//import static android.view.View.VISIBLE;
//import static java.lang.String.format;
//import static java.lang.String.valueOf;
//
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//
//import com.javaprogramming.quiz.R;
//import com.javaprogramming.quiz.databinding.ActivityQuizPlayBinding;
//import com.javaprogramming.quiz.interfaces.DialogCallback;
//import com.javaprogramming.quiz.model.Quiz;
//import com.javaprogramming.quiz.utilities.Helper;
//import com.javaprogramming.quiz.viewmodel.QuizViewModel;
//import com.javaprogramming.quiz.viewmodel.UserViewModel;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import cn.pedant.SweetAlert.SweetAlertDialog;
//
//public class QuizPlayActivity extends AppCompatActivity implements View.OnClickListener{
//
//    private ActivityQuizPlayBinding binding;
//    private MediaPlayer mediaPlayer;
//    private boolean isMuted = false;  // Track mute state
//
//    private CountDownTimer countTimerForSingleQuestion;
//    private SweetAlertDialog dialog;
//
//    private QuizViewModel quizViewModel;
//
//    private UserViewModel userViewModel;
//
//    private int userID = 1;
//
//    private int quizIndex = 0;
//    private int currentQuestionsProgress = 0, currentTimeProgress = 0;
//    private int correctAnswer = 0, wrongAnswer = 0;
//    private int totalQuiz = 0, currentQuiz = 0;
//    private int hintAvailable =0, fiftyFiftyAvailable= 0, timerFeatureAvailable = 0, coin=0;
//
//    private String correctAnswerText;
//
//    private boolean isCountOn = false;
//    private boolean isPaused = false;
//
//    private ArrayList<Quiz> quizzes;
//
//    private Context context;
//    private DialogCallback dialogCallback;
//
//    // Define a flag to track pause state
//    private boolean isPausedByUser = false;
//    private boolean isPauseDialogShown = false;
//
//    private long mFixedTimerForCurrentTime = 15000;
//    private long mTimerLeftInMillis = mFixedTimerForCurrentTime;
//
//    // These variables are used to track the time taken by the user to finish the quiz
//    private long startTime = 0; // When the quiz starts or resumed
//
//    // The pausedTime has used outside the pauseTheQuiz separately to track the paused time
//    // Because we had use the pauseTheQuiz method in the onResume method
//    // If we use it inside the pauseTheQuiz method then it will reset the pausedTime and cannot track exact time
//    private long pausedTime; // When the quiz is paused
//    private long totalPausedDuration = 0; // Total time spent in paused state
//
//    private boolean isClicked;
//
//    private Dialog timerFeatureDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityQuizPlayBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        // When the user click the sound button
//
//        // When the user click the pause button
//        if (v.getId() == R.id.layer_btn_pause_resume){
//            pausedTime = System.currentTimeMillis();
//            pauseTheQuiz();
//            displayPauseDialog();
//        }
//
//
//        // When the user click the timer feature button
//        if (v.getId() == R.id.layer_btn_timer_feature){
//            if (timerFeatureAvailable > 0){
//                timerExtend(false);
//            }else{
//                displayBuyTimer(context,"Timer");
//            }
//        }
//
//        // Check if the clicked view is one of the specific buttons (btn1Lin, btn2Lin, btn3Lin, and btn4Lin)
//        if (v.getId() == R.id.btn_option1 || v.getId() == R.id.btn_option2 || v.getId() == R.id.btn_option3 || v.getId() == R.id.btn_option4) {
//
//            // User clicked any 1 button from 4 buttons
//            isClicked = true;
//            // Reset the timer for the next question
//            resetTimeForNextQuestion();
//
//
//            // Ensure the clicked view is a LinearLayout
//            if (v instanceof AppCompatButton selectedLinearLayout) {
//
//                // Get the text of the TextView
//                String selectedText = selectedLinearLayout.getText().toString();
//
//            }
//
//            updateQuizNum();
//
//            // Wait few seconds for the next question to be displayed
//            waitAfterEachQuestion();
//        }
//
//    }
//
//    public void pauseTheQuiz() {
//        binding.imgPauseResume.setImageResource(R.drawable.play_circle_24);
//        if (isCountOn && countTimerForSingleQuestion != null) {
//
//            // Cancel the timer if it's running
//            countTimerForSingleQuestion.cancel();
//            isCountOn = false; // Update the flag to indicate the timer is not running
//            isPaused = true; // Update the flag to indicate the quiz is not in pause state
//
//        }
//    }
//
//    public void displayPauseDialog() {
//        dialog = Helper.displayDialog(
//                context,
//                SweetAlertDialog.WARNING_TYPE,
//                "Quiz Paused",
//                "You can resume the quiz whenever you're ready!",
//                "Resume",
//                "Exit",
//                dialogCallback,
//                Helper.getDialogPurposeText(Helper.DialogPurpose.PAUSE)
//
//        );
//
//        // Reset the flag when the dialog is dismissed
//        dialog.setOnDismissListener(dialogInterface -> isPauseDialogShown = false);
//
//        dialog.show();
//        isPausedByUser = true;
//        isPauseDialogShown = true;
//    }
//
//    public void resumeTheQuiz() {
//
//        binding.imgPauseResume.setImageResource(R.drawable.rounded_pause_circle_24);
//
//        // Record the time when the pause ended
//        // When the quiz is resume or start again
//        long resumeTime = System.currentTimeMillis();
//
//        // Calculate and add the duration of this pause to the total paused time
//        totalPausedDuration += resumeTime - pausedTime;
//
//        // Restart the timer
//        setCountTimerForSingleQuestion();
//
//    }
//
//    @SuppressLint("DefaultLocale")
//    private void updateQuizNum(){
//        currentQuiz++;
//        currentQuestionsProgress++;
//        binding.txtTotalQuestions.setText(format("%d/%d", currentQuiz, totalQuiz));
//    }
//
//    @SuppressLint("DefaultLocale")
//    private void loadQuizData(List<Quiz> quizDataLists) {
//        totalQuiz = quizDataLists.size();
//        nextQuestion();
//        setCountTimerForSingleQuestion();
//        binding.txtTotalQuestions.setText(format("%d/%d", currentQuiz, totalQuiz));
//    }
//
//    public void checkCanGo(){
//        if (quizIndex == (totalQuiz - 1)){
//            // Cancel the timer
//            countTimerForSingleQuestion.cancel();
//            // This is end of the quiz. Now prepare to go to the result page
//            quizFinished();
//
//        }else{
//            // Question still left to display
//
//            quizIndex++;
//
//            // Reset the button states
//            enableOrDisableButton(true);
//
//            nextQuestion();
//            resetProgressTime();
//            setCountTimerForSingleQuestion();
//            binding.btnOption1.setVisibility(VISIBLE);
//        }
//    }
//
//    public void nextQuestion(){
//
//        isClicked = false;
//
//        binding.txtHint.setVisibility(View.GONE);
//
//        // Get the correct answer number
//        byte currentAnswer = quizzes.get(quizIndex).getAnswer();
//
//        // Get the current answer text according to the answer number
//        switch (currentAnswer){
//            case 1:
//                correctAnswerText = quizzes.get(quizIndex).getOptionA();
//                break;
//            default:
//                correctAnswerText = quizzes.get(quizIndex).getOptionD();
//                break;
//        }
//
//        // Create a List of possible answers
//        List<String> possibleAnswers = new ArrayList<>();
//        possibleAnswers.add(quizzes.get(quizIndex).getOptionA());
//
//        // Shuffle the possible answer
//        Collections.shuffle(possibleAnswers);
//
//        // Set the current question to the textview UI
//        binding.txtCurrentQuestion.setText(quizzes.get(quizIndex).getQuestion());
//
//        // Set possible answer text to the button UI
//        binding.btnOption1.setText(possibleAnswers.get(0));
//        binding.btnOption2.setText(possibleAnswers.get(1));
//        binding.btnOption3.setText(possibleAnswers.get(2));
//        binding.btnOption4.setText(possibleAnswers.get(3));
//
//
//        // Start time to track how long it take the user to finish the quiz
//        if (startTime == 0) { // Set only once
//            startTime = System.currentTimeMillis();
//        }
//
//    }
//
//    public void waitAfterEachQuestion() {
//
//        new CountDownTimer(1000, 1000) {
//
//            public void onTick(long millisUntilFinished) { }
//            public void onFinish() {
//
//                checkCanGo();
//            }
//        }.start();
//
//    }
//
//    public void resetProgressTime(){
//        currentTimeProgress = 0;
//
//        // Reset the timer here for next question
//        mTimerLeftInMillis = mFixedTimerForCurrentTime;
//    }
//
//    public void displayExitDialog() {
//        dialog = Helper.displayDialog(
//                context,
//                SweetAlertDialog.WARNING_TYPE,
//                "Exit from Quiz!!!",
//                "Are you sure you want to exit from quiz?",
//                "Yes",
//                null,
//                dialogCallback,
//                Helper.getDialogPurposeText(Helper.DialogPurpose.EXIT)
//        );
//
//        // Reset the flag when the dialog is dismissed
//        dialog.setOnDismissListener(dialogInterface -> isPauseDialogShown = false);
//
//        dialog.show();
//        isPausedByUser = true;
//        isPauseDialogShown = true;
//    }
//
//    public void setCountTimerForSingleQuestion(){
//        countTimerForSingleQuestion = new CountDownTimer(mTimerLeftInMillis,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                mTimerLeftInMillis = millisUntilFinished;
//                binding.txtTimer.setText(valueOf(mTimerLeftInMillis / 1000));
//            }
//            @Override
//            public void onFinish() {
//
//                if (isClicked){
//                    isCountOn = false;
//                    updateQuizNum();
//                    resetProgressTime();
//                    checkCanGo();
//                }else{
//                    // Ask for extend timer
//                    displayBuyTimer(context,"Timer");
//                }
//
//                // Reset isClicked flag to false
//                isClicked = false;
//
//            }
//        }.start();
//
//        isCountOn = true;  // Mark the timer as running
//        isPaused = false;  // Reset the paused state
//    }
//
//    private void timerExtend(boolean instantUse){
//        if (!instantUse){
//            userViewModel.updateTimerFeature(1, false);
//        }
//        mTimerLeftInMillis += 10000;
//        startNewTimer();  // Restart the timer with the updated time
//    }
//
//    private void displayBuyTimer(Context context, String frm) {
//
//        pauseTheQuiz();
//
//        timerFeatureDialog = new Dialog(context);
//
//        // Extend timer directly
//        bindingDialog.btnCustomExtendTimer.setOnClickListener(v -> {
//            timerExtend(false);
//            //resumeTheQuiz();
//            timerFeatureDialog.dismiss();
//        });
//
//
//        // Close dialog on clicking the close button
//        bindingDialog.imgCloseDialog.setOnClickListener(v -> {
//            resumeTheQuiz();
//            timerFeatureDialog.dismiss();
//        });
//
//        // Buy with coins
//        bindingDialog.layerBtnBuyWithCoin.setOnClickListener(v -> {
//
//            // This part is common for every action
//            if (!(coin >= itemPrice)){
//                bindingDialog.txtCustomErrorMessage.setVisibility(View.VISIBLE);
//                return;
//            }
//
//            // Buy features with coins
//            switch (frm) {
//                case "Timer" -> timerExtend(true);
//                case "Hint" -> userViewModel.updateHints(1, true);
//                case "Fifty" -> userViewModel.updateFiftyFifty(1, true);
//            }
//
//            userViewModel.updateCoins(itemPrice, false);
//            timerFeatureDialog.dismiss();
//
//            resumeTheQuiz();
//
//        });
//
//        // Watch video for extra time
//        bindingDialog.layerBtnWatchVideo.setOnClickListener(v -> {
//            Toast.makeText(context, "Ads loading", Toast.LENGTH_SHORT).show();
//            callAdController(frm);
//            timerFeatureDialog.dismiss();
//        });
//
//        // Go to the next question
//        bindingDialog.btnCustomGoToNextQuestion.setOnClickListener(v -> {
//            binding.imgPauseResume.setImageResource(R.drawable.rounded_pause_circle_24);
//            updateQuizNum();
//            resetProgressTime();
//            checkCanGo();
//            resumeTheQuiz(); // maybe remove it from here
//            timerFeatureDialog.dismiss();
//        });
//    }
//
//    private void callAdController(String frm){
//        resumeTheQuiz();
//        Toast.makeText(QuizPlayActivity.this, "Completed", Toast.LENGTH_SHORT).show();
//
//        switch (frm) {
//            case "Hint" -> userViewModel.updateHints(1, true);
//            case "Timer" -> timerExtend(false);
//            case "Fifty" -> userViewModel.updateFiftyFifty(1, true);
//        }
//    }
//
//    private void startNewTimer() {
//        if (countTimerForSingleQuestion != null) {
//            countTimerForSingleQuestion.cancel();  // Cancel the old timer
//            countTimerForSingleQuestion = null;
//        }
//        setCountTimerForSingleQuestion();  // Start a new timer with the updated time
//    }
//
//    private void resetTimeForNextQuestion(){
//        if (isCountOn) countTimerForSingleQuestion.cancel();
//        binding.txtHint.setVisibility(View.GONE);
//        mTimerLeftInMillis = mFixedTimerForCurrentTime;
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (isPaused && mTimerLeftInMillis > 0) {
//            // Resume the timer from where it was paused
//            setCountTimerForSingleQuestion();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Check if the app is resuming from a pause state
//        if (isPausedByUser) {
//            isPausedByUser = false; // Reset the flag
//            // Pause the quiz again
//            pauseTheQuiz();
//
//            // Display the pause dialog if it's not already displayed - the alert dialog can be either for exit or pause
//            if (!isPauseDialogShown){
//                displayPauseDialog();
//            }
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Set the flag to indicate the app was paused and paused by the user (not come from another activity)
//        isPausedByUser = true;
//        // Pause the quiz again
//        if (!isPaused) {
//            pausedTime = System.currentTimeMillis();
//            // If not already paused, pause the quiz
//            pauseTheQuiz();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//            dialog = null;
//        }
//    }
//
//}