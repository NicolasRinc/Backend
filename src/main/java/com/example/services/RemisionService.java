  /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Remision;
import com.example.models.RemisionDTO;
import com.example.models.Ruta;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
@Path("/remisiones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RemisionService {

    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    @GET
    @Path("/get")
    public Response obtenerRemisiones() {
        Query query = entityManager.createQuery("SELECT r FROM Remision r");
        List<Remision> remisiones = query.getResultList();
        return Response.ok(remisiones).build();
    }

    @POST
    @Path("/add")
    public Response crearRemision(RemisionDTO remisionDTO) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Remision remision = new Remision();

            remision.setFechaHoraRecogida(remisionDTO.getFechaHoraRecogida());
            remision.setOrigen(remisionDTO.getOrigen());
            remision.setDestino(remisionDTO.getDestino());
            remision.setPlacaCamion(remisionDTO.getPlacaCamion());
            remision.setConductor(remisionDTO.getConductor());

            // Buscar la ruta correspondiente usando el ID proporcionado
            Ruta ruta = entityManager.find(Ruta.class, remisionDTO.getRuta().getId());
            if (ruta == null) {
                // Si la ruta no existe, devuelve un error
                return Response.status(Response.Status.BAD_REQUEST).entity("La ruta proporcionada no existe").build();
            }
            remision.setRuta(ruta);

            entityManager.persist(remision);
            transaction.commit();
            return Response.status(Response.Status.CREATED).entity(remision.getId()).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al crear la remisión").build();
        }
    }

    @PUT
    @Path("/update/{id}")
    public Response actualizarRemision(@PathParam("id") String id, Remision remisionActualizada) {
        Remision remision = entityManager.find(Remision.class, id);
        if (remision == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        remision.setFechaHoraRecogida(remisionActualizada.getFechaHoraRecogida());
        remision.setOrigen(remisionActualizada.getOrigen());
        remision.setDestino(remisionActualizada.getDestino());
        remision.setPlacaCamion(remisionActualizada.getPlacaCamion());
        remision.setConductor(remisionActualizada.getConductor());
        remision.setRuta(remisionActualizada.getRuta());
        entityManager.merge(remision);
        return Response.ok().build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response eliminarRemision(@PathParam("id") String id) {
        Remision remision = entityManager.find(Remision.class, id);
        if (remision == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entityManager.remove(remision);
        return Response.ok().build();
    }
}
