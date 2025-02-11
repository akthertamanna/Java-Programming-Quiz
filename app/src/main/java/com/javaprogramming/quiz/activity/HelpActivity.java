package com.javaprogramming.quiz.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.databinding.ActivityHelpBinding;
import com.javaprogramming.quiz.utilities.Helper;

public class HelpActivity extends AppCompatActivity {

    private ActivityHelpBinding activityBinding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityBinding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String dataKey = bundle.getString("data", "none");

            if (dataKey.equals("contact")){
                activityBinding.titleScreen.setText(R.string.contact_us);

                if (Helper.isNetworkConnected(HelpActivity.this)){
                    String link = "https://docs.google.com/forms/d/e/1FAIpQLSfk9HIYTi9BnJbOfxuZFxxU8R3kc2dgMr8svz1XnIFb0qhTQQ/viewform?usp=dialog";
                    activityBinding.webviewHelpID.loadUrl(link);
                    activityBinding.webviewHelpID.getSettings().setJavaScriptEnabled(true);

                    activityBinding.webviewHelpID.setWebViewClient(new WebViewClient());
                    activityBinding.webviewHelpID.setWebViewClient(new WebViewClient(){
                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                            activityBinding.offlineID.setVisibility(View.VISIBLE);
                            activityBinding.emailID.setText(String.format("%s%s", getString(R.string.please_send_email_at), getString(R.string.emailus)));
                            activityBinding.browserID.setVisibility(View.GONE);

                        }
                    });
                    activityBinding.offlineID.setVisibility(View.GONE);
                    activityBinding.browserID.setVisibility(View.VISIBLE);

                }else{
                    activityBinding.offlineID.setVisibility(View.VISIBLE);
                    activityBinding.emailID.setText(String.format("%s%s", getString(R.string.please_send_email_at), getString(R.string.emailus)));
                    activityBinding.browserID.setVisibility(View.GONE);
                }
            }else{
                activityBinding.offlineID.setVisibility(View.GONE);
                activityBinding.browserID.setVisibility(View.VISIBLE);

                String url = "file:///android_asset/";
                switch (dataKey) {
                    case "term":
                        activityBinding.titleScreen.setText(R.string.terms_conditions);
                        activityBinding.webviewHelpID.loadUrl(url + "terms.html");
                        break;
                    case "privacy":
                        activityBinding.titleScreen.setText(R.string.privacy_policy);
                        activityBinding.webviewHelpID.loadUrl(url + "privacy.html");
                        break;
                    case "about":
                        activityBinding.webviewHelpID.loadUrl(url + "about.html");
                        activityBinding.titleScreen.setText(R.string.about_us);
                        break;
                }


                activityBinding.webviewHelpID.getSettings().setJavaScriptEnabled(true);
                activityBinding.webviewHelpID.setWebViewClient(new WebViewClient());
                activityBinding.browserID.setVisibility(View.VISIBLE);

            }

            // Register back-press callback
            OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    finish();
                }
            };

            // Add the callback to the OnBackPressedDispatcher
            getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
        }

        activityBinding.imgBack.setOnClickListener(v -> finish());

    }

}