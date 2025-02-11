package com.javaprogramming.quiz.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.database.SaveData;
import com.javaprogramming.quiz.databinding.ActivitySettingBinding;
import com.javaprogramming.quiz.utilities.Helper;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean isMuted = SaveData.getIsMuted(SettingActivity.this);
        Log.d("iswork", "onCreate: "+isMuted);
        binding.switchBtnAudio.setChecked(!isMuted);


        binding.switchBtnAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean newState = !isChecked; // Invert the state before saving
            SaveData.saveIsMuted(newState, SettingActivity.this);
        });


        binding.imgBack.setOnClickListener(v -> Helper.navigateToActivity(SettingActivity.this, HomeActivity.class, null , true));

        binding.clickContactID.setOnClickListener(v -> gotoHelp("contact"));
        binding.clickAboutID.setOnClickListener(v -> gotoHelp("about"));
        binding.clickPrivacyID.setOnClickListener(v -> gotoHelp("privacy"));
        binding.clickTermConditionID.setOnClickListener(v -> gotoHelp("term"));
        binding.clickRateID.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="+ SettingActivity.this.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        binding.clickShareID.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String shareMessage = "🚀 Enhance your Java skills with this amazing quiz app! 🧠💡\n\n"
                        + "Challenge yourself with fun quizzes across different categories and learn exciting facts about Java. 📚✨\n\n"
                        + "Download now and start your journey to becoming a Java pro! 🔥👇\n\n";

                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share Quiz Master Apps"));
            } catch(Exception ignored) {

            }
        });

        backHandler();


    }
    private void gotoHelp(String data){
        Bundle bundle = new Bundle();
        bundle.putString("data",data);
        Helper.navigateToActivity(SettingActivity.this, HelpActivity.class, bundle , false);
    }

    private void backHandler(){
        // Register back-press callback
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Helper.navigateToActivity(SettingActivity.this, HomeActivity.class, null, true);
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }


}