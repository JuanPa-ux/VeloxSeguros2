package com.project.veloxseguros2.pagos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.project.veloxseguros2.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PagosFragment extends Fragment {

    // ⚠️ CAMBIAR por la IP de tu computadora
    String URL_PAGOS = "http://192.168.20.130:5000/Pagos";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pagos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarPagos();
    }

    // GET /Pagos
    private void cargarPagos() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, URL_PAGOS, null,
                response -> {
                    // TODO: mostrar en RecyclerView
                    try {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject pago = response.getJSONObject(i);
                            sb.append(pago.optString("_idPagos"))
                              .append(" — $").append(pago.optString("Monto"))
                              .append("\n");
                        }
                        Toast.makeText(requireContext(),
                                response.length() + " pagos encontrados", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(requireContext(),
                        "Error al cargar pagos — verifica la IP", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(requireContext()).add(request);
    }
}
