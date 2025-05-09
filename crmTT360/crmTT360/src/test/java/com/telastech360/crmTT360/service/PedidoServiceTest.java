package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.PedidoDTO;
import com.telastech360.crmTT360.dto.PedidoDetalleDTO;
import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.exception.InvalidDataException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ClienteInternoRepository clienteRepository;
    @Mock private EstadoRepository estadoRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private PedidoDetalleRepository pedidoDetalleRepository;
    @Mock private ItemService itemService; // <<<--- Mock añadido
    @Mock private FacturaRepository facturaRepository; // Añadir si se usa en los tests

    @InjectMocks
    private PedidoService pedidoService;

    private PedidoDTO pedidoDto;
    private ClienteInterno cliente;
    private Estado estadoPendiente;
    private Item itemProducto;
    private Pedido pedidoGuardado;

    @BeforeEach
    void setUp() {
        cliente = new ClienteInterno();
        cliente.setClienteId(10L);
        cliente.setNombre("Cliente Prueba");

        estadoPendiente = new Estado(Estado.TipoEstado.PEDIDO, "Pendiente");
        estadoPendiente.setEstadoId(1L);

        itemProducto = new Producto(); // Asumiendo que Item es Producto
        itemProducto.setItemId(101L);
        itemProducto.setNombre("Camisa Test");
        itemProducto.setStockDisponible(50); // Stock suficiente inicial

        PedidoDetalleDTO detalleDto = new PedidoDetalleDTO(itemProducto.getItemId(), 5, new BigDecimal("25.50"));

        pedidoDto = new PedidoDTO();
        pedidoDto.setClienteId(cliente.getClienteId());
        pedidoDto.setEstadoId(estadoPendiente.getEstadoId());
        pedidoDto.setDetalles(List.of(detalleDto));

        // Simulación de pedido guardado
        pedidoGuardado = new Pedido(cliente, estadoPendiente);
        pedidoGuardado.setPedidoId(1L);
        PedidoDetalle detalleGuardado = new PedidoDetalle(pedidoGuardado, itemProducto, 5, new BigDecimal("25.50"));
        // Asignar ID compuesto simulado
        detalleGuardado.setId(new PedidoDetalle.PedidoDetalleId(pedidoGuardado.getPedidoId(), itemProducto.getItemId()));
        pedidoGuardado.getDetalles().add(detalleGuardado); // Añadir a la lista

    }

    @Test
    @DisplayName("Crear Pedido - Éxito")
    void crearPedidoConDetalles_Exito() {
        // Arrange
        when(clienteRepository.findById(pedidoDto.getClienteId())).thenReturn(Optional.of(cliente));
        when(estadoRepository.findById(pedidoDto.getEstadoId())).thenReturn(Optional.of(estadoPendiente));
        when(itemRepository.findById(itemProducto.getItemId())).thenReturn(Optional.of(itemProducto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setPedidoId(1L);
            if (p.getDetalles() != null) {
                p.getDetalles().forEach(d -> d.setId(new PedidoDetalle.PedidoDetalleId(p.getPedidoId(), d.getProducto().getItemId())));
            }
            return p;
        });
        doNothing().when(itemService).ajustarStock(anyLong(), anyInt());

        // Act
        Pedido resultado = pedidoService.crearPedidoConDetalles(pedidoDto);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getPedidoId());
        assertEquals(cliente, resultado.getCliente());
        assertEquals(estadoPendiente, resultado.getEstado());
        assertFalse(resultado.getDetalles().isEmpty());
        assertEquals(1, resultado.getDetalles().size());
        assertEquals(itemProducto.getItemId(), resultado.getDetalles().get(0).getProducto().getItemId());
        assertEquals(5, resultado.getDetalles().get(0).getCantidad());

        verify(clienteRepository).findById(pedidoDto.getClienteId());
        verify(estadoRepository).findById(pedidoDto.getEstadoId());
        verify(itemRepository, times(2)).findById(itemProducto.getItemId());
        verify(pedidoRepository).save(any(Pedido.class));
        verify(itemService, times(1)).ajustarStock(eq(itemProducto.getItemId()), eq(-5));
    }

    @Test
    @DisplayName("Crear Pedido - Stock Insuficiente")
    void crearPedidoConDetalles_StockInsuficiente() {
        // Arrange
        itemProducto.setStockDisponible(3); // Stock insuficiente
        when(clienteRepository.findById(pedidoDto.getClienteId())).thenReturn(Optional.of(cliente));
        when(estadoRepository.findById(pedidoDto.getEstadoId())).thenReturn(Optional.of(estadoPendiente));
        when(itemRepository.findById(itemProducto.getItemId())).thenReturn(Optional.of(itemProducto));

        // Act & Assert
        IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> {
            pedidoService.crearPedidoConDetalles(pedidoDto);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente para el ítem"));
        verify(itemRepository).findById(itemProducto.getItemId());
        verify(pedidoRepository, never()).save(any(Pedido.class));
        verify(itemService, never()).ajustarStock(anyLong(), anyInt());
    }

    // ... (otros tests de PedidoServiceTest) ...
    @Test
    @DisplayName("Crear Pedido - Cliente No Encontrado")
    void crearPedidoConDetalles_ClienteNoEncontrado() {
        when(clienteRepository.findById(pedidoDto.getClienteId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            pedidoService.crearPedidoConDetalles(pedidoDto);
        });

        assertEquals("Cliente no encontrado con ID: " + pedidoDto.getClienteId(), exception.getMessage());
        verify(clienteRepository).findById(pedidoDto.getClienteId());
        verifyNoInteractions(estadoRepository, itemRepository, pedidoRepository, itemService);
    }

    @Test
    @DisplayName("Crear Pedido - Estado No Encontrado")
    void crearPedidoConDetalles_EstadoNoEncontrado() {
        when(clienteRepository.findById(pedidoDto.getClienteId())).thenReturn(Optional.of(cliente));
        when(estadoRepository.findById(pedidoDto.getEstadoId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            pedidoService.crearPedidoConDetalles(pedidoDto);
        });

        assertEquals("Estado no encontrado con ID: " + pedidoDto.getEstadoId(), exception.getMessage());
        verify(estadoRepository).findById(pedidoDto.getEstadoId());
        verifyNoInteractions(itemRepository, pedidoRepository, itemService);
    }

    @Test
    @DisplayName("Crear Pedido - Item No Encontrado")
    void crearPedidoConDetalles_ItemNoEncontrado() {
        when(clienteRepository.findById(pedidoDto.getClienteId())).thenReturn(Optional.of(cliente));
        when(estadoRepository.findById(pedidoDto.getEstadoId())).thenReturn(Optional.of(estadoPendiente));
        when(itemRepository.findById(itemProducto.getItemId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            pedidoService.crearPedidoConDetalles(pedidoDto);
        });

        assertEquals("Ítem no encontrado con ID: " + itemProducto.getItemId(), exception.getMessage());
        verify(itemRepository).findById(itemProducto.getItemId());
        verifyNoInteractions(pedidoRepository, itemService);
    }

    @Test
    @DisplayName("Crear Pedido - Sin Detalles")
    void crearPedidoConDetalles_SinDetalles() {
        pedidoDto.setDetalles(new ArrayList<>());
        when(clienteRepository.findById(pedidoDto.getClienteId())).thenReturn(Optional.of(cliente));
        when(estadoRepository.findById(pedidoDto.getEstadoId())).thenReturn(Optional.of(estadoPendiente));

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            pedidoService.crearPedidoConDetalles(pedidoDto);
        });

        assertEquals("El pedido debe contener al menos un detalle.", exception.getMessage());
        verifyNoInteractions(itemRepository, pedidoRepository, itemService);
    }
}