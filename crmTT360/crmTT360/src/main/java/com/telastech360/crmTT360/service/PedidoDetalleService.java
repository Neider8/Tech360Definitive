// src/main/java/com/telastech360/crmTT360/service/PedidoDetalleService.java
package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Item;
import com.telastech360.crmTT360.entity.Pedido;
import com.telastech360.crmTT360.entity.PedidoDetalle;
import com.telastech360.crmTT360.entity.PedidoDetalle.PedidoDetalleId;
import com.telastech360.crmTT360.exception.*;
import com.telastech360.crmTT360.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoDetalleService {

    private static final Logger log = LoggerFactory.getLogger(PedidoDetalleService.class);

    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final PedidoRepository pedidoRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Autowired
    public PedidoDetalleService(
            PedidoDetalleRepository pedidoDetalleRepository,
            PedidoRepository pedidoRepository,
            ItemRepository itemRepository,
            @Lazy ItemService itemService) {
        this.pedidoDetalleRepository = pedidoDetalleRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    @Transactional(readOnly = true)
    public List<PedidoDetalle> listarTodosLosDetalles() {
        log.info("Listando todos los detalles de pedidos...");
        List<PedidoDetalle> detalles = pedidoDetalleRepository.findAll();
        log.debug("Se encontraron {} detalles de pedido en total.", detalles.size());
        return detalles;
    }

    @Transactional(readOnly = true)
    public PedidoDetalle obtenerDetallePorId(Long pedidoId, Long itemId) {
        log.info("Buscando detalle de pedido por ID compuesto: Pedido={}, Item={}", pedidoId, itemId);
        PedidoDetalleId id = new PedidoDetalleId(pedidoId, itemId);
        PedidoDetalle detalle = pedidoDetalleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Detalle de pedido no encontrado para Pedido ID {} e Item ID {}", pedidoId, itemId);
                    return new ResourceNotFoundException("Detalle de pedido no encontrado para Pedido ID " + pedidoId + " e Item ID " + itemId);
                });
        log.debug("Detalle de pedido encontrado para Pedido ID {} e Item ID {}", pedidoId, itemId);
        return detalle;
    }

    @Transactional
    public PedidoDetalle agregarDetalle(Long pedidoId, PedidoDetalle detalle) {
        Long itemId = (detalle.getProducto() != null) ? detalle.getProducto().getItemId() : null;
        log.info("Agregando detalle al pedido ID: {}. Item ID: {}, Cantidad: {}",
                pedidoId, itemId, detalle.getCantidad());

        if (itemId == null) {
            throw new IllegalArgumentException("El Item ID es obligatorio en el detalle.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado con ID: " + itemId));

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new IllegalOperationException("La cantidad debe ser mayor a cero.");
        }
        if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalOperationException("El precio unitario debe ser mayor a cero.");
        }

        // << --- Validación de Stock (Tarea 4) --- >>
        log.debug("Verificando stock disponible para Item ID {}", itemId);
        if (item.getStockDisponible() < detalle.getCantidad()) {
            log.error("Stock insuficiente al agregar detalle. Item ID: {}, Nombre: {}, Disponible: {}, Solicitado: {}",
                    item.getItemId(), item.getNombre(), item.getStockDisponible(), detalle.getCantidad());
            throw new IllegalOperationException("Stock insuficiente para el ítem '" + item.getNombre() +
                    "'. Disponible: " + item.getStockDisponible() +
                    ", Solicitado: " + detalle.getCantidad());
        }
        log.debug("Stock suficiente verificado para Item ID {}.", itemId);

        // Ajustar stock (Tarea 1)
        log.debug("Ajustando stock para Item ID {} antes de guardar detalle.", itemId);
        itemService.ajustarStock(itemId, -detalle.getCantidad());
        log.debug("Stock disminuido exitosamente para Item ID {}.", itemId);

        // Crear y guardar el nuevo detalle
        PedidoDetalle nuevoDetalle = new PedidoDetalle();
        nuevoDetalle.setPedido(pedido);
        nuevoDetalle.setProducto(item);
        nuevoDetalle.setCantidad(detalle.getCantidad());
        nuevoDetalle.setPrecioUnitario(detalle.getPrecioUnitario());

        PedidoDetalle detalleGuardado = pedidoDetalleRepository.save(nuevoDetalle);
        log.info("Detalle agregado exitosamente al pedido ID {}. Item ID: {}, Cantidad: {}",
                pedidoId, itemId, detalleGuardado.getCantidad());

        return detalleGuardado;
    }

    @Transactional
    public PedidoDetalle actualizarDetalle(Long pedidoId, Long itemId, PedidoDetalle detalleActualizado) {
        log.info("Actualizando detalle para Pedido ID {} e Item ID {}", pedidoId, itemId);
        PedidoDetalle detalleExistente = obtenerDetallePorId(pedidoId, itemId);

        Integer cantidadAnterior = detalleExistente.getCantidad();
        Integer cantidadNueva = detalleActualizado.getCantidad();
        int diferenciaCantidad = 0;
        boolean cantidadCambiada = false;

        if (cantidadNueva != null && !cantidadNueva.equals(cantidadAnterior)) {
            if (cantidadNueva <= 0) {
                throw new IllegalOperationException("La cantidad debe ser mayor a cero.");
            }
            diferenciaCantidad = cantidadNueva - cantidadAnterior;
            cantidadCambiada = true;
            log.debug("Cantidad para detalle (P:{}, I:{}) cambia de {} a {}. Diferencia neta: {}",
                    pedidoId, itemId, cantidadAnterior, cantidadNueva, diferenciaCantidad);
        }

        if (cantidadCambiada) {
            int ajusteStock = -diferenciaCantidad;
            log.debug("Ajustando stock para Item ID {} por diferencia de cantidad. Ajuste a aplicar: {}", itemId, ajusteStock);
            itemService.ajustarStock(itemId, ajusteStock);
            log.debug("Stock ajustado correctamente para Item ID {}.", itemId);
            detalleExistente.setCantidad(cantidadNueva);
        }

        boolean precioCambiado = false;
        if (detalleActualizado.getPrecioUnitario() != null) {
            if (detalleActualizado.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalOperationException("El precio unitario debe ser mayor a cero.");
            }
            if (detalleExistente.getPrecioUnitario().compareTo(detalleActualizado.getPrecioUnitario()) != 0) {
                log.debug("Precio para detalle (P:{}, I:{}) cambia de {} a {}",
                        pedidoId, itemId, detalleExistente.getPrecioUnitario(), detalleActualizado.getPrecioUnitario());
                detalleExistente.setPrecioUnitario(detalleActualizado.getPrecioUnitario());
                precioCambiado = true;
            }
        }

        if (cantidadCambiada || precioCambiado) {
            PedidoDetalle detalleGuardado = pedidoDetalleRepository.save(detalleExistente);
            log.info("Detalle (P:{}, I:{}) actualizado exitosamente.", pedidoId, itemId);
            return detalleGuardado;
        } else {
            log.info("No se realizaron cambios detectables en el detalle (P:{}, I:{}).", pedidoId, itemId);
            return detalleExistente;
        }
    }

    @Transactional
    public void eliminarDetalle(Long pedidoId, Long itemId) {
        log.info("Intentando eliminar detalle para Pedido ID {} e Item ID {}", pedidoId, itemId);
        PedidoDetalle detalle = obtenerDetallePorId(pedidoId, itemId);

        Integer cantidadEliminada = detalle.getCantidad();
        Long idItemARestaurar = detalle.getProducto().getItemId();

        pedidoDetalleRepository.delete(detalle);
        log.debug("Detalle (P:{}, I:{}) eliminado de la base de datos.", pedidoId, itemId);

        log.debug("Restaurando stock para Item ID {} en {} unidades.", idItemARestaurar, cantidadEliminada);
        itemService.ajustarStock(idItemARestaurar, cantidadEliminada);
        log.debug("Stock restaurado para Item ID {}.", idItemARestaurar);

        log.info("Detalle (P:{}, I:{}) eliminado exitosamente y stock restaurado.", pedidoId, itemId);
    }

    @Transactional(readOnly = true)
    public List<PedidoDetalle> listarDetallesPorPedido(Long pedidoId) {
        log.info("Listando detalles para el pedido ID: {}", pedidoId);
        if (!pedidoRepository.existsById(pedidoId)) {
            log.warn("Pedido ID {} no encontrado al listar sus detalles.", pedidoId);
            throw new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId);
        }
        List<PedidoDetalle> detalles = pedidoDetalleRepository.findByPedido_PedidoId(pedidoId);
        log.debug("Se encontraron {} detalles para el pedido ID {}", detalles.size(), pedidoId);
        return detalles;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSubtotalDetalle(Long pedidoId, Long itemId) {
        log.info("Calculando subtotal para detalle (P:{}, I:{})", pedidoId, itemId);
        PedidoDetalle detalle = obtenerDetallePorId(pedidoId, itemId);
        BigDecimal subtotal = detalle.getSubtotal();
        log.debug("Subtotal calculado para detalle (P:{}, I:{}): {}", pedidoId, itemId, subtotal);
        return subtotal;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(Long pedidoId) {
        log.info("Calculando total para el pedido ID: {}", pedidoId);
        List<PedidoDetalle> detalles = listarDetallesPorPedido(pedidoId);
        BigDecimal total = detalles.stream()
                .map(PedidoDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.debug("Total calculado para pedido ID {}: {}", pedidoId, total);
        return total;
    }
}