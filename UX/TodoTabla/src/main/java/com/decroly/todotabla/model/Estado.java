package com.decroly.todotabla.model;

public class Estado {
    private String nombre;
    private String color;
    private int orden;

    public Estado(String nombre, String color, int orden) {
        this.nombre = nombre;
        this.color = color;
        this.orden = orden;
    }

    public String getNombre() {
        return nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Estado{");
        sb.append("nombre='").append(nombre).append('\'');
        sb.append(", color='").append(color).append('\'');
        sb.append(", orden=").append(orden);
        sb.append('}');
        return sb.toString();
    }
}
