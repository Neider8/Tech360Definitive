package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Item; // Para verificar items
import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.ProveedorRepository;
import com.telastech360.crmTT360.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio relacionada con los Proveedores.
 */
@Service
public class ProveedorService {

    private static final Logger log = LoggerFactory.getLogger(ProveedorService.class);

    private final ProveedorRepository proveedorRepository;
    private final ItemRepository itemRepository; // Para verificar items al eliminar

    /**
     * Constructor para inyección de dependencias.
     * @param proveedorRepository Repositorio para Proveedores.
     * @param itemRepository Repositorio para Items (verificar asociación).
     */
    @Autowired
    public ProveedorService(ProveedorRepository proveedorRepository, ItemRepository itemRepository) {
        this.proveedorRepository = proveedorRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Busca proveedores cuyo nombre Y dirección (ubicación) contengan los textos dados.
     * Búsqueda case-insensitive.
     * @param nombre Texto a buscar en el nombre.
     * @param ubicacion Texto a buscar en la dirección.
     * @return Lista de entidades Proveedor coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedoresPorNombreYUbicacion(String nombre, String ubicacion) {
        log.info("Buscando proveedores por nombre '{}' y ubicación '{}'", nombre, ubicacion);
        List<Proveedor> proveedores = proveedorRepository.findByNombreAndDireccionContainingIgnoreCase(nombre, ubicacion); // Asume método en repo
        log.debug("Búsqueda por nombre/ubicación encontró {} proveedores.", proveedores.size());
        return proveedores;
    }

    /**
     * Crea un nuevo proveedor.
     * Valida unicidad de email y teléfono.
     * @param proveedor Entidad Proveedor con los datos a crear.
     * @return La entidad Proveedor creada y guardada.
     * @throws DuplicateResourceException Si el email o teléfono ya existen.
     */
    @Transactional
    public Proveedor crearProveedor(Proveedor proveedor) {
        log.info("Intentando crear proveedor con email: {}", proveedor.getEmail());
        if (proveedorRepository.existsByEmail(proveedor.getEmail())) {
            log.warn("Intento de crear proveedor con email duplicado: {}", proveedor.getEmail());
            throw new DuplicateResourceException("El email ya está registrado: " + proveedor.getEmail());
        }

        if (proveedor.getTelefono() != null && !proveedor.getTelefono().isBlank() &&
                proveedorRepository.findByTelefono(proveedor.getTelefono()).isPresent()) {
            log.warn("Intento de crear proveedor con teléfono duplicado: {}", proveedor.getTelefono());
            throw new DuplicateResourceException("El teléfono ya está registrado: " + proveedor.getTelefono());
        }
        log.debug("Validaciones de unicidad superadas para email {} y teléfono {}", proveedor.getEmail(), proveedor.getTelefono());

        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        log.info("Proveedor '{}' (Email: {}) creado exitosamente con ID: {}",
                proveedorGuardado.getNombre(), proveedorGuardado.getEmail(), proveedorGuardado.getProveedorId());
        return proveedorGuardado;
    }

    /**
     * Obtiene un proveedor por su ID.
     * @param id ID del proveedor a buscar.
     * @return La entidad Proveedor encontrada.
     * @throws ResourceNotFoundException si el proveedor no existe.
     */
    @Transactional(readOnly = true)
    public Proveedor obtenerProveedorPorId(Long id) {
        log.info("Buscando proveedor por ID: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Proveedor con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);
                });
        log.debug("Proveedor encontrado: {} (ID: {})", proveedor.getNombre(), id);
        return proveedor;
    }

    /**
     * Lista todos los proveedores registrados, ordenados por nombre.
     * @return Lista de entidades Proveedor.
     */
    @Transactional(readOnly = true)
    public List<Proveedor> listarTodosLosProveedores() {
        log.info("Listando todos los proveedores...");
        List<Proveedor> proveedores = proveedorRepository.findAllByOrderByNombreAsc();
        log.debug("Se encontraron {} proveedores.", proveedores.size());
        return proveedores;
    }

    /**
     * Actualiza un proveedor existente.
     * Valida unicidad del nuevo email y teléfono si cambian.
     * @param id ID del proveedor a actualizar.
     * @param proveedorActualizado Entidad Proveedor con los nuevos datos.
     * @return La entidad Proveedor actualizada.
     * @throws ResourceNotFoundException si el proveedor no existe.
     * @throws DuplicateResourceException si el nuevo email o teléfono ya están en uso por otro proveedor.
     */
    @Transactional
    public Proveedor actualizarProveedor(Long id, Proveedor proveedorActualizado) {
        log.info("Intentando actualizar proveedor con ID: {}", id);
        Proveedor proveedorExistente = obtenerProveedorPorId(id); // Valida existencia

        // Validar email si cambia
        if (!proveedorExistente.getEmail().equalsIgnoreCase(proveedorActualizado.getEmail())) {
            log.debug("Email cambiado para proveedor ID {}. Verificando disponibilidad de '{}'", id, proveedorActualizado.getEmail());
            if (proveedorRepository.existsByEmail(proveedorActualizado.getEmail())) {
                log.warn("Conflicto: Email '{}' ya registrado al actualizar proveedor ID {}", proveedorActualizado.getEmail(), id);
                throw new DuplicateResourceException("El email ya está registrado: " + proveedorActualizado.getEmail());
            }
            log.debug("Email '{}' disponible.", proveedorActualizado.getEmail());
            proveedorExistente.setEmail(proveedorActualizado.getEmail());
        }

        // Validar teléfono si cambia
        String telNuevo = proveedorActualizado.getTelefono();
        String telActual = proveedorExistente.getTelefono();
        if (telNuevo != null && !telNuevo.isBlank() && !telNuevo.equals(telActual)) {
            log.debug("Teléfono cambiado para proveedor ID {}. Verificando disponibilidad de '{}'", id, telNuevo);
            if (proveedorRepository.findByTelefono(telNuevo).isPresent()) {
                log.warn("Conflicto: Teléfono '{}' ya registrado al actualizar proveedor ID {}", telNuevo, id);
                throw new DuplicateResourceException("El teléfono ya está registrado: " + telNuevo);
            }
            log.debug("Teléfono '{}' disponible.", telNuevo);
            proveedorExistente.setTelefono(telNuevo);
        } else if ((telNuevo == null || telNuevo.isBlank()) && telActual != null) {
            log.debug("Quitando teléfono del proveedor ID {}", id);
            proveedorExistente.setTelefono(null); // Permitir quitar teléfono
        }


        // Actualizar otros campos
        proveedorExistente.setNombre(proveedorActualizado.getNombre());
        proveedorExistente.setDireccion(proveedorActualizado.getDireccion());
        log.debug("Campos nombre y dirección actualizados para proveedor ID {}.", id);

        Proveedor proveedorGuardado = proveedorRepository.save(proveedorExistente);
        log.info("Proveedor ID {} actualizado exitosamente.", id);
        return proveedorGuardado;
    }

    /**
     * Elimina un proveedor por su ID.
     * Verifica que el proveedor no esté asociado a ningún item antes de eliminar.
     * @param id ID del proveedor a eliminar.
     * @throws ResourceNotFoundException si el proveedor no existe.
     * @throws IllegalOperationException si el proveedor tiene items asociados.
     */
    @Transactional
    public void eliminarProveedor(Long id) {
        log.info("Intentando eliminar proveedor con ID: {}", id);
        Proveedor proveedor = obtenerProveedorPorId(id); // Valida existencia

        // Validar que no esté en uso por Items
        long itemCount = itemRepository.countByProveedor(proveedor); // Asume método en ItemRepo
        if (itemCount > 0) {
            log.warn("Intento de eliminar proveedor ID {} ('{}') que está asociado a {} item(s).", id, proveedor.getNombre(), itemCount);
            throw new IllegalOperationException("No se puede eliminar el proveedor '" + proveedor.getNombre() + "' porque tiene " + itemCount + " items asociados.");
        }
        log.debug("El proveedor ID {} ('{}') no tiene items asociados.", id, proveedor.getNombre());

        proveedorRepository.delete(proveedor);
        log.info("Proveedor ID {} ('{}') eliminado exitosamente.", id, proveedor.getNombre());
    }

    /**
     * Busca proveedores (método alias para listar todos si no hay criterios).
     * @return Lista de todos los proveedores.
     */
    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedores() {
        log.info("Buscando todos los proveedores (sin filtro específico).");
        return listarTodosLosProveedores();
    }

    /**
     * Busca proveedores cuyo nombre contenga el texto dado (ignorando mayúsculas/minúsculas).
     * @param nombre Texto a buscar en el nombre.
     * @return Lista de entidades Proveedor coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedoresPorNombre(String nombre) {
        log.info("Buscando proveedores por nombre que contenga: '{}'", nombre);
        List<Proveedor> proveedores = proveedorRepository.findByNombreContainingIgnoreCase(nombre);
        log.debug("Búsqueda por nombre '{}' encontró {} proveedores.", nombre, proveedores.size());
        return proveedores;
    }

    /**
     * Busca proveedores cuya dirección (ubicación) contenga el texto dado.
     * @param ubicacion Texto a buscar en la dirección.
     * @return Lista de entidades Proveedor coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedoresPorUbicacion(String ubicacion) {
        log.info("Buscando proveedores por ubicación que contenga: '{}'", ubicacion);
        // Asume que buscarPorUbicacion busca en el campo 'direccion'
        List<Proveedor> proveedores = proveedorRepository.buscarPorUbicacion(ubicacion); // Asume método en repo
        log.debug("Búsqueda por ubicación '{}' encontró {} proveedores.", ubicacion, proveedores.size());
        return proveedores;
    }

    /**
     * Lista los proveedores junto con el número de items (productos/materias primas) asociados a cada uno.
     * @return Lista de Object[], donde cada array contiene [Proveedor (entidad), Long (conteo)].
     */
    @Transactional(readOnly = true)
    public List<Object[]> listarProveedoresConProductos() {
        log.info("Listando proveedores con conteo de items asociados...");
        List<Object[]> resultado = proveedorRepository.findProveedoresWithProductCount(); // Asume método en repo
        log.debug("Se obtuvo conteo de items para {} proveedores.", resultado.size());
        return resultado;
    }

    /**
     * Busca un proveedor por su dirección de email exacta.
     * @param email Email a buscar.
     * @return Optional<Proveedor> que contiene el proveedor si se encuentra.
     */
    @Transactional(readOnly = true)
    public Optional<Proveedor> buscarPorEmail(String email) {
        log.info("Buscando proveedor por email: {}", email);
        Optional<Proveedor> proveedorOpt = proveedorRepository.findByEmail(email);
        if(proveedorOpt.isPresent()){
            log.debug("Proveedor encontrado para email {}", email);
        } else {
            log.debug("Proveedor no encontrado para email {}", email);
        }
        return proveedorOpt;
    }

    /**
     * Busca un proveedor por su número de teléfono exacto.
     * @param telefono Teléfono a buscar.
     * @return Optional<Proveedor> que contiene el proveedor si se encuentra.
     */
    @Transactional(readOnly = true)
    public Optional<Proveedor> buscarPorTelefono(String telefono) {
        log.info("Buscando proveedor por teléfono: {}", telefono);
        Optional<Proveedor> proveedorOpt = proveedorRepository.findByTelefono(telefono);
        if(proveedorOpt.isPresent()){
            log.debug("Proveedor encontrado para teléfono {}", telefono);
        } else {
            log.debug("Proveedor no encontrado para teléfono {}", telefono);
        }
        return proveedorOpt;
    }
}