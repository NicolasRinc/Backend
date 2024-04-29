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
import com.example.models.VehiculoDTO;
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
@Path("/vehiculos")
@Produces(MediaType.APPLICATION_JSON)
public class VehiculoService {

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
        Query q = entityManager.createQuery("select u from Vehiculo u order by u.placa ASC");
        List<PropietarioCarga> conductor = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(conductor).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarConductor(VehiculoDTO vehiculoactualizado) {
        EntityTransaction transaction = null;
        try {
            System.out.println(vehiculoactualizado.getId());
            Vehiculo vehiculo = entityManager.find(Vehiculo.class, vehiculoactualizado.getId());
            if (vehiculo != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                // Actualizar los campos del conductor
                vehiculo.setMarca(vehiculoactualizado.getMarca());
                vehiculo.setPlaca(vehiculoactualizado.getPlaca());
                vehiculo.setTipodecarroceria(vehiculoactualizado.getTipodecarroceria());
                // Persistir los cambios con merge
                entityManager.merge(vehiculo);
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
    public Response eliminarConductor(VehiculoDTO vehiculoEliminado) {
        EntityTransaction transaction = null;
        try {
            Vehiculo temp = entityManager.find(Vehiculo.class, vehiculoEliminado.getId());
            if (temp != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(temp);
                transaction.commit();

                return Response.status(Response.Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("vehiculo eliminado correctamente")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("No se encontró el vehiculo")
                        .build();
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error al eliminar el vehiculo")
                    .build();
        }
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(VehiculoDTO vehiculo) {
        Vehiculo c  = new Vehiculo();
        JSONObject rta = new JSONObject();
        c.setMarca(vehiculo.getMarca());
        c.setPlaca(vehiculo.getPlaca());
        c.setTipodecarroceria(vehiculo.getTipodecarroceria());
        vehiculo.setId(c.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(c);
            entityManager.getTransaction().commit();
            entityManager.refresh(c);
            rta.put("id_vehiculo", c.getId());
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
    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }

}