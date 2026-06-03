package com.project.veloxseguros2.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.project.veloxseguros2.MainActivity;
import com.project.veloxseguros2.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText txtCorreo, txtTelefonoLog;
    MaterialButton btnLogin;
    TextView tvRegistrate, tvOlvide;

    // ⚠️ CAMBIAR por la IP de tu computadora donde corre Flask
    String URL_LOGIN = "http://192.168.20.130:5000/Clientes/Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtCorreo       = findViewById(R.id.txtCorreo);
        // Apunta al segundo cuadro de texto (el que antes se usaba como password)
        txtTelefonoLog  = findViewById(R.id.txtPassword);
        btnLogin        = findViewById(R.id.btnLogin);
        tvRegistrate    = findViewById(R.id.tvRegistrate);
        tvOlvide        = findViewById(R.id.tvOlvide);

        btnLogin.setOnClickListener(v -> login());
        tvRegistrate.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        tvOlvide.setOnClickListener(v ->
                Toast.makeText(this, "Función próxima", Toast.LENGTH_SHORT).show());
    }

    // Login con Volley — POST /Clientes/Login
    private void login() {
        String email    = txtCorreo.getText().toString().trim();
        String telefono = txtTelefonoLog.getText().toString().trim();

        if (email.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject datos = new JSONObject();
        try {
            // Mandamos las llaves exactas que pide find_one en Flask
            datos.put("Email",    email);
            datos.put("Telefono", telefono);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, URL_LOGIN, datos,
                response -> {
                    try {
                        String idCliente = response.getString("_idClientes");
                        String nombre    = response.getString("Nombre");

                        SharedPreferences prefs = getSharedPreferences("velox_prefs", MODE_PRIVATE);
                        prefs.edit()
                                .putString("_idClientes",    idCliente)
                                .putString("nombre",         nombre)
                                .putBoolean("sesion_activa", true)
                                .apply();

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(this, "Email o teléfono incorrectos", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error de conexión — verifica la IP o backend", Toast.LENGTH_LONG).show();
                    }
                }
        );
        Volley.newRequestQueue(this).add(request);
    }
}