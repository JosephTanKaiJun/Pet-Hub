package com.example.pethub.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.example.pethub.R;
import com.example.pethub.userauthentication.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoImage;
    private TextView appNameText;
    private TextView taglineText;
    private ImageView pawPrint1;
    private ImageView pawPrint2;
    private ImageView pawPrint3;
    private ProgressBar loadingSpinner;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        logoImage = findViewById(R.id.splash_logo);
        appNameText = findViewById(R.id.app_name_text);
        taglineText = findViewById(R.id.tagline_text);
        pawPrint1 = findViewById(R.id.paw_print1);
        pawPrint2 = findViewById(R.id.paw_print2);
        pawPrint3 = findViewById(R.id.paw_print3);
        loadingSpinner = findViewById(R.id.loading_spinner);
        rootView = findViewById(android.R.id.content);

        // Load animations
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        Animation textFadeIn = AnimationUtils.loadAnimation(this, R.anim.text_fade_in);
        Animation pawPrintAnim1 = AnimationUtils.loadAnimation(this, R.anim.paw_print_animation);
        Animation pawPrintAnim2 = AnimationUtils.loadAnimation(this, R.anim.paw_print_animation);
        Animation pawPrintAnim3 = AnimationUtils.loadAnimation(this, R.anim.paw_print_animation);
        Animation spinnerFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Set animation listeners
        logoAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                logoImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start text animations after logo animation completes
                appNameText.startAnimation(textFadeIn);
                taglineText.startAnimation(textFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        textFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                appNameText.setVisibility(View.VISIBLE);
                taglineText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                pawPrint1.setVisibility(View.VISIBLE);
                pawPrint2.setVisibility(View.VISIBLE);
                pawPrint3.setVisibility(View.VISIBLE);
                // Start paw print animations after text animations
                pawPrint1.startAnimation(pawPrintAnim1);

                // Delay the second paw print slightly
                new Handler().postDelayed(() -> {
                    pawPrint2.startAnimation(pawPrintAnim2);
                }, 150);

                // Delay the third paw print a bit more
                new Handler().postDelayed(() -> {
                    pawPrint3.startAnimation(pawPrintAnim3);
                }, 300);

                // Show loading spinner
                loadingSpinner.startAnimation(spinnerFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Set visibility and start animations
        logoImage.setAlpha(1f);
        logoImage.startAnimation(logoAnim);

        // Schedule transition to login activity
        new Handler().postDelayed(() -> {
            // Create a zoom out animation for transition
            Animation zoomFadeOut = AnimationUtils.loadAnimation(this, R.anim.zoom_fade_out);
            rootView.startAnimation(zoomFadeOut);

            zoomFadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Create an activity transition
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                            SplashActivity.this, R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent, options.toBundle());
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }, 3000); // Total duration: 3 seconds
    }
}