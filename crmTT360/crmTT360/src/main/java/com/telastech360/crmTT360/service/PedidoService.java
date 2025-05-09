// src/main/java/com/telastech360/crmTT360/service/PedidoService.java
package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.PedidoDTO;
import com.telastech360.crmTT360.dto.PedidoDetalleDTO;
import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.exception.*;
import com.telastech360.crmTT360.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar la lógica de negocio relacionada con los Pedidos.
 */
@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final ClienteInternoRepository clienteRepository;
    private final EstadoRepository estadoRepository;
    private final ItemRepository itemRepository;
    private final FacturaRepository facturaRepository;
    private final ItemService itemService;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoDetalleRepository pedidoDetalleRepository,
                         ClienteInternoRepository clienteRepository,
                         EstadoRepository estadoRepository,
                         ItemRepository itemRepository,
                         FacturaRepository facturaRepository,
                         @Lazy ItemService itemService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoDetalleRepository = pedidoDetalleRepository;
        this.clienteRepository = clienteRepository;
        this.estadoRepository = estadoRepository;
        this.itemRepository = itemRepository;
        this.facturaRepository = facturaRepository;
        this.itemService = itemService;
    }

    /**
     * Crea un nuevo pedido junto con sus detalles.
     * Valida stock ANTES de crear y disminuye el stock DESPUÉS de guardar.
     * @param pedidoDto DTO con la información del pedido y detalles.
     * @return La entidad Pedido creada y guardada.
     * @throws InvalidDataException Si no hay detalles o algún dato es inválido.
     * @throws ResourceNotFoundException Si cliente, estado o ítem no existen.
     * @throws IllegalOperationException Si no hay stock suficiente para algún ítem.
     */
    @Transactional
    public Pedido crearPedidoConDetalles(PedidoDTO pedidoDto) {
        log.info("Intentando crear pedido para Cliente ID: {} con Estado ID: {}", pedidoDto.getClienteId(), pedidoDto.getEstadoId());

        // --- Validación de Cliente y Estado ---
        ClienteInterno cliente = null;
        if (pedidoDto.getClienteId() != null) {
            cliente = clienteRepository.findById(pedidoDto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + pedidoDto.getClienteId()));
        }
        Estado estado = estadoRepository.findById(pedidoDto.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + pedidoDto.getEstadoId()));

        // --- Validación de Detalles y Stock (ANTES de crear) ---
        if (pedidoDto.getDetalles() == null || pedidoDto.getDetalles().isEmpty()) {
            throw new InvalidDataException("El pedido debe contener al menos un detalle.");
        }
        log.debug("Verificando stock para {} detalles...", pedidoDto.getDetalles().size());
        for (PedidoDetalleDTO detalleDto : pedidoDto.getDetalles()) {
            if (detalleDto.getItemId() == null || detalleDto.getCantidad() == null || detalleDto.getCantidad() <= 0 || detalleDto.getPrecioUnitario() == null || detalleDto.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidDataException("Detalle inválido: " + detalleDto); // Mejorar mensaje si es posible
            }
            Item item = itemRepository.findById(detalleDto.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado con ID: " + detalleDto.getItemId()));

            // << --- Validación de Stock (Tarea 4) --- >>
            if (item.getStockDisponible() < detalleDto.getCantidad()) {
                log.error("Stock insuficiente para crear pedido. Item ID: {}, Nombre: {}, Disponible: {}, Solicitado: {}",
                        item.getItemId(), item.getNombre(), item.getStockDisponible(), detalleDto.getCantidad());
                throw new IllegalOperationException("Stock insuficiente para el ítem '" + item.getNombre() +
                        "'. Disponible: " + item.getStockDisponible() +
                        ", Solicitado: " + detalleDto.getCantidad());
            }
            log.trace("Stock verificado OK para Item ID {}", item.getItemId());
        }
        log.debug("Verificación de stock completada.");

        // --- Crear Pedido y Detalles ---
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEstado(estado);
        pedido.setFechaPedido(new Timestamp(System.currentTimeMillis()));
        pedido.setDetalles(new ArrayList<>());

        for (PedidoDetalleDTO detalleDto : pedidoDto.getDetalles()) {
            Item item = itemRepository.findById(detalleDto.getItemId()).get(); // Sabemos que existe
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setProducto(item);
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setPrecioUnitario(detalleDto.getPrecioUnitario());
            pedido.agregarDetalle(detalle);
        }

        // Guardar Pedido CON sus detalles (CASCADE)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.debug("Pedido ID {} y sus detalles guardados en BD.", pedidoGuardado.getPedidoId());

        // --- Ajuste de Stock (DESPUÉS de guardar) ---
        log.debug("Ajustando stock para los ítems del pedido ID {}...", pedidoGuardado.getPedidoId());
        for (PedidoDetalle detalleGuardado : pedidoGuardado.getDetalles()) {
            try {
                itemService.ajustarStock(detalleGuardado.getProducto().getItemId(), -detalleGuardado.getCantidad());
            } catch (Exception e) {
                log.error("Error INESPERADO al ajustar stock para Item ID {} después de guardar pedido {}: {} - ¡ROLLBACK!",
                        detalleGuardado.getProducto().getItemId(), pedidoGuardado.getPedidoId(), e.getMessage());
                // La transacción hará rollback automáticamente al lanzar la excepción no capturada
                throw e;
            }
        }
        log.debug("Ajuste de stock completado para pedido ID {}.", pedidoGuardado.getPedidoId());

        log.info("Pedido creado exitosamente con ID: {} para cliente ID: {}", pedidoGuardado.getPedidoId(), pedidoDto.getClienteId());
        return pedidoGuardado;
    }


    /**
     * Elimina un pedido por su ID.
     * Restaura el stock de los ítems asociados al pedido.
     * @param id ID del pedido a eliminar.
     * @throws ResourceNotFoundException si el pedido no existe.
     * @throws IllegalOperationException si el pedido tiene facturas asociadas.
     */
    @Transactional
    public void eliminarPedido(Long id) {
        log.info("Intentando eliminar pedido con ID: {}", id);
        Pedido pedido = pedidoRepository.findByIdWithDetails(id) // Cargar detalles EAGERLY
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        boolean tieneFacturas = facturaRepository.existsByPedidoId(id);
        if (tieneFacturas) {
            log.warn("Intento de eliminar pedido ID {} que tiene facturas asociadas.", id);
            throw new IllegalOperationException("No se puede eliminar el pedido ID " + id + " porque tiene facturas asociadas.");
        }

        // --- Restaurar Stock (ANTES de eliminar) ---
        log.debug("Restaurando stock para los ítems del pedido ID {} antes de eliminar...", id);
        if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                try {
                    itemService.ajustarStock(detalle.getProducto().getItemId(), detalle.getCantidad()); // Cantidad positiva
                    log.trace("Stock restaurado para Item ID {} (+{})", detalle.getProducto().getItemId(), detalle.getCantidad());
                } catch (Exception e) {
                    log.error("Error al restaurar stock para Item ID {} al eliminar pedido {}: {} - ¡ROLLBACK!",
                            detalle.getProducto().getItemId(), id, e.getMessage());
                    throw new RuntimeException("Fallo al restaurar stock durante eliminación de pedido.", e); // Forzar rollback
                }
            }
            log.debug("Restauración de stock completada para ítems del pedido ID {}.", id);
        } else {
            log.debug("Pedido ID {} no tenía detalles, no se restaura stock.", id);
        }

        // Eliminar el pedido (CASCADE debería eliminar los detalles)
        pedidoRepository.delete(pedido);
        log.info("Pedido ID {} y sus detalles eliminados exitosamente.", id);
    }


    // --- Otros métodos del servicio (obtenerPedidoPorId, listarTodosLosPedidos, actualizarPedido, etc.) ---
    // (Incluir el resto de métodos como en la respuesta anterior)
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(Long id) {
        log.info("Buscando pedido por ID con detalles: {}", id);
        Pedido pedido = pedidoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> {
                    log.warn("Pedido con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + id);
                });
        log.debug("Pedido encontrado con detalles: ID {}", id);
        return pedido;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodosLosPedidos() {
        log.info("Listando todos los pedidos...");
        List<Pedido> pedidos = pedidoRepository.findAll();
        log.debug("Se encontraron {} pedidos.", pedidos.size());
        return pedidos;
    }

    @Transactional
    public Pedido actualizarPedido(Long id, PedidoDTO pedidoDto) {
        log.info("Intentando actualizar información principal del pedido ID: {}", id);
        Pedido pedidoExistente = obtenerPedidoPorId(id);

        Long clienteIdDto = pedidoDto.getClienteId();
        Long clienteIdActual = (pedidoExistente.getCliente() != null) ? pedidoExistente.getCliente().getClienteId() : null;

        if (clienteIdDto != null) {
            if (!clienteIdDto.equals(clienteIdActual)) {
                ClienteInterno clienteNuevo = clienteRepository.findById(clienteIdDto)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteIdDto));
                pedidoExistente.setCliente(clienteNuevo);
            }
        } else {
            if (clienteIdActual != null) {
                pedidoExistente.setCliente(null);
            }
        }

        Long estadoIdDto = pedidoDto.getEstadoId();
        if (estadoIdDto == null) {
            throw new IllegalArgumentException("El ID del estado es obligatorio para actualizar un pedido.");
        }
        Long estadoIdActual = pedidoExistente.getEstado().getEstadoId();

        if (!estadoIdDto.equals(estadoIdActual)) {
            Estado estadoNuevo = estadoRepository.findById(estadoIdDto)
                    .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + estadoIdDto));
            pedidoExistente.setEstado(estadoNuevo);
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedidoExistente);
        log.info("Información principal del pedido ID {} actualizada exitosamente.", id);
        return pedidoGuardado;
    }


    @Transactional(readOnly = true)
    public List<Pedido> buscarPedidosPorCliente(Long clienteId) {
        log.info("Buscando pedidos para el cliente ID: {}", clienteId);
        ClienteInterno cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));
        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);
        log.debug("Se encontraron {} pedidos para el cliente ID {}", pedidos.size(), clienteId);
        return pedidos;
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPedidosPorEstado(String estadoValor) {
        log.info("Buscando pedidos con estado: '{}'", estadoValor);
        List<Pedido> pedidos = pedidoRepository.findByEstado_Valor(estadoValor);
        log.debug("Se encontraron {} pedidos con estado '{}'", pedidos.size(), estadoValor);
        return pedidos;
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPedidosUrgentes() {
        log.info("Buscando pedidos urgentes...");
        List<Pedido> pedidos = pedidoRepository.findPedidosUrgentes();
        log.debug("Se encontraron {} pedidos urgentes.", pedidos.size());
        return pedidos;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(Long pedidoId) {
        log.info("Calculando total para pedido ID: {}", pedidoId);
        Pedido pedido = obtenerPedidoPorId(pedidoId);

        BigDecimal total = BigDecimal.ZERO;
        if (pedido.getDetalles() != null) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                total = total.add(detalle.getSubtotal());
            }
        }
        log.debug("Total calculado para pedido ID {}: {}", pedidoId, total);
        return total;
    }
}