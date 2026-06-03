package com.project.veloxseguros2.splash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.veloxseguros2.R;

// NUEVO ARCHIVO — faltaba en el rediseño, causaba error "cannot find symbol"
public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {

    private final String[] titulos = {
            "Cotiza en segundos",
            "Pólizas a tu medida",
            "Paga sin complicaciones"
    };

    private final String[] descripciones = {
            "Obtén el mejor precio para tu Nissan en minutos.",
            "Elige el plan que se adapte a tu presupuesto.",
            "Tarjeta, transferencia o efectivo. Tú decides."
    };

    private final int[] iconos = {
            R.drawable.ic_file_text,
            R.drawable.ic_shield,
            R.drawable.ic_credit_card
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitulo.setText(titulos[position]);
        holder.tvDescripcion.setText(descripciones[position]);
        holder.ivIcono.setImageResource(iconos[position]);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;
        ImageView ivIcono;

        ViewHolder(View itemView) {
            super(itemView);
            // IDs que coinciden con item_onboarding.xml del rediseño
            tvTitulo     = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            ivIcono      = itemView.findViewById(R.id.ivIcono);
        }
    }
}
