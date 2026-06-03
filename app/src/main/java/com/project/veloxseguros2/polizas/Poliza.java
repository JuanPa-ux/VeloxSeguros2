package com.project.veloxseguros2.polizas;

public class Poliza {

    public String modelo;
    public String placas;
    public String vencimiento;
    public String estado;
    public String monto;

    public Poliza(String modelo, String placas,
                  String vencimiento, String estado, String monto) {
        this.modelo      = modelo;
        this.placas      = placas;
        this.vencimiento = vencimiento;
        this.estado      = estado;
        this.monto       = monto;
    }

}
