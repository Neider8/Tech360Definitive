package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.repository.BodegaRepository;
import com.telastech360.crmTT360.repository.EstadoRepository;
import com.telastech360.crmTT360.repository.ItemRepository; // Para verificar items asociados
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar la lógica de negocio relacionada con las Bodegas.
 */
@Service
public class BodegaService {

    private static final Logger log = LoggerFactory.getLogger(BodegaService.class);

    private final BodegaRepository bodegaRepository;
    private final EstadoRepository estadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemRepository itemRepository; // Para verificar items al eliminar

    /**
     * Constructor para inyección de dependencias.
     * @param bodegaRepository Repositorio para Bodegas.
     * @param estadoRepository Repositorio para Estados.
     * @param usuarioRepository Repositorio para Usuarios (responsables).
     * @param itemRepository Repositorio para Items (verificar asociación).
     */
    @Autowired
    public BodegaService(BodegaRepository bodegaRepository,
                         EstadoRepository estadoRepository,
                         UsuarioRepository usuarioRepository,
                         ItemRepository itemRepository) { // Inyectar ItemRepository
        this.bodegaRepository = bodegaRepository;
        this.estadoRepository = estadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemRepository = itemRepository; // Asignar ItemRepository
    }

    /**
     * Crea una nueva bodega.
     * @param bodega La entidad Bodega a crear (sin ID). Se valida nombre único y relaciones.
     * @return La entidad Bodega creada y guardada.
     * @throws DuplicateResourceException Si el nombre ya existe.
     * @throws ResourceNotFoundException Si el Estado o Usuario responsable (si se provee) no existen.
     */
    @Transactional
    public Bodega crearBodega(Bodega bodega) {
        log.info("Intentando crear bodega con nombre: '{}'", bodega.getNombre());
        if (bodegaRepository.existsByNombre(bodega.getNombre())) {
            log.warn("Intento de crear bodega con nombre duplicado: {}", bodega.getNombre());
            throw new DuplicateResourceException("Ya existe una bodega con el nombre: " + bodega.getNombre());
        }

        // Validar y obtener Estado
        if (bodega.getEstado() == null || bodega.getEstado().getEstadoId() == null) {
            log.error("Estado es nulo o no tiene ID al crear bodega '{}'", bodega.getNombre());
            throw new IllegalArgumentException("El estado es obligatorio para crear una bodega.");
        }
        Estado estado = estadoRepository.findById(bodega.getEstado().getEstadoId())
                .orElseThrow(() -> {
                    log.error("Estado con ID {} no encontrado al crear bodega '{}'", bodega.getEstado().getEstadoId(), bodega.getNombre());
                    return new ResourceNotFoundException("Estado no encontrado con ID: " + bodega.getEstado().getEstadoId());
                });
        bodega.setEstado(estado);
        log.debug("Estado '{}' (ID:{}) asignado a nueva bodega.", estado.getValor(), estado.getEstadoId());

        // Validar y obtener Usuario responsable (si se especifica)
        if (bodega.getResponsable() != null && bodega.getResponsable().getUsuarioId() != null) {
            Usuario responsable = usuarioRepository.findById(bodega.getResponsable().getUsuarioId())
                    .orElseThrow(() -> {
                        log.error("Usuario responsable con ID {} no encontrado al crear bodega '{}'", bodega.getResponsable().getUsuarioId(), bodega.getNombre());
                        return new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + bodega.getResponsable().getUsuarioId());
                    });
            bodega.setResponsable(responsable);
            log.debug("Usuario responsable '{}' (ID:{}) asignado a nueva bodega.", responsable.getNombre(), responsable.getUsuarioId());
        } else {
            bodega.setResponsable(null); // Asegurar que sea null si no se especifica ID
            log.debug("No se especificó responsable para la nueva bodega '{}'.", bodega.getNombre());
        }

        Bodega bodegaGuardada = bodegaRepository.save(bodega);
        log.info("Bodega '{}' creada exitosamente con ID: {}", bodegaGuardada.getNombre(), bodegaGuardada.getBodegaId());
        return bodegaGuardada;
    }

    /**
     * Obtiene una bodega por su ID.
     * @param id ID de la bodega a buscar.
     * @return La entidad Bodega encontrada.
     * @throws ResourceNotFoundException si la bodega no existe.
     */
    @Transactional(readOnly = true)
    public Bodega obtenerBodegaPorId(Long id) {
        log.info("Buscando bodega por ID: {}", id);
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Bodega con ID {} no encontrada.", id);
                    return new ResourceNotFoundException("Bodega no encontrada con ID: " + id);
                });
        log.debug("Bodega encontrada: {} (ID: {})", bodega.getNombre(), id);
        return bodega;
    }

    /**
     * Lista todas las bodegas registradas, ordenadas por nombre.
     * @return Lista de entidades Bodega.
     */
    @Transactional(readOnly = true)
    public List<Bodega> listarTodasLasBodegas() {
        log.info("Listando todas las bodegas...");
        List<Bodega> bodegas = bodegaRepository.findAllByOrderByNombreAsc();
        log.debug("Se encontraron {} bodegas.", bodegas.size());
        return bodegas;
    }

    /**
     * Actualiza una bodega existente.
     * @param id ID de la bodega a actualizar.
     * @param bodegaActualizada Entidad Bodega con los nuevos datos.
     * @return La entidad Bodega actualizada.
     * @throws ResourceNotFoundException Si la bodega, el nuevo estado o el nuevo responsable no existen.
     * @throws DuplicateResourceException Si el nuevo nombre ya está en uso por otra bodega.
     */
    @Transactional
    public Bodega actualizarBodega(Long id, Bodega bodegaActualizada) {
        log.info("Intentando actualizar bodega con ID: {}", id);
        Bodega bodegaExistente = obtenerBodegaPorId(id); // Valida existencia

        // Validar nombre único si cambia
        if (!bodegaExistente.getNombre().equalsIgnoreCase(bodegaActualizada.getNombre())) {
            log.debug("El nombre de la bodega ID {} ha cambiado a '{}'. Verificando disponibilidad...", id, bodegaActualizada.getNombre());
            if (bodegaRepository.existsByNombre(bodegaActualizada.getNombre())) {
                log.warn("Conflicto: El nombre '{}' ya está en uso por otra bodega.", bodegaActualizada.getNombre());
                throw new DuplicateResourceException("Ya existe una bodega con el nombre: " + bodegaActualizada.getNombre());
            }
            log.debug("Nombre '{}' disponible.", bodegaActualizada.getNombre());
            bodegaExistente.setNombre(bodegaActualizada.getNombre());
        }

        // Actualizar otros campos
        bodegaExistente.setTipoBodega(bodegaActualizada.getTipoBodega());
        bodegaExistente.setCapacidadMaxima(bodegaActualizada.getCapacidadMaxima());
        bodegaExistente.setUbicacion(bodegaActualizada.getUbicacion());
        log.debug("Campos básicos (tipo, capacidad, ubicación) actualizados para bodega ID {}.", id);

        // Actualizar Estado (si se proporciona ID)
        if (bodegaActualizada.getEstado() != null && bodegaActualizada.getEstado().getEstadoId() != null) {
            if (bodegaExistente.getEstado() == null || !bodegaExistente.getEstado().getEstadoId().equals(bodegaActualizada.getEstado().getEstadoId())) {
                log.debug("Actualizando estado para bodega ID {} al ID: {}", id, bodegaActualizada.getEstado().getEstadoId());
                Estado estado = estadoRepository.findById(bodegaActualizada.getEstado().getEstadoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + bodegaActualizada.getEstado().getEstadoId()));
                bodegaExistente.setEstado(estado);
            }
        } // Si no se proporciona, no se cambia el estado existente

        // Actualizar Responsable (si se proporciona ID o es explícitamente null)
        Long idResponsableNuevo = (bodegaActualizada.getResponsable() != null) ? bodegaActualizada.getResponsable().getUsuarioId() : null;
        Long idResponsableActual = (bodegaExistente.getResponsable() != null) ? bodegaExistente.getResponsable().getUsuarioId() : null;

        if (idResponsableNuevo != idResponsableActual) { // Comparar IDs directamente
            if (idResponsableNuevo != null) {
                log.debug("Actualizando responsable para bodega ID {} al Usuario ID: {}", id, idResponsableNuevo);
                Usuario responsable = usuarioRepository.findById(idResponsableNuevo)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + idResponsableNuevo));
                bodegaExistente.setResponsable(responsable);
            } else {
                log.debug("Quitando responsable de bodega ID {}.", id);
                bodegaExistente.setResponsable(null);
            }
        }

        Bodega bodegaGuardada = bodegaRepository.save(bodegaExistente);
        log.info("Bodega ID {} actualizada exitosamente.", id);
        return bodegaGuardada;
    }

    /**
     * Elimina una bodega por su ID.
     * Verifica que la bodega no contenga items antes de eliminarla.
     * @param id ID de la bodega a eliminar.
     * @throws ResourceNotFoundException si la bodega no existe.
     * @throws IllegalOperationException si la bodega tiene items asociados.
     */
    @Transactional
    public void eliminarBodega(Long id) {
        log.info("Intentando eliminar bodega con ID: {}", id);
        Bodega bodega = obtenerBodegaPorId(id); // Valida existencia

        // Verificar si tiene items asociados usando ItemRepository
        // boolean tieneItems = itemRepository.existsByBodega(bodega); // Necesitaría método en ItemRepo
        // Solución alternativa usando el método del repo de Bodega (si existe):
        boolean tieneItems = bodegaRepository.existsBodegaWithItems(id);
        if (tieneItems) {
            log.warn("Intento de eliminar bodega ID {} ('{}') que tiene items asociados.", id, bodega.getNombre());
            throw new IllegalOperationException("No se puede eliminar la bodega '" + bodega.getNombre() + "' porque tiene items asociados.");
        }
        log.debug("La bodega ID {} ('{}') no tiene items asociados.", id, bodega.getNombre());

        bodegaRepository.delete(bodega);
        log.info("Bodega ID {} ('{}') eliminada exitosamente.", id, bodega.getNombre());
    }

    // ========== MÉTODOS ESPECIALIZADOS ========== //

    /**
     * Busca bodegas por su tipo.
     * @param tipo El TipoBodega a filtrar.
     * @return Lista de entidades Bodega coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Bodega> buscarBodegasPorTipo(Bodega.TipoBodega tipo) {
        log.info("Buscando bodegas por tipo: {}", tipo);
        List<Bodega> bodegas = bodegaRepository.findByTipoBodega(tipo);
        log.debug("Se encontraron {} bodegas del tipo {}", bodegas.size(), tipo);
        return bodegas;
    }

    /**
     * Busca bodegas cuya ubicación contenga el texto proporcionado.
     * @param ubicacion Texto a buscar en la ubicación.
     * @return Lista de entidades Bodega coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Bodega> buscarBodegasPorUbicacion(String ubicacion) {
        log.info("Buscando bodegas por ubicación que contenga: '{}'", ubicacion);
        List<Bodega> bodegas = bodegaRepository.findByUbicacionContaining(ubicacion);
        log.debug("Se encontraron {} bodegas para la búsqueda de ubicación '{}'", bodegas.size(), ubicacion);
        return bodegas;
    }

    /**
     * Busca bodegas que aún tengan capacidad disponible (capacidad máxima > suma del stock de sus items).
     * @return Lista de entidades Bodega con capacidad disponible.
     */
    @Transactional(readOnly = true)
    public List<Bodega> buscarBodegasConCapacidadDisponible() {
        log.info("Buscando bodegas con capacidad disponible...");
        List<Bodega> bodegas = bodegaRepository.findBodegasConCapacidadDisponible();
        log.debug("Se encontraron {} bodegas con capacidad disponible.", bodegas.size());
        return bodegas;
    }

    /**
     * Busca bodegas asignadas a un usuario responsable específico.
     * @param responsableId ID del usuario responsable.
     * @return Lista de entidades Bodega asignadas a ese responsable.
     * @throws ResourceNotFoundException si el usuario responsable no existe.
     */
    @Transactional(readOnly = true)
    public List<Bodega> buscarBodegasPorResponsable(Long responsableId) {
        log.info("Buscando bodegas por responsable ID: {}", responsableId);
        // Validar que el usuario exista primero
        Usuario responsable = usuarioRepository.findById(responsableId)
                .orElseThrow(() -> {
                    log.warn("Usuario responsable con ID {} no encontrado al buscar bodegas.", responsableId);
                    return new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + responsableId);
                });
        List<Bodega> bodegas = bodegaRepository.findByResponsable(responsable);
        log.debug("Se encontraron {} bodegas para el responsable ID {}", bodegas.size(), responsableId);
        return bodegas;
    }

    /**
     * Verifica si existe una bodega con el nombre dado (case-insensitive).
     * @param nombre Nombre de la bodega a verificar.
     * @return true si existe, false en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existeBodegaConNombre(String nombre) {
        log.debug("Verificando existencia de bodega con nombre: '{}'", nombre);
        return bodegaRepository.existsByNombre(nombre); // Asumiendo que existsByNombre es case-insensitive o ajusta
    }
}