package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.exception.*;
import com.telastech360.crmTT360.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final ClienteInternoRepository clienteRepository;
    private final EstadoRepository estadoRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoDetalleRepository pedidoDetalleRepository,
                         ClienteInternoRepository clienteRepository,
                         EstadoRepository estadoRepository,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoDetalleRepository = pedidoDetalleRepository;
        this.clienteRepository = clienteRepository;
        this.estadoRepository = estadoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        if (pedido.getCliente() != null) {
            ClienteInterno cliente = clienteRepository.findById(pedido.getCliente().getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
            pedido.setCliente(cliente);
        }

        Estado estado = estadoRepository.findById(pedido.getEstado().getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado"));
        pedido.setEstado(estado);

        pedido.setFechaPedido(new Timestamp(System.currentTimeMillis()));

        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {
        Pedido pedidoExistente = obtenerPedidoPorId(id);

        pedidoExistente.setEstado(estadoRepository.findById(pedidoActualizado.getEstado().getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado")));

        if (pedidoActualizado.getCliente() != null) {
            pedidoExistente.setCliente(clienteRepository.findById(pedidoActualizado.getCliente().getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado")));
        } else {
            pedidoExistente.setCliente(null);
        }

        return pedidoRepository.save(pedidoExistente);
    }

    @Transactional
    public void eliminarPedido(Long id) {
        Pedido pedido = obtenerPedidoPorId(id);

        if (!pedido.getFacturas().isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar el pedido porque tiene facturas asociadas");
        }

        pedidoDetalleRepository.deleteByPedido(pedido);
        pedidoRepository.delete(pedido);
    }

    @Transactional
    public void agregarDetalle(Long pedidoId, PedidoDetalle detalle) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);
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

        pedidoDetalleRepository.save(nuevoDetalle);
    }

    @Transactional
    public void eliminarDetalle(Long pedidoId, Long itemId) {
        PedidoDetalle detalle = pedidoDetalleRepository.findByPedido_PedidoIdAndProducto_ItemId(pedidoId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado"));

        pedidoDetalleRepository.delete(detalle);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPedidosPorCliente(Long clienteId) {
        ClienteInterno cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        return pedidoRepository.findByCliente(cliente);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPedidosPorEstado(String estadoValor) {
        return pedidoRepository.findByEstado_Valor(estadoValor);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPedidosUrgentes() {
        return pedidoRepository.findPedidosUrgentes();
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(Long pedidoId) {
        List<PedidoDetalle> detalles = pedidoDetalleRepository.findByPedido_PedidoId(pedidoId);
        return detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
