package com.project.veloxseguros2.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.veloxseguros2.R;

import java.util.ArrayList;
import java.util.List;

public class MovimientosAdapter extends
        RecyclerView.Adapter<MovimientosAdapter.ViewHolder> {

    private List<Movimiento> lista = new ArrayList<>();

    // ==========================================
    // Actualizar lista
    // ==========================================

    public void setLista(List<Movimiento> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movimiento, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Movimiento m = lista.get(position);

        holder.tvDescripcion.setText(m.descripcion);
        holder.tvFecha.setText(m.fecha);
        holder.tvMonto.setText(m.monto);

        if (m.tipo == TipoMovimiento.PAGO) {
            holder.ivIcono.setImageResource(R.drawable.ic_credit_card);
        } else {
            holder.ivIcono.setImageResource(R.drawable.ic_shield);
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ==========================================
    // ViewHolder
    // ==========================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView  tvDescripcion;
        TextView  tvFecha;
        TextView  tvMonto;
        ImageView ivIcono;

        ViewHolder(View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionMovimiento);
            tvFecha       = itemView.findViewById(R.id.tvFechaMovimiento);
            tvMonto       = itemView.findViewById(R.id.tvMontoMovimiento);
            ivIcono       = itemView.findViewById(R.id.ivIconoMovimiento);
        }

    }

}
