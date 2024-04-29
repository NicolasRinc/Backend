/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.PropietarioCarga;
import com.example.models.PropietarioCargaDTO;

import com.example.models.PropietarioCamion;
import com.example.models.PropietarioCamionDTO;
import com.example.models.Vehiculo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import org.json.simple.JSONObject;

/**
 *
 * @author Mauricio
 */
@Path("/propietariocamion")
@Produces(MediaType.APPLICATION_JSON)
public class PropietarioCamionService {

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
        Query q = entityManager.createQuery("select u from PropietarioCamion u order by u.nombre ASC");
        List<PropietarioCarga> conductor = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(conductor).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarConductor(PropietarioCamionDTO propietarioactualizado) {
        EntityTransaction transaction = null;
        try {
            System.out.println(propietarioactualizado.getId());
            PropietarioCamion propietario = entityManager.find(PropietarioCamion.class, propietarioactualizado.getId());
            if (propietario != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                // Actualizar los campos del conductor
                propietario.setCorreo(propietarioactualizado.getCorreo());
                propietario.setDireccion(propietarioactualizado.getDireccion());
                propietario.setNombre(propietarioactualizado.getNombre());
                propietario.setTelefono(propietarioactualizado.getTelefono());
                propietario.setVehicle(propietarioactualizado.getVehiculo());
                // Persistir los cambios con merge
                entityManager.merge(propietario);
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

    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarConductor(PropietarioCamionDTO PropietarioEliminado) {
        EntityTransaction transaction = null;
        try {
            PropietarioCamion temp = entityManager.find(PropietarioCamion.class, PropietarioEliminado.getId());
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
    public Response createCompetitor(PropietarioCamionDTO propietariocamion) {
        PropietarioCamion c = new PropietarioCamion();
        JSONObject rta = new JSONObject();
        c.setCorreo(propietariocamion.getCorreo());
        c.setDireccion(propietariocamion.getDireccion());
        c.setNombre(propietariocamion.getNombre());
        c.setTelefono(propietariocamion.getTelefono());
       
        propietariocamion.setVehiculo(obteneridvehiculo(propietariocamion.getVehiculo()));
        c.setVehicle(propietariocamion.getVehiculo());
        propietariocamion.setId(c.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(c);
            entityManager.getTransaction().commit();
            entityManager.refresh(c);
            rta.put("id_propietario", c.getId());
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
