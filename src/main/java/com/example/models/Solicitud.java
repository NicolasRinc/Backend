package com.example.models;

import com.sun.istack.NotNull;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

@NoSql(dataFormat = DataFormatType.MAPPED)
@Entity
@XmlRootElement
public class Solicitud implements Serializable {
    
    private String origen;
    private String destino;
    private String dimensiones;
    private Double peso;
    private Double valorAsegurado;
    private String empaque;
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private String id;

    @NotNull
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt;

    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedAt;

    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar fecha;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private PropietarioCarga propietarioCarga;

    @PreUpdate
    private void updateTimestamp() {
        this.updatedAt = Calendar.getInstance();
    }

    @PrePersist
    private void creationTimestamp() {
        this.createdAt = this.updatedAt = Calendar.getInstance();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getFecha() {
        return fecha;
    }

    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    public PropietarioCarga getPropietarioCarga() {
        return propietarioCarga;
    }

    public void setPropietarioCarga(PropietarioCarga propietarioCarga) {
        this.propietarioCarga = propietarioCarga;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getValorAsegurado() {
        return valorAsegurado;
    }

    public void setValorAsegurado(Double valorAsegurado) {
        this.valorAsegurado = valorAsegurado;
    }

    public String getEmpaque() {
        return empaque;
    }

    public void setEmpaque(String empaque) {
        this.empaque = empaque;
    }
}
