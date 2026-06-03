package com.project.veloxseguros2.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.project.veloxseguros2.MainActivity;
import com.project.veloxseguros2.R;
import com.project.veloxseguros2.login.LoginActivity; // ← NUEVO

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View layoutLogo = findViewById(R.id.layoutLogo);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(700);
        fadeIn.setFillAfter(true);
        layoutLogo.startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences("velox_prefs", MODE_PRIVATE);
            boolean vioOnboarding = prefs.getBoolean("vio_onboarding", false);
            boolean sesionActiva  = prefs.getBoolean("sesion_activa",  false); // ← NUEVO

            // ANTES: solo revisaba onboarding → siempre iba a MainActivity
            // AHORA: revisa también si hay sesión iniciada
            Intent destino;
            if (!vioOnboarding) {
                destino = new Intent(this, OnboardingActivity.class);
            } else if (sesionActiva) {
                destino = new Intent(this, MainActivity.class);
            } else {
                destino = new Intent(this, LoginActivity.class); // ← va al login si no hay sesión
            }

            AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
            fadeOut.setDuration(300);
            fadeOut.setFillAfter(true);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation a) {}
                @Override public void onAnimationRepeat(Animation a) {}
                @Override
                public void onAnimationEnd(Animation a) {
                    startActivity(destino);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            });
            layoutLogo.startAnimation(fadeOut);

        }, 2200);
    }
}