/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.PropietarioCarga;
import com.example.models.PropietarioCargaDTO;


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
@Path("/conductor")
@Produces(MediaType.APPLICATION_JSON)
public class PropietarioCargaService {

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

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from PropietarioCarga u order by u.nombre ASC");
        List<PropietarioCarga> conductor = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(conductor).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarConductor(PropietarioCargaDTO propietarioActualizado) {
        EntityTransaction transaction = null;
        try {
            System.out.println(propietarioActualizado.getId());
            PropietarioCarga propietario = entityManager.find(PropietarioCarga.class, propietarioActualizado.getId());
            if (propietario != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                // Actualizar los campos del conductor
                propietario.setCorreo(propietarioActualizado.getCorreo());
                propietario.setDireccion(propietarioActualizado.getDireccion());
                propietario.setNombre(propietarioActualizado.getNombre());
                propietario.setTelefono(propietarioActualizado.getTelefono());
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
    public Response eliminarConductor(PropietarioCargaDTO propietarioEliminado) {
        EntityTransaction transaction = null;
        try {
            PropietarioCarga temp = entityManager.find(PropietarioCarga.class, propietarioEliminado.getId());
            if (temp != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(temp);
                transaction.commit();

                return Response.status(Response.Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("Conductor eliminado correctamente")
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
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error al eliminar el conductor")
                    .build();
        }
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(PropietarioCargaDTO propietario) {
        PropietarioCarga c = new PropietarioCarga();
        JSONObject rta = new JSONObject();
        c.setCorreo(propietario.getCorreo());
        c.setDireccion(propietario.getDireccion());
        c.setNombre(propietario.getNombre());
        c.setTelefono(propietario.getTelefono());
        propietario.setId(c.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(c);
            entityManager.getTransaction().commit();
            entityManager.refresh(c);
            rta.put("competitor_id", c.getId());
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
