/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Conductor;
import com.example.models.ConductorDTO;
import com.example.models.PropietarioCamion;
import com.example.models.PropietarioCamionDTO;
import com.example.models.PropietarioCarga;
import com.example.models.Vehiculo;
import com.sun.istack.NotNull;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.persistence.nosql.annotations.Field;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
@Path("/conductor")
@Produces(MediaType.APPLICATION_JSON)
public class ConductorService {
    @PersistenceContext(unitName = "mongoPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Conductor
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Conductor u order by u.nombre ASC");
        List<PropietarioCarga> conductor = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(conductor).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarConductor(ConductorDTO conductoractualizado) {
        EntityTransaction transaction = null;
        try {
            System.out.println(conductoractualizado.getId());
            Conductor conductor = entityManager.find(Conductor.class, conductoractualizado.getId());
            if (conductor != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                // Actualizar los campos del conductor
                conductor.setCorreo(conductoractualizado.getCorreo());
                conductor.setDireccion(conductoractualizado.getDireccion());
                conductor.setNombre(conductoractualizado.getNombre());
                conductor.setTelefono(conductoractualizado.getTelefono());
                conductor.setVehiculo(conductoractualizado.getVehiculo());
                // Persistir los cambios con merge
                entityManager.merge(conductor);
                transaction.commit();

                return Response.status(Response.Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("Conductor actualizado correctamente")
                        .build();

            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("No se encontró el conductor")
                        .build();
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error al actualizar el conductor")
                    .build();
        }
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarConductor(ConductorDTO ConductorEliminado) {
        EntityTransaction transaction = null;
        try {
            Conductor temp = entityManager.find(Conductor.class, ConductorEliminado.getId());
            if (temp != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(temp);
                transaction.commit();

                return Response.status(Response.Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("Propietario eliminado correctamente")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("No se encontró el Propietario")
                        .build();
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error al eliminar el Propietario")
                    .build();
        }
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(ConductorDTO conductor) {
        Conductor c = new Conductor();
        JSONObject rta = new JSONObject();
        c.setCorreo(conductor.getCorreo());
        c.setDireccion(conductor.getDireccion());
        c.setNombre(conductor.getNombre());
        c.setTelefono(conductor.getTelefono());
       
        conductor.setVehiculo(obteneridvehiculo(conductor.getVehiculo()));
        c.setVehiculo(conductor.getVehiculo());
        conductor.setId(c.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(c);
            entityManager.getTransaction().commit();
            entityManager.refresh(c);
            rta.put("id_conductor", c.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            c = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin",
                "*").entity(rta.toJSONString()).build();
    }

    public Vehiculo obteneridvehiculo(Vehiculo vehiculo) {
        Query q = entityManager.createQuery("select u from Vehiculo u order by u.placa ASC");
        List<Vehiculo> vehiculos = q.getResultList();
        for (Vehiculo temp : vehiculos) {
            if (vehiculo.getMarca().equals(temp.getMarca())) {
                vehiculo.setId(temp.getId());
                return vehiculo;
            }
        }
        return null;

    }

    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }
    
   
    
}
