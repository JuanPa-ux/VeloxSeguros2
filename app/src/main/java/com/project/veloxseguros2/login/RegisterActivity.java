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
import com.project.veloxseguros2.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText txtNombre, txtCorreo, txtTelefono, txtDireccion;
    MaterialButton btnRegistrar;
    TextView tvYaTengo;

    // ⚠️ CAMBIAR por la IP de tu computadora
    String URL_REGISTRO = "http://192.168.20.130:5000/Clientes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtNombre    = findViewById(R.id.txtNombre);
        txtCorreo    = findViewById(R.id.txtCorreoReg);
        txtTelefono  = findViewById(R.id.txtTelefono);
        txtDireccion = findViewById(R.id.txtDireccion);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvYaTengo    = findViewById(R.id.tvYaTengo);

        btnRegistrar.setOnClickListener(v -> registrar());
        tvYaTengo.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registrar() {
        String nombre    = txtNombre.getText().toString().trim();
        String email     = txtCorreo.getText().toString().trim();
        String telefono  = txtTelefono.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Llena los campos obligatorios (Nombre, Email, Teléfono)", Toast.LENGTH_SHORT).show();
            return;
        }

        String idCliente = "CLI-" + System.currentTimeMillis();

        JSONObject datos = new JSONObject();
        try {
            datos.put("_idClientes", idCliente);
            datos.put("Nombre",      nombre);
            datos.put("Email",       email);
            datos.put("Telefono",    telefono);
            datos.put("Direccion",   direccion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, URL_REGISTRO, datos,
                response -> {
                    SharedPreferences prefs = getSharedPreferences("velox_prefs", MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean("vio_onboarding", true)
                            .apply();

                    Toast.makeText(this, "Cuenta creada. Ahora inicia sesión", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                        Toast.makeText(this, "Email inválido, debe contener '@' y '.'", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al registrar — verifica la IP o backend", Toast.LENGTH_LONG).show();
                    }
                }
        );
        Volley.newRequestQueue(this).add(request);
    }
}