package com.project.veloxseguros2.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.project.veloxseguros2.R;
import com.project.veloxseguros2.login.LoginActivity; // ← NUEVO

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvNombreUsuario, tvModeloVehiculo, tvPlacasHome;
    private TextView tvVencimientoHome, tvPlanHome, tvEstadoPolizaHome;
    private CardView cardCotizar, cardPagar, cardPolizas, cardSoporte;
    private RecyclerView rvMovimientos;

    // ⚠️ CAMBIAR por la IP de tu computadora
    String URL_POLIZAS   = "http://192.168.20.130:5000/Poliza";
    String URL_VEHICULOS = "http://192.168.20.130:5000/Vehiculo";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarVistas(view);
        cargarNombreUsuario();
        cargarPolizaActiva();
        configurarAccesosRapidos(view);
        configurarMovimientosEjemplo();
        configurarCerrarSesion(view); // ← NUEVO
    }

    private void inicializarVistas(View view) {
        tvNombreUsuario    = view.findViewById(R.id.tvNombreUsuario);
        tvModeloVehiculo   = view.findViewById(R.id.tvModeloVehiculo);
        tvPlacasHome       = view.findViewById(R.id.tvPlacasHome);
        tvVencimientoHome  = view.findViewById(R.id.tvVencimientoHome);
        tvPlanHome         = view.findViewById(R.id.tvPlanHome);
        tvEstadoPolizaHome = view.findViewById(R.id.tvEstadoPolizaHome);
        cardCotizar        = view.findViewById(R.id.cardCotizar);
        cardPagar          = view.findViewById(R.id.cardPagar);
        cardPolizas        = view.findViewById(R.id.cardPolizas);
        cardSoporte        = view.findViewById(R.id.cardSoporte);
        rvMovimientos      = view.findViewById(R.id.rvMovimientos);
    }

    private void cargarNombreUsuario() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("velox_prefs", requireActivity().MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Usuario");
        tvNombreUsuario.setText(nombre);
    }

    // GET /Poliza → busca la primera con Estado "Activa"
    private void cargarPolizaActiva() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, URL_POLIZAS, null,
                response -> {
                    try {
                        boolean encontrada = false;
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject poliza = response.getJSONObject(i);
                            if (poliza.getString("Estado").equals("Activa")) {
                                tvVencimientoHome.setText(poliza.getString("FechaVencimiento"));
                                tvPlanHome.setText(poliza.getString("_idPoliza"));
                                tvEstadoPolizaHome.setText("Activa");
                                String idVehiculo = poliza.optString("_idVehiculo", "");
                                if (!idVehiculo.isEmpty()) cargarVehiculo(idVehiculo);
                                encontrada = true;
                                break;
                            }
                        }
                        if (!encontrada) {
                            tvModeloVehiculo.setText("Sin póliza activa");
                            tvEstadoPolizaHome.setText("Inactiva");
                            tvPlacasHome.setText("—");
                            tvVencimientoHome.setText("—");
                            tvPlanHome.setText("—");
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> {
                    tvModeloVehiculo.setText("Sin conexión");
                    Toast.makeText(requireContext(), "No se pudo cargar la póliza", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(requireContext()).add(request);
    }

    // GET /Vehiculo/<id>
    private void cargarVehiculo(String idVehiculo) {
        String url = URL_VEHICULOS + "/" + idVehiculo;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String modelo = response.optString("modelo", response.optString("Modelo", "Nissan"));
                        String anio   = response.optString("Año",    response.optString("año", ""));
                        String placas = response.optString("Placas", "—");
                        tvModeloVehiculo.setText(modelo + " " + anio);
                        tvPlacasHome.setText(placas);
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> tvModeloVehiculo.setText("Vehículo no encontrado")
        );
        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void configurarAccesosRapidos(View view) {
        cardCotizar.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.cotizarFragment));
        cardPolizas.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.polizasFragment));
        cardPagar.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.pagosFragment));
        cardSoporte.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Soporte: 800-VELOX-01", Toast.LENGTH_LONG).show());
    }

    // ← NUEVO: limpia la sesión y regresa al Login
    private void configurarCerrarSesion(View view) {
        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            requireActivity()
                    .getSharedPreferences("velox_prefs", requireActivity().MODE_PRIVATE)
                    .edit()
                    .putBoolean("sesion_activa", false)
                    .remove("_idClientes")
                    .remove("nombre")
                    .apply();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void configurarMovimientosEjemplo() {
        MovimientosAdapter adapter = new MovimientosAdapter();
        rvMovimientos.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMovimientos.setAdapter(adapter);
        rvMovimientos.setNestedScrollingEnabled(false);
        List<Movimiento> lista = new ArrayList<>();
        lista.add(new Movimiento("Pago mensualidad", "08 May 2026", "-$890",  TipoMovimiento.PAGO));
        lista.add(new Movimiento("Póliza renovada",  "08 Ene 2026", "Activa", TipoMovimiento.POLIZA));
        lista.add(new Movimiento("Pago mensualidad", "08 Abr 2026", "-$890",  TipoMovimiento.PAGO));
        adapter.setLista(lista);
    }
}