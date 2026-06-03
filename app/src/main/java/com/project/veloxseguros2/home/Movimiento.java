package com.project.veloxseguros2.home;

public class Movimiento {

    public String descripcion;
    public String fecha;
    public String monto;
    public TipoMovimiento tipo;

    public Movimiento(String descripcion, String fecha,
                      String monto, TipoMovimiento tipo) {
        this.descripcion = descripcion;
        this.fecha       = fecha;
        this.monto       = monto;
        this.tipo        = tipo;
    }

}
