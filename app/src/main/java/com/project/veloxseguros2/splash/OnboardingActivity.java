package com.project.veloxseguros2.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.project.veloxseguros2.MainActivity;
import com.project.veloxseguros2.R;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    MaterialButton btnSiguiente;
    TextView tvSaltar;

    // Referencias a los dots
    View dot0, dot1, dot2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager    = findViewById(R.id.viewPager);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        tvSaltar     = findViewById(R.id.tvSaltar);
        dot0         = findViewById(R.id.dot0);
        dot1         = findViewById(R.id.dot1);
        dot2         = findViewById(R.id.dot2);

        OnboardingAdapter adapter = new OnboardingAdapter();
        viewPager.setAdapter(adapter);

        // Estado inicial: primer dot activo
        actualizarDots(0);

        btnSiguiente.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 2) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            } else {
                irAlMain();
            }
        });

        tvSaltar.setOnClickListener(v -> irAlMain());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                actualizarDots(position);
                btnSiguiente.setText(position == 2 ? "Empezar" : "Siguiente");
            }
        });
    }

    // ==========================================
    // Actualizar indicador de dots
    // CORRECCIÓN: ContextCompat.getColor() en lugar de getResources().getColor()
    //             para evitar crash/deprecation en API 23+
    // ==========================================

    private void actualizarDots(int posicion) {
        int colorActivo   = ContextCompat.getColor(this, R.color.colorPrimary);
        int colorInactivo = ContextCompat.getColor(this, R.color.colorBorder);

        // CORRECCIÓN: setBackgroundResource mantiene la forma circular del drawable
        dot0.setBackgroundResource(posicion == 0 ? R.drawable.dot_activo : R.drawable.dot_inactivo);
        dot1.setBackgroundResource(posicion == 1 ? R.drawable.dot_activo : R.drawable.dot_inactivo);
        dot2.setBackgroundResource(posicion == 2 ? R.drawable.dot_activo : R.drawable.dot_inactivo);
    }

    // ==========================================
    // Ir al Main y guardar que ya vio onboarding
    // ==========================================

    private void irAlMain() {
        SharedPreferences prefs = getSharedPreferences("velox_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("vio_onboarding", true).apply();

        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
