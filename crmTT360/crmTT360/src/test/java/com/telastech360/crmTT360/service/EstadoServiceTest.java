package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.EstadoDTO;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Estado.TipoEstado;
import com.telastech360.crmTT360.exception.DuplicateResourceException; // Asegurar import
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.UsedStateException; // Asegurar import
import com.telastech360.crmTT360.mapper.EstadoMapper;
import com.telastech360.crmTT360.repository.BodegaRepository;
import com.telastech360.crmTT360.repository.EstadoRepository;
import com.telastech360.crmTT360.repository.ItemRepository;
import com.telastech360.crmTT360.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para EstadoService.
 */
@ExtendWith(MockitoExtension.class)
class EstadoServiceTest {

    @Mock private EstadoRepository estadoRepository;
    @Mock private BodegaRepository bodegaRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private EstadoMapper estadoMapper; // Mockear el mapper también

    @InjectMocks
    private EstadoService estadoService;

    private Estado estadoActivoItem;
    private Estado estadoPendientePedido;
    private EstadoDTO estadoDtoActivo;
    private EstadoDTO estadoDtoNuevo;

    @BeforeEach
    void setUp() {
        estadoActivoItem = new Estado(TipoEstado.ITEM, "Activo");
        estadoActivoItem.setEstadoId(1L);

        estadoPendientePedido = new Estado(TipoEstado.PEDIDO, "Pendiente");
        estadoPendientePedido.setEstadoId(2L);

        estadoDtoActivo = new EstadoDTO(estadoActivoItem.getTipoEstado(), estadoActivoItem.getValor());
        estadoDtoNuevo = new EstadoDTO(TipoEstado.ITEM, "Descatalogado");
    }

    @Test
    @DisplayName("Eliminar Estado - Éxito")
    void eliminarEstado_Exito() {
        // Arrange
        Long estadoIdAEliminar = estadoPendientePedido.getEstadoId();
        when(estadoRepository.findById(estadoIdAEliminar)).thenReturn(Optional.of(estadoPendientePedido));
        // Simular que el estado NO está en uso
        when(bodegaRepository.existsByEstadoId(estadoIdAEliminar)).thenReturn(false);
        when(itemRepository.existsByEstadoId(estadoIdAEliminar)).thenReturn(false);
        when(pedidoRepository.existsByEstadoId(estadoIdAEliminar)).thenReturn(false);
        // Configurar para que deleteById no lance excepción (comportamiento por defecto de void)
        doNothing().when(estadoRepository).delete(any(Estado.class));

        // Act
        assertDoesNotThrow(() -> {
            estadoService.eliminarEstado(estadoIdAEliminar);
        });

        // Assert
        verify(estadoRepository).findById(estadoIdAEliminar);
        verify(bodegaRepository).existsByEstadoId(estadoIdAEliminar);
        verify(itemRepository).existsByEstadoId(estadoIdAEliminar);
        verify(pedidoRepository).existsByEstadoId(estadoIdAEliminar);
        verify(estadoRepository).delete(estadoPendientePedido);
    }

    @Test
    @DisplayName("Eliminar Estado - Estado No Encontrado")
    void eliminarEstado_NoEncontrado() {
        // Arrange
        Long idInexistente = 99L;
        when(estadoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            estadoService.eliminarEstado(idInexistente);
        });

        assertEquals("Estado no encontrado con ID: " + idInexistente, exception.getMessage());
        verify(estadoRepository).findById(idInexistente);
        verifyNoInteractions(bodegaRepository, itemRepository, pedidoRepository);
        verify(estadoRepository, never()).delete(any(Estado.class));
    }

    @Test
    @DisplayName("Eliminar Estado - Estado En Uso por Items")
    void eliminarEstado_EnUsoPorItems_DebeLanzarExcepcion() {
        // Arrange
        Long estadoId = estadoActivoItem.getEstadoId();
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estadoActivoItem));
        // Simular que NO está en uso por Bodega, SÍ por Item
        when(bodegaRepository.existsByEstadoId(estadoId)).thenReturn(false);
        when(itemRepository.existsByEstadoId(estadoId)).thenReturn(true); // EN USO

        // Act & Assert
        UsedStateException exception = assertThrows(UsedStateException.class, () -> {
            estadoService.eliminarEstado(estadoId);
        });

        assertTrue(exception.getMessage().contains("porque está en uso por Items"));

        // Verificar interacciones
        verify(estadoRepository).findById(estadoId);
        verify(bodegaRepository).existsByEstadoId(estadoId);
        verify(itemRepository).existsByEstadoId(estadoId);
        verify(pedidoRepository, never()).existsByEstadoId(anyLong()); // No se llega a verificar Pedido
        verify(estadoRepository, never()).delete(any(Estado.class)); // No se debe eliminar
    }

    @Test
    @DisplayName("Eliminar Estado - Estado En Uso por Pedidos")
    void eliminarEstado_EnUsoPorPedidos_DebeLanzarExcepcion() {
        // Arrange
        Long estadoId = estadoPendientePedido.getEstadoId();
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estadoPendientePedido));
        // Simular que NO está en uso por Bodega/Item, SÍ por Pedido
        when(bodegaRepository.existsByEstadoId(estadoId)).thenReturn(false);
        when(itemRepository.existsByEstadoId(estadoId)).thenReturn(false);
        when(pedidoRepository.existsByEstadoId(estadoId)).thenReturn(true); // EN USO

        // Act & Assert
        UsedStateException exception = assertThrows(UsedStateException.class, () -> {
            estadoService.eliminarEstado(estadoId);
        });

        assertTrue(exception.getMessage().contains("porque está en uso por Pedidos"));

        verify(estadoRepository).findById(estadoId);
        verify(bodegaRepository).existsByEstadoId(estadoId);
        verify(itemRepository).existsByEstadoId(estadoId);
        verify(pedidoRepository).existsByEstadoId(estadoId);
        verify(estadoRepository, never()).delete(any(Estado.class));
    }

    @Test
    @DisplayName("Crear Estado - Éxito")
    void crearEstado_Exito() {
        // Arrange
        when(estadoRepository.existsByTipoEstadoAndValor(estadoDtoNuevo.getTipoEstado(), estadoDtoNuevo.getValor()))
                .thenReturn(false); // No existe
        when(estadoMapper.toEntity(estadoDtoNuevo)).thenReturn(new Estado(estadoDtoNuevo.getTipoEstado(), estadoDtoNuevo.getValor()));
        when(estadoRepository.save(any(Estado.class))).thenAnswer(inv -> {
            Estado e = inv.getArgument(0);
            e.setEstadoId(99L); // Simular ID generado
            return e;
        });
        when(estadoMapper.toDTO(any(Estado.class))).thenReturn(estadoDtoNuevo); // Asumir que toDTO devuelve el DTO original

        // Act
        EstadoDTO resultado = estadoService.crearEstado(estadoDtoNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(estadoDtoNuevo.getTipoEstado(), resultado.getTipoEstado());
        assertEquals(estadoDtoNuevo.getValor(), resultado.getValor());

        verify(estadoRepository).existsByTipoEstadoAndValor(estadoDtoNuevo.getTipoEstado(), estadoDtoNuevo.getValor());
        verify(estadoMapper).toEntity(estadoDtoNuevo);
        verify(estadoRepository).save(any(Estado.class));
        verify(estadoMapper).toDTO(any(Estado.class));
    }

    @Test
    @DisplayName("Crear Estado - Duplicado")
    void crearEstado_Duplicado_DebeLanzarExcepcion() {
        // Arrange
        when(estadoRepository.existsByTipoEstadoAndValor(estadoDtoActivo.getTipoEstado(), estadoDtoActivo.getValor()))
                .thenReturn(true); // Simula que ya existe

        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            estadoService.crearEstado(estadoDtoActivo);
        });

        assertTrue(exception.getMessage().contains("Ya existe un estado con tipo"));
        verify(estadoRepository).existsByTipoEstadoAndValor(estadoDtoActivo.getTipoEstado(), estadoDtoActivo.getValor());
        verify(estadoMapper, never()).toEntity(any());
        verify(estadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar Estado - Éxito")
    void actualizarEstado_Exito() {
        // Arrange
        Long idActualizar = estadoActivoItem.getEstadoId();
        when(estadoRepository.findById(idActualizar)).thenReturn(Optional.of(estadoActivoItem));
        // Simular que la nueva combinación NO existe
        when(estadoRepository.existsByTipoEstadoAndValor(estadoDtoNuevo.getTipoEstado(), estadoDtoNuevo.getValor()))
                .thenReturn(false);
        // Simular la actualización por el mapper (void method)
        doNothing().when(estadoMapper).updateEntityFromDTO(eq(estadoDtoNuevo), eq(estadoActivoItem));
        when(estadoRepository.save(any(Estado.class))).thenReturn(estadoActivoItem); // Devuelve la entidad actualizada
        when(estadoMapper.toDTO(any(Estado.class))).thenReturn(estadoDtoNuevo); // Devuelve el DTO

        // Act
        EstadoDTO resultado = estadoService.actualizarEstado(idActualizar, estadoDtoNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(estadoDtoNuevo.getTipoEstado(), resultado.getTipoEstado());
        assertEquals(estadoDtoNuevo.getValor(), resultado.getValor());

        verify(estadoRepository).findById(idActualizar);
        verify(estadoRepository).existsByTipoEstadoAndValor(estadoDtoNuevo.getTipoEstado(), estadoDtoNuevo.getValor());
        verify(estadoMapper).updateEntityFromDTO(eq(estadoDtoNuevo), eq(estadoActivoItem));
        verify(estadoRepository).save(estadoActivoItem);
        verify(estadoMapper).toDTO(estadoActivoItem);
    }

    @Test
    @DisplayName("Actualizar Estado - Combinación Duplicada")
    void actualizarEstado_CombinacionDuplicada_DebeLanzarExcepcion() {
        // Arrange
        Long idActualizar = estadoActivoItem.getEstadoId();
        // DTO intenta cambiar a una combinación que ya existe (la de Pendiente Pedido)
        EstadoDTO dtoDuplicado = new EstadoDTO(TipoEstado.PEDIDO, "Pendiente");

        when(estadoRepository.findById(idActualizar)).thenReturn(Optional.of(estadoActivoItem));
        // Simula que la nueva combinación (PEDIDO, Pendiente) ya existe
        when(estadoRepository.existsByTipoEstadoAndValor(dtoDuplicado.getTipoEstado(), dtoDuplicado.getValor()))
                .thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            estadoService.actualizarEstado(idActualizar, dtoDuplicado);
        });

        assertTrue(exception.getMessage().contains("Ya existe un estado con tipo"));

        verify(estadoRepository).findById(idActualizar);
        verify(estadoRepository).existsByTipoEstadoAndValor(dtoDuplicado.getTipoEstado(), dtoDuplicado.getValor());
        verify(estadoMapper, never()).updateEntityFromDTO(any(), any());
        verify(estadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar Estado - No Encontrado")
    void actualizarEstado_NoEncontrado_DebeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 99L;
        when(estadoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            estadoService.actualizarEstado(idInexistente, estadoDtoNuevo);
        });

        assertEquals("Estado no encontrado con ID: " + idInexistente, exception.getMessage());
        verify(estadoRepository).findById(idInexistente);
        verify(estadoRepository, never()).existsByTipoEstadoAndValor(any(), any());
        verify(estadoRepository, never()).save(any());
    }
}