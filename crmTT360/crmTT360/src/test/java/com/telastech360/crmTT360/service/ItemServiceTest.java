package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.ItemDTO;
import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.exception.*; // Importar excepciones
import com.telastech360.crmTT360.mapper.ItemMapper;
import com.telastech360.crmTT360.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List; // Importar List
import java.util.Collections; // Importar Collections

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq; // Importar eq
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ItemService.
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock private ItemRepository itemRepository;
    @Mock private BodegaRepository bodegaRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private EstadoRepository estadoRepository;
    @Mock private ProveedorRepository proveedorRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    private ItemDTO itemDto;
    private Item itemEntidad;
    private Bodega bodega;
    private Categoria categoria;
    private Estado estado;
    private Proveedor proveedor;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        bodega = new Bodega(); bodega.setBodegaId(1L);
        categoria = new Categoria(); categoria.setCategoriaId(2L);
        estado = new Estado(); estado.setEstadoId(3L);
        proveedor = new Proveedor(); proveedor.setProveedorId(4L);
        usuario = new Usuario(); usuario.setUsuarioId(5L);

        itemDto = new ItemDTO(
                "ITEM001", "Item Test", "Unidad", new BigDecimal("10.99"),
                100, 10, estado.getEstadoId(), proveedor.getProveedorId(),
                categoria.getCategoriaId(), bodega.getBodegaId(), usuario.getUsuarioId(),
                "PRODUCTO_TERMINADO" // Tipo válido
        );
        itemDto.setItemId(1L); // Simular que el mapper añade el ID en toDTO

        itemEntidad = new Item(); // Simular entidad base
        itemEntidad.setItemId(1L);
        itemEntidad.setCodigo(itemDto.getCodigo());
        itemEntidad.setNombre(itemDto.getNombre());
        itemEntidad.setTipoItem(Item.TipoItem.PRODUCTO_TERMINADO);
        itemEntidad.setStockDisponible(100); // Asegurar stock inicial para pruebas de ajuste
        itemEntidad.setBodega(bodega);
        itemEntidad.setCategoria(categoria);
        itemEntidad.setEstado(estado);
        itemEntidad.setProveedor(proveedor);
        itemEntidad.setUsuario(usuario);
    }

    @Test
    @DisplayName("Crear Item - Éxito")
    void crearItem_Exito() {
        // Arrange
        when(itemRepository.existsByCodigo(anyString())).thenReturn(false);
        when(bodegaRepository.findById(anyLong())).thenReturn(Optional.of(bodega));
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(estadoRepository.findById(anyLong())).thenReturn(Optional.of(estado));
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(itemMapper.toEntity(any(ItemDTO.class), any(), any(), any(), any(), any())).thenReturn(itemEntidad); // Mapper devuelve entidad válida
        when(itemRepository.save(any(Item.class))).thenReturn(itemEntidad);
        when(itemMapper.toDTO(any(Item.class))).thenReturn(itemDto); // Mapper devuelve DTO con ID

        // Act
        ItemDTO resultado = itemService.crearItem(itemDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(itemDto.getCodigo(), resultado.getCodigo());
        assertEquals(itemDto.getItemId(), resultado.getItemId());

        verify(itemRepository).existsByCodigo(itemDto.getCodigo());
        verify(bodegaRepository).findById(itemDto.getBodegaId());
        verify(categoriaRepository).findById(itemDto.getCategoriaId());
        verify(estadoRepository).findById(itemDto.getEstadoId());
        verify(proveedorRepository).findById(itemDto.getProveedorId());
        verify(usuarioRepository).findById(itemDto.getUsuarioId());
        verify(itemMapper).toEntity(eq(itemDto), eq(bodega), eq(categoria), eq(estado), eq(proveedor), eq(usuario));
        verify(itemRepository).save(any(Item.class));
        verify(itemMapper).toDTO(any(Item.class));
    }

    @Test
    @DisplayName("Crear Item - Código Duplicado")
    void crearItem_CodigoDuplicado() {
        // Arrange
        when(itemRepository.existsByCodigo(itemDto.getCodigo())).thenReturn(true); // Código ya existe

        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            itemService.crearItem(itemDto);
        });

        assertEquals("Ya existe un ítem con el código: " + itemDto.getCodigo(), exception.getMessage());
        verify(itemRepository).existsByCodigo(itemDto.getCodigo());
        // Verificar que no se llamó a ningún otro repositorio o al mapper
        verifyNoInteractions(bodegaRepository, categoriaRepository, estadoRepository, proveedorRepository, usuarioRepository, itemMapper);
        verify(itemRepository, never()).save(any()); // Verificar que no se intentó guardar
    }


    @Test
    @DisplayName("Crear Item - Relación No Encontrada (Bodega)")
    void crearItem_RelacionNoEncontrada() {
        // Arrange
        when(itemRepository.existsByCodigo(anyString())).thenReturn(false);
        when(bodegaRepository.findById(itemDto.getBodegaId())).thenReturn(Optional.empty()); // Bodega no existe

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            itemService.crearItem(itemDto);
        });

        assertEquals("Bodega no encontrada con ID: " + itemDto.getBodegaId(), exception.getMessage());
        verify(itemRepository).existsByCodigo(itemDto.getCodigo());
        verify(bodegaRepository).findById(itemDto.getBodegaId());
        // Verificar que no se interactuó con otros repos o mappers después del fallo
        verifyNoInteractions(categoriaRepository, estadoRepository, proveedorRepository, usuarioRepository, itemMapper);
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear Item - Tipo de Ítem Inválido")
    void crearItem_TipoInvalido() {
        // Arrange
        itemDto.setTipoItem("TIPO_INVENTADO"); // Tipo inválido
        when(itemRepository.existsByCodigo(anyString())).thenReturn(false);
        when(bodegaRepository.findById(anyLong())).thenReturn(Optional.of(bodega));
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(estadoRepository.findById(anyLong())).thenReturn(Optional.of(estado));
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        // Configurar el mapper para lanzar la excepción cuando se llama a toEntity con tipo inválido
        when(itemMapper.toEntity(any(ItemDTO.class), any(), any(), any(), any(), any()))
                .thenThrow(new InvalidDataException("Tipo de item inválido: TIPO_INVENTADO. Valores permitidos: MATERIA_PRIMA, PRODUCTO_TERMINADO"));

        // Act & Assert
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            itemService.crearItem(itemDto);
        });

        assertTrue(exception.getMessage().contains("Tipo de item inválido: TIPO_INVENTADO"));

        verify(itemMapper).toEntity(eq(itemDto), eq(bodega), eq(categoria), eq(estado), eq(proveedor), eq(usuario));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Eliminar Item - En Uso por Pedidos (Falla)")
    void eliminarItem_EnUso_DebeLanzarExcepcion() {
        // Arrange
        Long itemId = itemEntidad.getItemId();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntidad));
        // Simula que el item SÍ está en pedidos activos
        when(itemRepository.existeEnPedidosActivos(itemId)).thenReturn(true);

        // Act & Assert
        IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> {
            itemService.eliminarItem(itemId);
        });

        assertTrue(exception.getMessage().contains("está en pedidos activos"));

        verify(itemRepository).findById(itemId);
        verify(itemRepository).existeEnPedidosActivos(itemId);
        verify(itemRepository, never()).delete(any(Item.class));
    }

    @Test
    @DisplayName("Eliminar Item - Éxito (No en uso)")
    void eliminarItem_Exito() {
        // Arrange
        Long itemId = itemEntidad.getItemId();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntidad));
        // Simula que el item NO está en pedidos activos
        when(itemRepository.existeEnPedidosActivos(itemId)).thenReturn(false);
        doNothing().when(itemRepository).delete(any(Item.class));

        // Act
        assertDoesNotThrow(() -> {
            itemService.eliminarItem(itemId);
        });

        // Assert
        verify(itemRepository).findById(itemId);
        verify(itemRepository).existeEnPedidosActivos(itemId);
        verify(itemRepository).delete(itemEntidad);
    }

    @Test
    @DisplayName("Eliminar Item - No Encontrado")
    void eliminarItem_NoEncontrado_DebeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(itemRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            itemService.eliminarItem(idInexistente);
        });
        assertEquals("Ítem no encontrado con ID: " + idInexistente, exception.getMessage());

        verify(itemRepository).findById(idInexistente);
        verify(itemRepository, never()).existeEnPedidosActivos(anyLong());
        verify(itemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Ajustar Stock - Insuficiente (Falla)")
    void ajustarStock_Insuficiente_DebeLanzarExcepcion() {
        // Arrange
        Long itemId = itemEntidad.getItemId();
        int cantidadAjuste = -150; // Más que el stock disponible (100)
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntidad));

        // Act & Assert
        IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> {
            itemService.ajustarStock(itemId, cantidadAjuste);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente"));

        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class)); // No se debe guardar
    }

    @Test
    @DisplayName("Ajustar Stock - Éxito (Suma)")
    void ajustarStock_ExitoSuma() {
        // Arrange
        Long itemId = itemEntidad.getItemId();
        int cantidadAjuste = 50;
        int stockEsperado = itemEntidad.getStockDisponible() + cantidadAjuste;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntidad));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        assertDoesNotThrow(() -> {
            itemService.ajustarStock(itemId, cantidadAjuste);
        });

        // Assert
        assertEquals(stockEsperado, itemEntidad.getStockDisponible()); // Verificar que el stock se actualizó en la entidad mockeada
        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(itemEntidad); // Verificar que se guardó la entidad
    }

    @Test
    @DisplayName("Ajustar Stock - Éxito (Resta)")
    void ajustarStock_ExitoResta() {
        // Arrange
        Long itemId = itemEntidad.getItemId();
        int cantidadAjuste = -30;
        int stockEsperado = itemEntidad.getStockDisponible() + cantidadAjuste;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntidad));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        assertDoesNotThrow(() -> {
            itemService.ajustarStock(itemId, cantidadAjuste);
        });

        // Assert
        assertEquals(stockEsperado, itemEntidad.getStockDisponible());
        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(itemEntidad);
    }

    @Test
    @DisplayName("Ajustar Stock - Item No Encontrado")
    void ajustarStock_ItemNoEncontrado_DebeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        int cantidadAjuste = 10;
        when(itemRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            itemService.ajustarStock(idInexistente, cantidadAjuste);
        });
        assertEquals("Ítem no encontrado con ID: " + idInexistente, exception.getMessage());

        verify(itemRepository).findById(idInexistente);
        verify(itemRepository, never()).save(any());
    }

}