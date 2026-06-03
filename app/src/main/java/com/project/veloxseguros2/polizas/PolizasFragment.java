package com.project.veloxseguros2.polizas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.project.veloxseguros2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PolizasFragment extends Fragment {

    RecyclerView rvPolizas;
    View layoutSinPolizas;

    // ⚠️ CAMBIAR por la IP de tu computadora
    String URL_POLIZAS = "http://192.168.20.130:5000/Poliza";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polizas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPolizas        = view.findViewById(R.id.rvPolizas);
        layoutSinPolizas = view.findViewById(R.id.layoutSinPolizas);
        cargarPolizas();
    }

    // GET /Poliza — carga todas las pólizas de la DB
    private void cargarPolizas() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, URL_POLIZAS, null,
                response -> {
                    List<Poliza> lista = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Poliza p = new Poliza(
                                    "Nissan",   // TODO: cruzar con /Vehiculo para modelo real
                                    obj.optString("_idVehiculo", "—"),
                                    obj.optString("FechaVencimiento", "—"),
                                    obj.optString("Estado", "—"),
                                    "$890/mes"
                            );
                            lista.add(p);
                        }
                    } catch (JSONException e) { e.printStackTrace(); }

                    if (lista.isEmpty()) {
                        rvPolizas.setVisibility(View.GONE);
                        layoutSinPolizas.setVisibility(View.VISIBLE);
                    } else {
                        rvPolizas.setVisibility(View.VISIBLE);
                        layoutSinPolizas.setVisibility(View.GONE);
                        PolizasAdapter adapter = new PolizasAdapter(lista);
                        rvPolizas.setLayoutManager(new LinearLayoutManager(requireContext()));
                        rvPolizas.setAdapter(adapter);
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Error al cargar pólizas", Toast.LENGTH_SHORT).show();
                    layoutSinPolizas.setVisibility(View.VISIBLE);
                    rvPolizas.setVisibility(View.GONE);
                }
        );
        Volley.newRequestQueue(requireContext()).add(request);
    }
}
