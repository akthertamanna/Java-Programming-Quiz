package com.javaprogramming.quiz.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.activity.QuizLevelActivity;
import com.javaprogramming.quiz.database.SaveData;
import com.javaprogramming.quiz.viewmodel.UserViewModel;
import com.javaprogramming.quiz.databinding.BuyItemLayoutBinding;
import com.javaprogramming.quiz.interfaces.DialogCallback;
import com.javaprogramming.quiz.model.User;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Helper {
    public static final String DATABASE_NAME = "QuizDatabase";
    public static final String QUIZ_HISTORY_TABLE_NAME = "QuizHistory";
    public static final String SEEN_QUESTION_TABLE_NAME = "SeenQuestion";
    public static final String USER_TABLE_NAME = "UserData";
    public static final String CATEGORY_TABLE_NAME = "Category";
    public static final String QUIZ_QUESTIONS_TABLE_NAME = "QuizQuestion";
    public static final String QUIZ_HISTORY_QUESTION_TABLE_NAME = "QuizHistoryQuestion";
    public static String key_clicked_category_title = "clickedCategoryTitle";
    public static String key_CategoryID = "categoryID";
    public static String key_Quiz_Level = "QuizLevel";

    public static boolean isUpdateChecked = false;
    public static String firstTime = "firstTime";
    public static String quizDataUrl = "quizdata.json";

    public static final byte EASY_MODE = 1;
    public static final byte INTERMEDIATE_MODE = 2;
    public static final byte ADVANCE_MODE = 3;

    public static String lastActiveFragment = "Home";


    public static final String PREFS_NAME = "QuizPrefs";
    public static final String KEY_IS_SAVED = "isSaved";
    public static final String KEY_SAVED_VERSION = "savedVersion";
    public static final String key_Current_Level ="currentLevel";
    public static String key_Is_Sets_New = "Is_Sets_New";
    public static String key_Is_Muted = "Is_Muted";


    // Get App Version Code
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;  // Works for API 28 and below
        } catch (PackageManager.NameNotFoundException ignored) {
            return -1;  // Return -1 if there's an error
        }
    }

    public static void showMessageSnackbar(View view, String message) {
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            // If view is null, fallback to a Toast
            Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showScreenSizeToast(Context context) {
        Configuration config = context.getResources().getConfiguration();
        int screenLayout = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        String screenSize = switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL -> "Small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL -> "Normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE -> "Large";
            case Configuration.SCREENLAYOUT_SIZE_XLARGE -> "Extra Large";
            default -> "Undefined";
        };

        Toast.makeText(context, "Screen Size: " + screenSize, Toast.LENGTH_SHORT).show();
    }

    public static String quizLevelConvertToText(byte level){
        if(level == 1){
            return "Beginner";
        }else if(level == 2){
            return "Intermediate";
        }else{
            return "Advance";
        }
    }

    public enum DialogPurpose {
        EXIT,
        PAUSE,
        HOME
    }


    public static boolean isLandscapeMode(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        // The device is in landscape mode
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static String getDialogPurposeText(DialogPurpose purpose){
        return purpose.name();
    }

    public static int getNumberOfQuestionShouldDisplay(byte level){

        if(level == EASY_MODE){
            return 10;
        }else if(level == INTERMEDIATE_MODE){
            return 10;
        }else{
            return 15;
        }

    }

    public static String getJsonData(String fileName, Context context){
        String json = null;
        try {
            InputStream in = context.getAssets().open(fileName);
            int size = in.available();
            byte[] bbuffer = new byte[size];
            in.read(bbuffer);
            in.close();
            json=new String(bbuffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
        return json;
    }

    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

//    public static void quizLevel(int beginnerLvl, int intermediateLvl, int advanceLvl, Context context){
//
//        Dialog dialog;
//        dialog = new Dialog(context);
//        dialog.setContentView(R.layout.quizlevel);
//
//        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        Button btnBeginner= dialog.findViewById(R.id.btnBeginner);
//        Button btnIntermediate = dialog.findViewById(R.id.btnIntermediate);
//        Button btnAdvance = dialog.findViewById(R.id.btnAdvance);
//        ImageView btnClose = dialog.findViewById(R.id.btnClose);
//
//        // need to change the logic of the quiz level here
//        // It should be show question size from the setting according to the user selection
//
//        if (beginnerLvl <=9){
//            btnBeginner.setVisibility(View.GONE);
//        }else{
//            btnBeginner.setVisibility(View.VISIBLE);
//        }
//
//        if (intermediateLvl<=9){
//            btnIntermediate.setVisibility(View.GONE);
//        }else{
//            btnIntermediate.setVisibility(View.VISIBLE);
//        }
//
//        if (advanceLvl <=9){
//            btnAdvance.setVisibility(View.GONE);
//        }else{
//            btnAdvance.setVisibility(View.VISIBLE);
//        }
//
//        btnClose.setOnClickListener(view -> dialog.dismiss());
//
//        btnBeginner.setOnClickListener(view -> {
//            SaveData.saveQuizLevel(Helper.EASY_MODE,context);
//            goToQuiz(context,dialog);
//        });
//
//        btnIntermediate.setOnClickListener(view -> {
//            SaveData.saveQuizLevel(Helper.INTERMEDIATE_MODE,context);
//            goToQuiz(context,dialog);
//        });
//
//        btnAdvance.setOnClickListener(view -> {
//            SaveData.saveQuizLevel(Helper.ADVANCE_MODE,context);
//            goToQuiz(context,dialog);
//        });
//
//        Dialog noDialog;
//        noDialog = new Dialog(context);
//        noDialog.setContentView(R.layout.noquiz);
//        Objects.requireNonNull(noDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ImageView btnCloseNo = noDialog.findViewById(R.id.btnCloseno);
//
//        btnCloseNo.setOnClickListener(view -> noDialog.dismiss());
//
//        if (advanceLvl<=9 && beginnerLvl<=9 && intermediateLvl<=9){
//            noDialog.show();
//        }else{
//            dialog.show();
//        }
//    }

    public static void goToQuiz(Context mContext , Dialog dialog){

        Intent quizLevel = new Intent(mContext, QuizLevelActivity.class);
        dialog.dismiss();
        mContext.startActivity(quizLevel);
        ((Activity) mContext).finish();
    }

//    public static void quizDialogStart(Context context){
//
//        Dialog dialog;
//        dialog = new Dialog(context);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        // Set custom layout
//        dialog.setContentView(R.layout.dialog_quiz_start);
//
//
//
//        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ImageButton btnClose = dialog.findViewById(R.id.btn_close);
//        ImageButton btnPlay = dialog.findViewById(R.id.btn_play);
//
//        // need to change the logic of the quiz level here
//        // It should be show question size from the setting according to the user selection
//
//
//
//        btnClose.setOnClickListener(view -> dialog.dismiss());
//
//        Dialog noDialog;
//        noDialog = new Dialog(context);
//        noDialog.setContentView(R.layout.noquiz);
//        Objects.requireNonNull(noDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ImageView btnCloseNo = noDialog.findViewById(R.id.btnCloseno);
//
//        btnCloseNo.setOnClickListener(view -> noDialog.dismiss());
//
//        dialog.show();
//
//    }

    public static SweetAlertDialog displayDialog(Context context, int alertType, String title, String contentText, String confirmText, String cancelText, DialogCallback callback, String purpose){

        // Create and display the exit dialog
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, alertType);
        // Prevent the dialog from being dismissed when clicking outside
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        // Prevent to cancel the dialog by press back button
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(contentText);

        cancelText = cancelText == null ? "No" : cancelText;

        sweetAlertDialog.setCancelText(cancelText);
        sweetAlertDialog.setCancelClickListener(sweetAlertDialogVar -> {
            sweetAlertDialogVar.dismiss();  // Dismiss the dialog
            if (callback != null) {
                // If the callback is not null, invoke onCancel() method
                callback.onCancel(purpose);  // Custom behavior for cancel action
            }
        });
        sweetAlertDialog.setConfirmText(confirmText);
        sweetAlertDialog.setConfirmClickListener(sweetAlertDialogVar -> {
            sweetAlertDialogVar.dismiss();  // Dismiss the dialog
            if (callback != null) {
                // If the callback is not null, invoke onConfirm() method
                callback.onConfirm(purpose);  // Custom behavior for confirm action
            }
        });

        // Return the SweetAlertDialog instance
        return sweetAlertDialog;
    }

    public static void navigateToActivity(Context context, Class<?> targetActivity, Bundle data, boolean shouldFinishCurrentActivity) {
        Intent intent = new Intent(context, targetActivity);

        // If data is provided, add it as extras to the intent
        if (data != null) {
            intent.putExtras(data);
        }

        context.startActivity(intent); // Start the target activity

        // Check if the context is an instance of Activity before calling finish
        if (shouldFinishCurrentActivity && context instanceof Activity) {
            ((Activity) context).finish(); // Finish the current activity if flag is true
        }

    }


    public static void displayBuyTimer(Context context, String frm, User user, UserViewModel userViewModel) {

        if (user == null || userViewModel == null) {
            return;  // Ensure neither is null
        }

            // Inflate the custom layout using ViewBinding
        LayoutInflater inflater = LayoutInflater.from(context);
        BuyItemLayoutBinding bindingDialog = BuyItemLayoutBinding.inflate(inflater);

        int itemPrice;


        String title = context.getString(R.string.timer_buy_title_opt);
        String desc = context.getString(R.string.common_buy_description);

        if (user.getTimerFeature_U() <=0){
            title = context.getString(R.string.timer_buy_title);
        }

        if (frm.equals("Timer")){
            desc = context.getString(R.string.timer_extend_description);
        }
        if (frm.equals("Fifty")) {
            itemPrice = 25;

            if (user.getFiftyFifty_U() <=0){
                title = context.getString(R.string.fifty_fifty_buy_title);
            }else{
                title = context.getString(R.string.fifty_fifty_buy_title_opt);
            }

        } else if (frm.equals("Hint")) {
            itemPrice = 25;
            if (user.getHints_U() <=0){
                title = context.getString(R.string.hint_buy_title);
            }else{
                title = context.getString(R.string.hint_buy_title_opt);
            }
        } else {
            itemPrice = 25;
        }

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return; // Avoid crashes if the context is invalid or the activity is finishing
        }


        // Initialize the dialog and set its content view using binding
        Dialog timerFeatureDialog = new Dialog(context);
        timerFeatureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timerFeatureDialog.setContentView(bindingDialog.getRoot()); // Use ViewBinding root view
        timerFeatureDialog.setCancelable(false);
        timerFeatureDialog.setCanceledOnTouchOutside(false);

        // Set dialog window attributes
        if (timerFeatureDialog.getWindow() != null) {
            Window window = timerFeatureDialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.halfTransparent)));
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

            bindingDialog.imgVideoItemLogo.setImageResource(R.drawable.timer_sand);
            bindingDialog.imgBuyItemLogo.setImageResource(R.drawable.timer_sand);

        }else if (frm.equals("Hint")){
            bindingDialog.imgVideoItemLogo.setImageResource(R.drawable.baseline_lightbulb_24);
            bindingDialog.imgBuyItemLogo.setImageResource(R.drawable.baseline_lightbulb_24);
        }else{
            bindingDialog.imgVideoItemLogo.setImageResource(R.drawable.fifty_fifty);
            bindingDialog.imgBuyItemLogo.setImageResource(R.drawable.fifty_fifty);
        }

        bindingDialog.txtPrice.setText(String.valueOf(itemPrice));
        bindingDialog.txtCoin.setText(String.valueOf(user.getCoins_U()));


        // Close dialog on clicking the close button
        bindingDialog.imgCloseDialog.setOnClickListener(v -> {

            timerFeatureDialog.dismiss();
        });

        // Buy with coins
        bindingDialog.layerBtnBuyWithCoin.setOnClickListener(v -> {

            // This part is common for every action
            if (!(user.getCoins_U() >= itemPrice)){
                bindingDialog.txtCustomErrorMessage.setVisibility(View.VISIBLE);
                return;
            }

            // Buy timer with coins
            if (frm.equals("Timer")){
                if (user.getCoins_U()>= itemPrice){
                    userViewModel.updateTimerFeature(1, true);
//                    timerExtend();
                }
            }else if(frm.equals("Hint")){
                userViewModel.updateHints(1, true);
            }else if(frm.equals("Fifty")){
                userViewModel.updateFiftyFifty(1, true);
            }

            userViewModel.updateCoins(itemPrice, false);
            timerFeatureDialog.dismiss();

        });

        // Watch video for extra time
        bindingDialog.layerBtnWatchVideo.setOnClickListener(v -> {
            Toast.makeText(context, "Ads loading", Toast.LENGTH_SHORT).show();
            callAdController(context, frm, userViewModel);
            timerFeatureDialog.dismiss();
        });


        // Show dialog only if it's not already displayed
        if (!timerFeatureDialog.isShowing()) {
            timerFeatureDialog.show();
        }
    }

    private static void callAdController(Context context, String frm, UserViewModel userViewModel) {
        Toast.makeText(context, "Completed", Toast.LENGTH_SHORT).show();
        switch (frm) {
            case "Hint" -> userViewModel.updateHints(1, true);
            case "Timer" -> userViewModel.updateTimerFeature(1, true);
            case "Fifty" -> userViewModel.updateFiftyFifty(1, true);
        }
    }


}
