package com.javaprogramming.quiz.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.javaprogramming.quiz.databinding.ActivityHomelllBinding;
import com.javaprogramming.quiz.interfaces.DialogCallback;
import com.javaprogramming.quiz.utilities.Helper;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    public ActivityHomelllBinding activityBinding;
    private Fragment activeFragment = null;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    private static final String HomeFragmentTag = "HomeFragment";
    private static final String CategoriesFragmentTag = "CategoriesFragment";
    private static final String QuizHistoryFragmentTag = "QuizHistoryFragment";
    private static final String AboutFragmentTag = "AboutFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityHomelllBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    // handle callback
                    if (result.getResultCode() != RESULT_OK) {
                        // Update is canceled or fails,
                        // Request to start the update again.
                        Toast.makeText(getApplicationContext(), "Update failed! Try again", Toast.LENGTH_SHORT).show();
                        checkInternetAndAppUpdate();

                    }else{
                        Toast.makeText(getApplicationContext(), "Update successfully done", Toast.LENGTH_SHORT).show();
                        // Update successfully done
                        Helper.isUpdateChecked = true;
                    }

                });


        registerBackPressedCallback();

        checkInternetAndAppUpdate();

    }

    private void checkInternetAndAppUpdate(){
        // Internet is not connected
        if (!Helper.isNetworkConnected(this)) {
            // Internet is not connected, don't check the app update is available or not
            Helper.isUpdateChecked = true;
        }else{
            // Internet is connected, check the update now
            // If the update already checked, don't check again
            if (!Helper.isUpdateChecked){
                // Check the app update is available or not as it's not checked already
                checkAppUpdate();
            }
        }
    }

    private void checkAppUpdate() {

        // Initialize the AppUpdateManager
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
            } else {
                // Update is not available, load data for MaiaActivity
                Helper.isUpdateChecked = true;
            }
        });
    }

    private void showProgressBar(boolean show) {
        if (show) {
            activityBinding.progressBarOverlay.setVisibility(View.VISIBLE);
        } else {
            activityBinding.progressBarOverlay.setVisibility(View.GONE);
        }
    }


    private void registerBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                SweetAlertDialog dialog = Helper.displayDialog(
                        MainActivity.this,
                        SweetAlertDialog.WARNING_TYPE,
                        "Exit?",
                        "Are you sure you want to exit?",
                        "Exit",
                        "No",
                        new DialogCallback() {
                            @Override
                            public void onConfirm(String purpose) {
                                finishAffinity(); // Exit the app
                            }

                            @Override
                            public void onCancel(String purpose) {
                                // Do nothing on cancel
                            }
                        },
                        Helper.getDialogPurposeText(Helper.DialogPurpose.PAUSE)
                );
                dialog.show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showProgressBar(false);
    }
}
