package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Pedido;
import com.telastech360.crmTT360.entity.PedidoDetalle;
import com.telastech360.crmTT360.entity.Producto;
import com.telastech360.crmTT360.exception.*;
import com.telastech360.crmTT360.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoDetalleService {

    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public PedidoDetalleService(
            PedidoDetalleRepository pedidoDetalleRepository,
            PedidoRepository pedidoRepository,
            ProductoRepository productoRepository) {
        this.pedidoDetalleRepository = pedidoDetalleRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<PedidoDetalle> listarTodosLosDetalles() {
        return pedidoDetalleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PedidoDetalle obtenerDetallePorId(Long pedidoId, Long itemId) {
        return pedidoDetalleRepository.findById(new PedidoDetalle.PedidoDetalleId(pedidoId, itemId))
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado"));
    }

    @Transactional
    public PedidoDetalle agregarDetalle(Long pedidoId, PedidoDetalle detalle) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        Producto producto = productoRepository.findById(detalle.getProducto().getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (detalle.getCantidad() <= 0) {
            throw new IllegalOperationException("La cantidad debe ser mayor a cero");
        }

        if (detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalOperationException("El precio unitario debe ser mayor a cero");
        }

        PedidoDetalle nuevoDetalle = new PedidoDetalle();
        nuevoDetalle.setPedido(pedido);
        nuevoDetalle.setProducto(producto);
        nuevoDetalle.setCantidad(detalle.getCantidad());
        nuevoDetalle.setPrecioUnitario(detalle.getPrecioUnitario());

        return pedidoDetalleRepository.save(nuevoDetalle);
    }

    @Transactional
    public PedidoDetalle actualizarDetalle(Long pedidoId, Long itemId, PedidoDetalle detalleActualizado) {
        PedidoDetalle detalleExistente = obtenerDetallePorId(pedidoId, itemId);

        if (detalleActualizado.getCantidad() != null && detalleActualizado.getCantidad() > 0) {
            detalleExistente.setCantidad(detalleActualizado.getCantidad());
        }

        if (detalleActualizado.getPrecioUnitario() != null && detalleActualizado.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0) {
            detalleExistente.setPrecioUnitario(detalleActualizado.getPrecioUnitario());
        }

        return pedidoDetalleRepository.save(detalleExistente);
    }

    @Transactional
    public void eliminarDetalle(Long pedidoId, Long itemId) {
        PedidoDetalle detalle = obtenerDetallePorId(pedidoId, itemId);
        pedidoDetalleRepository.delete(detalle);
    }

    @Transactional(readOnly = true)
    public List<PedidoDetalle> listarDetallesPorPedido(Long pedidoId) {
        return pedidoDetalleRepository.findByPedido_PedidoId(pedidoId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSubtotalDetalle(Long pedidoId, Long itemId) {
        PedidoDetalle detalle = obtenerDetallePorId(pedidoId, itemId);
        return detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(Long pedidoId) {
        List<PedidoDetalle> detalles = listarDetallesPorPedido(pedidoId);
        return detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
