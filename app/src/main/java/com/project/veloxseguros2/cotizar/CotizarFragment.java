package com.project.veloxseguros2.cotizar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.card.MaterialCardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.project.veloxseguros2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CotizarFragment extends Fragment {

    Spinner spinnerModelo, spinnerAnio, spinnerColor;
    MaterialCardView cardPlanBasico, cardPlanTotal;
    TextView tvMontoBasico, tvMontoTotal;
    MaterialButton btnContratar;

    String planSeleccionado = "Total";
    int posicionModelo = 0;

    // ⚠️ CAMBIAR por la IP de tu computadora
    String URL_POLIZAS   = "http://192.168.20.130:5000/Poliza";
    String URL_VEHICULOS = "http://192.168.20.130:5000/Vehiculo";

    int[] preciosBasico = {890, 950, 920, 820, 1100, 1200, 1050};
    int[] preciosTotal  = {1450, 1550, 1480, 1320, 1750, 1900, 1680};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cotizar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerModelo  = view.findViewById(R.id.spinnerModelo);
        spinnerAnio    = view.findViewById(R.id.spinnerAnio);
        spinnerColor   = view.findViewById(R.id.spinnerColor);
        cardPlanBasico = view.findViewById(R.id.cardPlanBasico);
        cardPlanTotal  = view.findViewById(R.id.cardPlanTotal);
        tvMontoBasico  = view.findViewById(R.id.tvMontoBasico);
        tvMontoTotal   = view.findViewById(R.id.tvMontoTotal);
        btnContratar   = view.findViewById(R.id.btnContratar);

        configurarSpinners();
        configurarSeleccionPlan();
        btnContratar.setOnClickListener(v -> contratarPoliza(v));
    }

    private void configurarSpinners() {
        String[] modelos = {"Versa", "Kicks", "Sentra", "NP300", "Frontier", "Pathfinder", "Murano"};
        String[] anios   = {"2026", "2025", "2024", "2023", "2022", "2021", "2020"};
        String[] colores = {"Blanco Glaciar", "Negro Onix", "Azul Cobalto", "Rojo Cereza",
                            "Plata Brillante", "Gris Urbano", "Cafe Bronce"};

        spinnerModelo.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, modelos));
        spinnerAnio.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, anios));
        spinnerColor.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, colores));

        spinnerModelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicionModelo = position;
                actualizarPrecios();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void actualizarPrecios() {
        tvMontoBasico.setText("$" + preciosBasico[posicionModelo]);
        tvMontoTotal.setText("$" + preciosTotal[posicionModelo]);
        btnContratar.setText(planSeleccionado.equals("Basico")
                ? "Contratar Plan Básico — $" + preciosBasico[posicionModelo] + "/mes"
                : "Contratar Plan Total — $"  + preciosTotal[posicionModelo]  + "/mes");
    }

    private void configurarSeleccionPlan() {
        // Plan Total activo por defecto — CORRECCIÓN: usar ContextCompat
        cardPlanTotal.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));

        cardPlanBasico.setOnClickListener(v -> {
            planSeleccionado = "Basico";
            cardPlanBasico.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            cardPlanTotal.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.colorBorder));
            actualizarPrecios();
        });

        cardPlanTotal.setOnClickListener(v -> {
            planSeleccionado = "Total";
            cardPlanTotal.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            cardPlanBasico.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.colorBorder));
            actualizarPrecios();
        });
    }

    private void contratarPoliza(View v) {
        String modelo = spinnerModelo.getSelectedItem().toString();
        String anio   = spinnerAnio.getSelectedItem().toString();
        String color  = spinnerColor.getSelectedItem().toString();

        long   ts         = System.currentTimeMillis();
        String idVehiculo = "VEH-" + ts;
        String idPoliza   = "POL-" + ts;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaInicio = sdf.format(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        String fechaVencimiento = sdf.format(cal.getTime());

        // Leer _idClientes guardado en SharedPreferences durante el login
        SharedPreferences prefs = requireActivity().getSharedPreferences("velox_prefs", android.content.Context.MODE_PRIVATE);
        String idCliente = prefs.getString("_idClientes", "CLI-TEMP");

        registrarVehiculo(idVehiculo, idCliente, modelo, anio, color, idPoliza, fechaInicio, fechaVencimiento, v);
    }

    // POST /Vehiculo
    private void registrarVehiculo(String idVehiculo, String idCliente,
                                    String modelo, String anio, String color,
                                    String idPoliza, String fechaInicio,
                                    String fechaVencimiento, View v) {
        JSONObject vehiculoData = new JSONObject();
        try {
            vehiculoData.put("_idVehiculo", idVehiculo);
            vehiculoData.put("_idClientes", idCliente);
            vehiculoData.put("modelo",      "Nissan " + modelo);
            vehiculoData.put("Año",         Integer.parseInt(anio));
            vehiculoData.put("Color",       color);
            vehiculoData.put("Placas",      "PENDIENTE");
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest reqVehiculo = new JsonObjectRequest(
                Request.Method.POST, URL_VEHICULOS, vehiculoData,
                response -> registrarPoliza(idVehiculo, idPoliza, fechaInicio, fechaVencimiento, v),
                error  -> Toast.makeText(requireContext(), "Error al registrar vehículo", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(requireContext()).add(reqVehiculo);
    }

    // POST /Poliza
    private void registrarPoliza(String idVehiculo, String idPoliza,
                                  String fechaInicio, String fechaVencimiento, View v) {
        JSONObject polizaData = new JSONObject();
        try {
            polizaData.put("_idPoliza",        idPoliza);
            polizaData.put("FechaInicio",      fechaInicio);
            polizaData.put("FechaVencimiento", fechaVencimiento);
            polizaData.put("Estado",           "Activa");
            polizaData.put("_idVehiculo",      idVehiculo);
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest reqPoliza = new JsonObjectRequest(
                Request.Method.POST, URL_POLIZAS, polizaData,
                response -> {
                    Toast.makeText(requireContext(), "¡Póliza contratada!", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(v).navigate(R.id.homeFragment);
                },
                error -> Toast.makeText(requireContext(), "Error al crear póliza", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(requireContext()).add(reqPoliza);
    }
}
