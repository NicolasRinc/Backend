/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Remision;
import com.example.models.Ruta;
import com.example.models.RutaDTO;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Nicolas
 */
@Path("/rutas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RutaService {

    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    @GET
    @Path("/get")
    public Response obtenerRutas() {
        Query query = entityManager.createQuery("SELECT r FROM Ruta r");
        List<Ruta> rutas = query.getResultList();
        return Response.ok(rutas).build();
    }

    @POST
    @Path("/add")
    public Response crearRuta(RutaDTO rutaDTO) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Ruta ruta = new Ruta();
            ruta.setDescripcion(rutaDTO.getDescripcion());
            entityManager.persist(ruta);
            transaction.commit();
            return Response.status(Response.Status.CREATED).entity(ruta.getId()).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al crear la ruta").build();
        }
    }

    @PUT
    @Path("/update/{id}")
    public Response actualizarRuta(@PathParam("id") String id, Ruta rutaActualizada) {
        Ruta ruta = entityManager.find(Ruta.class, id);
        if (ruta == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ruta.setDescripcion(rutaActualizada.getDescripcion());
        entityManager.merge(ruta);
        return Response.ok().build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response eliminarRuta(@PathParam("id") String id) {
        Ruta ruta = entityManager.find(Ruta.class, id);
        if (ruta == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entityManager.remove(ruta);
        return Response.ok().build();
    }
}
