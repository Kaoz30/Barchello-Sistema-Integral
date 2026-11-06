package com.barchello.cliente.models;


public class Producto {
    private final String nombre;
    private final String descripcion;
    private final double precio;
    private final int imagenResId; // por ahora usaremos placeholder_food

    public Producto(String nombre, String descripcion, double precio, int imagenResId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenResId = imagenResId;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public int getImagenResId() { return imagenResId; }
}