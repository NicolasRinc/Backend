package com.example.services;

import com.example.PersistenceManager;
import com.example.models.PropietarioCarga;
import com.example.models.Solicitud;
import com.example.models.SolicitudDTO;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/solicitudes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolicitudCargaService {

    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    @GET
    @Path("/get")
    public Response obtenerSolicitudes() {
        Query query = entityManager.createQuery("SELECT s FROM Solicitud s ORDER BY s.fecha DESC");
        List<Solicitud> solicitudes = query.getResultList();
        return Response.ok(solicitudes).build();
    }

    @POST
    @Path("/add")
    public Response agregarSolicitud(SolicitudDTO solicitudDTO) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Solicitud solicitud = new Solicitud();

            solicitud.setFecha(solicitudDTO.getFecha());
            solicitud.setOrigen(solicitudDTO.getOrigen());
            solicitud.setDestino(solicitudDTO.getDestino());
            solicitud.setDimensiones(solicitudDTO.getDimensiones());
            solicitud.setPeso(solicitudDTO.getPeso());
            solicitud.setValorAsegurado(solicitudDTO.getValorAsegurado());
            solicitud.setEmpaque(solicitudDTO.getEmpaque());

            if (solicitudDTO.getPropietarioCarga() != null) {
                PropietarioCarga propietarioCarga = new PropietarioCarga();
                propietarioCarga.setNombre(solicitudDTO.getPropietarioCarga().getNombre());
                propietarioCarga.setCorreo(solicitudDTO.getPropietarioCarga().getCorreo());
                propietarioCarga.setTelefono(solicitudDTO.getPropietarioCarga().getTelefono());
                propietarioCarga.setDireccion(solicitudDTO.getPropietarioCarga().getDireccion());
                solicitud.setPropietarioCarga(propietarioCarga);
            }

            entityManager.persist(solicitud);
            transaction.commit();
            return Response.status(Response.Status.CREATED).entity(solicitud.getId()).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al agregar la solicitud").build();
        }
    }

    @PUT
    @Path("/update/{id}")
    public Response actualizarSolicitud(@PathParam("id") String id, SolicitudDTO solicitudDTO) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Solicitud solicitud = entityManager.find(Solicitud.class, id);
            if (solicitud == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Solicitud no encontrada").build();
            }
            transaction.begin();

            solicitud.setFecha(solicitudDTO.getFecha());
            solicitud.setOrigen(solicitudDTO.getOrigen());
            solicitud.setDestino(solicitudDTO.getDestino());
            solicitud.setDimensiones(solicitudDTO.getDimensiones());
            solicitud.setPeso(solicitudDTO.getPeso());
            solicitud.setValorAsegurado(solicitudDTO.getValorAsegurado());
            solicitud.setEmpaque(solicitudDTO.getEmpaque());

            if (solicitudDTO.getPropietarioCarga() != null) {
                PropietarioCarga propietarioCarga = solicitud.getPropietarioCarga();
                propietarioCarga.setNombre(solicitudDTO.getPropietarioCarga().getNombre());
                propietarioCarga.setCorreo(solicitudDTO.getPropietarioCarga().getCorreo());
                propietarioCarga.setTelefono(solicitudDTO.getPropietarioCarga().getTelefono());
                propietarioCarga.setDireccion(solicitudDTO.getPropietarioCarga().getDireccion());
            }

            entityManager.merge(solicitud);
            transaction.commit();
            return Response.ok().entity("Solicitud actualizada correctamente").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al actualizar la solicitud").build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    public Response eliminarSolicitud(@PathParam("id") String id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Solicitud solicitud = entityManager.find(Solicitud.class, id);
            if (solicitud == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Solicitud no encontrada").build();
            }
            transaction.begin();
            entityManager.remove(solicitud);
            transaction.commit();
            return Response.ok().entity("Solicitud eliminada correctamente").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al eliminar la solicitud").build();
        }
    }

    @OPTIONS
    @Path("{path : .*}")
    public Response options() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .build();
    }
}
