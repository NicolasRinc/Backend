/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.models;

/**
 *
 * @author Nicolas
 */
public class VehiculoDTO {

    private String placa;
    private String marca;
    private String Tipodecarroceria;
    private String id;

    public VehiculoDTO() {
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTipodecarroceria() {
        return Tipodecarroceria;
    }

    public void setTipodecarroceria(String Tipodecarroceria) {
        this.Tipodecarroceria = Tipodecarroceria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
