package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.ClienteInterno;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.repository.ClienteInternoRepository;
import com.telastech360.crmTT360.repository.PedidoRepository; // Para verificar pedidos asociados
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para gestionar la lógica de negocio de los Clientes Internos.
 */
@Service
public class ClienteInternoService {

    private static final Logger log = LoggerFactory.getLogger(ClienteInternoService.class);

    private final ClienteInternoRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository; // Para verificar pedidos al eliminar

    /**
     * Constructor para inyección de dependencias.
     * @param clienteRepository Repositorio para Clientes Internos.
     * @param usuarioRepository Repositorio para Usuarios (responsables).
     * @param pedidoRepository Repositorio para Pedidos (verificar asociación).
     */
    @Autowired
    public ClienteInternoService(ClienteInternoRepository clienteRepository,
                                 UsuarioRepository usuarioRepository,
                                 PedidoRepository pedidoRepository) { // Inyectar PedidoRepository
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository; // Asignar PedidoRepository
    }

    /**
     * Crea un nuevo cliente interno.
     * Valida código interno único y existencia del responsable si se especifica.
     * @param cliente Entidad ClienteInterno con los datos a crear.
     * @return La entidad ClienteInterno creada y guardada.
     * @throws DuplicateResourceException Si el código interno ya existe.
     * @throws ResourceNotFoundException Si el usuario responsable no existe.
     */
    @Transactional
    public ClienteInterno crearCliente(ClienteInterno cliente) {
        log.info("Intentando crear cliente interno con código: '{}'", cliente.getCodigoInterno());
        if (clienteRepository.existsByCodigoInterno(cliente.getCodigoInterno())) {
            log.warn("Intento de crear cliente con código interno duplicado: {}", cliente.getCodigoInterno());
            throw new DuplicateResourceException("El código interno ya está registrado: " + cliente.getCodigoInterno());
        }

        // Validar y asignar responsable si se especifica
        if (cliente.getResponsable() != null && cliente.getResponsable().getUsuarioId() != null) {
            Long responsableId = cliente.getResponsable().getUsuarioId();
            log.debug("Buscando usuario responsable con ID: {}", responsableId);
            Usuario responsable = usuarioRepository.findById(responsableId)
                    .orElseThrow(() -> {
                        log.error("Usuario responsable ID {} no encontrado al crear cliente {}", responsableId, cliente.getCodigoInterno());
                        return new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + responsableId);
                    });
            cliente.setResponsable(responsable);
            log.debug("Usuario responsable '{}' asignado al cliente '{}'", responsable.getNombre(), cliente.getCodigoInterno());
        } else {
            cliente.setResponsable(null);
            log.debug("No se especificó responsable para el cliente '{}'", cliente.getCodigoInterno());
        }

        ClienteInterno clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente interno '{}' (Código: {}) creado exitosamente con ID: {}",
                clienteGuardado.getNombre(), clienteGuardado.getCodigoInterno(), clienteGuardado.getClienteId());
        return clienteGuardado;
    }

    /**
     * Obtiene un cliente interno por su ID.
     * @param id ID del cliente a buscar.
     * @return La entidad ClienteInterno encontrada.
     * @throws ResourceNotFoundException si el cliente no existe.
     */
    @Transactional(readOnly = true)
    public ClienteInterno obtenerClientePorId(Long id) {
        log.info("Buscando cliente interno por ID: {}", id);
        ClienteInterno cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cliente interno con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Cliente interno no encontrado con ID: " + id);
                });
        log.debug("Cliente interno encontrado: {} (ID: {})", cliente.getNombre(), id);
        return cliente;
    }

    /**
     * Lista todos los clientes internos registrados, ordenados por nombre.
     * @return Lista de entidades ClienteInterno.
     */
    @Transactional(readOnly = true)
    public List<ClienteInterno> listarTodosLosClientes() {
        log.info("Listando todos los clientes internos...");
        List<ClienteInterno> clientes = clienteRepository.findAllByOrderByNombreAsc();
        log.debug("Se encontraron {} clientes internos.", clientes.size());
        return clientes;
    }

    /**
     * Actualiza un cliente interno existente.
     * @param id ID del cliente a actualizar.
     * @param clienteActualizado Entidad ClienteInterno con los nuevos datos.
     * @return La entidad ClienteInterno actualizada.
     * @throws ResourceNotFoundException Si el cliente o el nuevo responsable no existen.
     * @throws DuplicateResourceException Si el nuevo código interno ya está en uso por otro cliente.
     */
    @Transactional
    public ClienteInterno actualizarCliente(Long id, ClienteInterno clienteActualizado) {
        log.info("Intentando actualizar cliente interno con ID: {}", id);
        ClienteInterno clienteExistente = obtenerClientePorId(id); // Valida existencia

        // Validar código interno único si cambia
        if (!clienteExistente.getCodigoInterno().equalsIgnoreCase(clienteActualizado.getCodigoInterno())) {
            log.debug("El código interno del cliente ID {} ha cambiado a '{}'. Verificando disponibilidad...", id, clienteActualizado.getCodigoInterno());
            if (clienteRepository.existsByCodigoInterno(clienteActualizado.getCodigoInterno())) {
                log.warn("Conflicto: El código interno '{}' ya está en uso por otro cliente.", clienteActualizado.getCodigoInterno());
                throw new DuplicateResourceException("El código interno ya está registrado: " + clienteActualizado.getCodigoInterno());
            }
            log.debug("Código interno '{}' disponible.", clienteActualizado.getCodigoInterno());
            clienteExistente.setCodigoInterno(clienteActualizado.getCodigoInterno());
        }

        // Actualizar otros campos
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setTipo(clienteActualizado.getTipo());
        clienteExistente.setUbicacion(clienteActualizado.getUbicacion());
        clienteExistente.setPresupuestoAnual(clienteActualizado.getPresupuestoAnual());
        log.debug("Campos básicos actualizados para cliente ID {}.", id);

        // Actualizar responsable si se especifica
        Long idResponsableNuevo = (clienteActualizado.getResponsable() != null) ? clienteActualizado.getResponsable().getUsuarioId() : null;
        Long idResponsableActual = (clienteExistente.getResponsable() != null) ? clienteExistente.getResponsable().getUsuarioId() : null;

        if (idResponsableNuevo != idResponsableActual) { // Comparar IDs
            if (idResponsableNuevo != null) {
                log.debug("Actualizando responsable para cliente ID {} al Usuario ID: {}", id, idResponsableNuevo);
                Usuario responsable = usuarioRepository.findById(idResponsableNuevo)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + idResponsableNuevo));
                clienteExistente.setResponsable(responsable);
            } else {
                log.debug("Quitando responsable del cliente ID {}.", id);
                clienteExistente.setResponsable(null);
            }
        }

        ClienteInterno clienteGuardado = clienteRepository.save(clienteExistente);
        log.info("Cliente interno ID {} actualizado exitosamente.", id);
        return clienteGuardado;
    }

    /**
     * Elimina un cliente interno por su ID.
     * Verifica que el cliente no tenga pedidos asociados antes de eliminar.
     * @param id ID del cliente a eliminar.
     * @throws ResourceNotFoundException si el cliente no existe.
     * @throws IllegalOperationException si el cliente tiene pedidos asociados.
     */
    @Transactional
    public void eliminarCliente(Long id) {
        log.info("Intentando eliminar cliente interno con ID: {}", id);
        ClienteInterno cliente = obtenerClientePorId(id); // Valida existencia

        // Validar que no tenga pedidos asociados
        // boolean tienePedidos = pedidoRepository.existsByCliente(cliente); // Necesita método en PedidoRepo
        // Alternativa usando método count del ClienteRepo:
        long conteoPedidos = clienteRepository.countPedidosByClienteId(id); // Asume que existe
        if (conteoPedidos > 0) {
            log.warn("Intento de eliminar cliente ID {} ('{}') que tiene {} pedido(s) asociado(s).", id, cliente.getNombre(), conteoPedidos);
            throw new IllegalOperationException("No se puede eliminar el cliente '" + cliente.getNombre() + "' porque tiene pedidos asociados.");
        }
        log.debug("El cliente ID {} ('{}') no tiene pedidos asociados.", id, cliente.getNombre());

        clienteRepository.delete(cliente);
        log.info("Cliente interno ID {} ('{}') eliminado exitosamente.", id, cliente.getNombre());
    }

    // ========== MÉTODOS ESPECIALIZADOS ========== //

    /**
     * Busca clientes internos cuyo nombre contenga el texto dado (ignorando mayúsculas/minúsculas).
     * @param nombre Texto a buscar en el nombre.
     * @return Lista de entidades ClienteInterno coincidentes.
     */
    @Transactional(readOnly = true)
    public List<ClienteInterno> buscarPorNombre(String nombre) {
        log.info("Buscando clientes internos por nombre que contenga: '{}'", nombre);
        List<ClienteInterno> clientes = clienteRepository.findByNombreContainingIgnoreCase(nombre);
        log.debug("Búsqueda por nombre '{}' encontró {} clientes.", nombre, clientes.size());
        return clientes;
    }

    /**
     * Busca clientes internos por su tipo (INTERNO o EXTERNO).
     * @param tipo El TipoCliente a filtrar.
     * @return Lista de entidades ClienteInterno coincidentes.
     */
    @Transactional(readOnly = true)
    public List<ClienteInterno> buscarPorTipo(ClienteInterno.TipoCliente tipo) {
        log.info("Buscando clientes internos por tipo: {}", tipo);
        List<ClienteInterno> clientes = clienteRepository.findByTipo(tipo);
        log.debug("Se encontraron {} clientes del tipo {}", clientes.size(), tipo);
        return clientes;
    }

    /**
     * Busca clientes internos asignados a un usuario responsable específico.
     * @param responsableId ID del usuario responsable.
     * @return Lista de entidades ClienteInterno asignadas.
     * @throws ResourceNotFoundException si el usuario responsable no existe.
     */
    @Transactional(readOnly = true)
    public List<ClienteInterno> buscarPorResponsable(Long responsableId) {
        log.info("Buscando clientes internos por responsable ID: {}", responsableId);
        // Validar que el usuario exista (aunque findByResponsableId ya lo haría implícitamente)
        if (!usuarioRepository.existsById(responsableId)) {
            log.warn("Usuario responsable con ID {} no encontrado al buscar clientes.", responsableId);
            throw new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + responsableId);
        }
        List<ClienteInterno> clientes = clienteRepository.findByResponsableId(responsableId); // Asume método en repo
        log.debug("Se encontraron {} clientes para el responsable ID {}", clientes.size(), responsableId);
        return clientes;
    }

    /**
     * Busca clientes internos cuyo presupuesto anual sea mayor o igual al monto especificado.
     * @param monto Monto mínimo del presupuesto a buscar.
     * @return Lista de entidades ClienteInterno coincidentes.
     */
    @Transactional(readOnly = true)
    public List<ClienteInterno> buscarPorPresupuestoMayorQue(BigDecimal monto) {
        log.info("Buscando clientes internos con presupuesto anual >= {}", monto);
        // El repositorio espera Double, hacemos la conversión
        List<ClienteInterno> clientes = clienteRepository.findByPresupuestoAnualMayorQue(monto.doubleValue());
        log.debug("Se encontraron {} clientes con presupuesto >= {}", clientes.size(), monto);
        return clientes;
    }

    /**
     * Verifica si existe un cliente interno con el código dado.
     * @param codigo Código interno a verificar.
     * @return true si existe, false en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existeClienteConCodigo(String codigo) {
        log.debug("Verificando existencia de cliente con código interno: '{}'", codigo);
        return clienteRepository.existsByCodigoInterno(codigo);
    }

    /**
     * Obtiene un resumen de clientes agrupados por tipo, incluyendo conteo y presupuesto promedio.
     * @return Lista de Object[], donde cada array contiene [TipoCliente, Long (conteo), Double (presupuesto promedio)].
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerResumenPorTipo() {
        log.info("Obteniendo resumen de clientes por tipo...");
        List<Object[]> resumen = clienteRepository.getResumenClientesPorTipo();
        log.debug("Resumen de clientes por tipo obtenido con {} entradas.", resumen.size());
        return resumen;
    }
}