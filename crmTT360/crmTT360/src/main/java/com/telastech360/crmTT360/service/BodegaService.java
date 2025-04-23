package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.repository.BodegaRepository;
import com.telastech360.crmTT360.repository.EstadoRepository;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BodegaService {

    private final BodegaRepository bodegaRepository;
    private final EstadoRepository estadoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public BodegaService(BodegaRepository bodegaRepository,
                         EstadoRepository estadoRepository,
                         UsuarioRepository usuarioRepository) {
        this.bodegaRepository = bodegaRepository;
        this.estadoRepository = estadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ========== CRUD BÁSICO ========== //

    @Transactional
    public Bodega crearBodega(Bodega bodega) {
        // Validar nombre único
        if (bodegaRepository.existsByNombre(bodega.getNombre())) {
            throw new DuplicateResourceException("Ya existe una bodega con el nombre: " + bodega.getNombre());
        }

        // Validar estado existente
        Estado estado = estadoRepository.findById(bodega.getEstado().getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + bodega.getEstado().getEstadoId()));
        bodega.setEstado(estado);

        // Validar responsable existente (si aplica)
        if (bodega.getResponsable() != null) {
            Usuario responsable = usuarioRepository.findById(bodega.getResponsable().getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + bodega.getResponsable().getUsuarioId()));
            bodega.setResponsable(responsable);
        }

        return bodegaRepository.save(bodega);
    }

    @Transactional(readOnly = true)
    public Bodega obtenerBodegaPorId(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Bodega> listarTodasLasBodegas() {
        return bodegaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public Bodega actualizarBodega(Long id, Bodega bodegaActualizada) {
        Bodega bodegaExistente = obtenerBodegaPorId(id);

        // Validar nombre único si cambia
        if (!bodegaExistente.getNombre().equals(bodegaActualizada.getNombre()) &&
                bodegaRepository.existsByNombre(bodegaActualizada.getNombre())) {
            throw new DuplicateResourceException("Ya existe una bodega con el nombre: " + bodegaActualizada.getNombre());
        }

        // Actualizar campos básicos
        bodegaExistente.setNombre(bodegaActualizada.getNombre());
        bodegaExistente.setTipoBodega(bodegaActualizada.getTipoBodega());
        bodegaExistente.setCapacidadMaxima(bodegaActualizada.getCapacidadMaxima());
        bodegaExistente.setUbicacion(bodegaActualizada.getUbicacion());

        // Actualizar estado si viene en el request
        if (bodegaActualizada.getEstado() != null) {
            Estado estado = estadoRepository.findById(bodegaActualizada.getEstado().getEstadoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + bodegaActualizada.getEstado().getEstadoId()));
            bodegaExistente.setEstado(estado);
        }

        // Actualizar responsable si viene en el request
        if (bodegaActualizada.getResponsable() != null) {
            Usuario responsable = usuarioRepository.findById(bodegaActualizada.getResponsable().getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + bodegaActualizada.getResponsable().getUsuarioId()));
            bodegaExistente.setResponsable(responsable);
        } else {
            bodegaExistente.setResponsable(null);
        }

        return bodegaRepository.save(bodegaExistente);
    }

    @Transactional
    public void eliminarBodega(Long id) {
        Bodega bodega = obtenerBodegaPorId(id);

        // Validar que no tenga items asociados
        if (bodegaRepository.existsBodegaWithItems(id)) {
            throw new IllegalOperationException("No se puede eliminar la bodega porque tiene items asociados");
        }

        bodegaRepository.delete(bodega);
    }

    // ========== MÉTODOS ESPECIALIZADOS ========== //

    @Transactional(readOnly = true)
    public List<Bodega> buscarBodegasPorTipo(Bodega.TipoBodega tipo) {
        return bodegaRepository.findByTipoBodega(tipo);
    }

    @Transactional(readOnly = true)
    public List<Bodega> buscarBodegasPorUbicacion(String ubicacion) {
        return bodegaRepository.findByUbicacionContaining(ubicacion);
    }

    @Transactional(readOnly = true)
    public List<Bodega> buscarBodegasConCapacidadDisponible() {
        return bodegaRepository.findBodegasConCapacidadDisponible();
    }

    @Transactional(readOnly = true)
    public List<Bodega> buscarBodegasPorResponsable(Long responsableId) {
        Usuario responsable = usuarioRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + responsableId));
        return bodegaRepository.findByResponsable(responsable);
    }

    @Transactional(readOnly = true)
    public boolean existeBodegaConNombre(String nombre) {
        return bodegaRepository.existsByNombre(nombre);
    }
}