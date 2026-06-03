package com.project.veloxseguros2.polizas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.project.veloxseguros2.R;

import java.util.List;

public class PolizasAdapter extends RecyclerView.Adapter<PolizasAdapter.ViewHolder> {

    private final List<Poliza> lista;

    public PolizasAdapter(List<Poliza> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_poliza, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Poliza p = lista.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvModelo.setText(p.modelo);
        holder.tvPlacas.setText(p.placas);
        holder.tvVencimiento.setText(p.vencimiento);
        holder.tvEstado.setText(p.estado);
        holder.tvMonto.setText(p.monto);

        // CORRECCIÓN: badge de estado con color dinámico según el estado real
        if (p.estado.equalsIgnoreCase("Activa")) {
            holder.tvEstado.setBackgroundResource(R.drawable.bg_badge_green);
            holder.tvEstado.setTextColor(ContextCompat.getColor(ctx, R.color.colorSuccess));
        } else {
            // "Por vencer", "Vencida" u otro estado
            holder.tvEstado.setBackgroundResource(R.drawable.bg_badge_amber);
            holder.tvEstado.setTextColor(ContextCompat.getColor(ctx, R.color.colorWarning));
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModelo, tvPlacas, tvVencimiento, tvEstado, tvMonto;

        ViewHolder(View itemView) {
            super(itemView);
            tvModelo      = itemView.findViewById(R.id.tvModeloPoliza);
            tvPlacas      = itemView.findViewById(R.id.tvPlacasPoliza);
            tvVencimiento = itemView.findViewById(R.id.tvVencimientoPoliza);
            tvEstado      = itemView.findViewById(R.id.tvEstadoPoliza);
            tvMonto       = itemView.findViewById(R.id.tvMontoPoliza);
        }
    }
}
