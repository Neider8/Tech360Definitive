package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.ItemDTO; // Importar ItemDTO
import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.entity.Item.TipoItem;
import com.telastech360.crmTT360.exception.*; // Importar todas las excepciones, incluyendo InvalidDataException
import com.telastech360.crmTT360.repository.*;
import com.telastech360.crmTT360.mapper.ItemMapper; // Importar ItemMapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.Date; // Importar java.sql.Date si se usa en métodos adicionales

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final BodegaRepository bodegaRepository;
    private final CategoriaRepository categoriaRepository;
    private final EstadoRepository estadoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemMapper itemMapper; // Inyectar el ItemMapper

    @Autowired
    public ItemService(ItemRepository itemRepository,
                       BodegaRepository bodegaRepository,
                       CategoriaRepository categoriaRepository,
                       EstadoRepository estadoRepository,
                       ProveedorRepository proveedorRepository,
                       UsuarioRepository usuarioRepository,
                       ItemMapper itemMapper) { // Inyectar el ItemMapper
        this.itemRepository = itemRepository;
        this.bodegaRepository = bodegaRepository;
        this.categoriaRepository = categoriaRepository;
        this.estadoRepository = estadoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemMapper = itemMapper; // Asignar el Mapper inyectado
    }

    // ========== CRUD BÁSICO con DTOs ========== //

    @Transactional
    public ItemDTO crearItem(ItemDTO itemDTO) {
        // Validar código único antes de cargar relaciones para eficiencia
        if (itemRepository.existsByCodigo(itemDTO.getCodigo())) {
            throw new DuplicateResourceException("Ya existe un ítem con el código: " + itemDTO.getCodigo());
        }


        Bodega bodega = bodegaRepository.findById(itemDTO.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + itemDTO.getBodegaId()));
        Categoria categoria = categoriaRepository.findById(itemDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + itemDTO.getCategoriaId()));
        Estado estado = estadoRepository.findById(itemDTO.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrada con ID: " + itemDTO.getEstadoId()));
        Proveedor proveedor = proveedorRepository.findById(itemDTO.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + itemDTO.getProveedorId()));
        Usuario usuario = usuarioRepository.findById(itemDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + itemDTO.getUsuarioId()));

        // Convertir DTO a entidad usando el Mapper y las entidades cargadas
        // El mapper ahora maneja la conversión segura del tipo de item y lanza InvalidDataException si es necesario.
        Item item = itemMapper.toEntity(itemDTO, bodega, categoria, estado, proveedor, usuario);

        // Guardar la entidad
        Item nuevoItem = itemRepository.save(item);

        // Convertir la entidad guardada de nuevo a DTO para retornarla
        return itemMapper.toDTO(nuevoItem);
    }

    @Transactional(readOnly = true)
    public ItemDTO obtenerItemPorId(Long id) {
        // Obtener la entidad por ID
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado con ID: " + id));

        // Convertir entidad a DTO y retornarlo
        return itemMapper.toDTO(item);
    }

    @Transactional(readOnly = true)
    public List<ItemDTO> listarTodosLosItems() {
        // Obtener todas las entidades
        List<Item> items = itemRepository.findAll();


        return items.stream()
                .map(itemMapper::toDTO) // Usar la instancia del mapper
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDTO actualizarItem(Long id, ItemDTO itemDTO) {
        // Obtener el ítem existente
        Item itemExistente = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado con ID: " + id));

        // Validar código único si cambia
        if (!itemExistente.getCodigo().equals(itemDTO.getCodigo()) &&
                itemRepository.existsByCodigo(itemDTO.getCodigo())) {
            throw new DuplicateResourceException("El código ya está registrado: " + itemDTO.getCodigo());
        }


        Bodega bodega = bodegaRepository.findById(itemDTO.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + itemDTO.getBodegaId()));
        Categoria categoria = categoriaRepository.findById(itemDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + itemDTO.getCategoriaId()));
        Estado estado = estadoRepository.findById(itemDTO.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + itemDTO.getEstadoId()));
        Proveedor proveedor = proveedorRepository.findById(itemDTO.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + itemDTO.getProveedorId()));
        Usuario usuario = usuarioRepository.findById(itemDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + itemDTO.getUsuarioId()));


        itemMapper.updateEntityFromDTO(itemDTO, itemExistente, bodega, categoria, estado, proveedor, usuario);

        // Guardar la entidad actualizada
        Item itemActualizado = itemRepository.save(itemExistente);

        // Convertir la entidad actualizada a DTO para retornarla
        return itemMapper.toDTO(itemActualizado);
    }

    @Transactional
    public void eliminarItem(Long id) {
        // Obtener el ítem por ID
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado con ID: " + id));

        // Validar que no esté en pedidos activos (método simulado, debes implementarlo en tu repositorio si es necesario)
        // if (itemRepository.existeEnPedidosActivos(id)) {
        //     throw new IllegalOperationException("No se puede eliminar el ítem porque está en pedidos activos");
        // }

        // Eliminar la entidad
        itemRepository.delete(item);
    }

    // Nota: Estos métodos adicionales retornan entidades por simplicidad,

    // Implementar en ItemRepository: List<Item> findByNombreContainingIgnoreCase(String nombre);
    @Transactional(readOnly = true)
    public List<Item> buscarItemsPorNombre(String nombre) {
        //return itemRepository.findByNombreContainingIgnoreCase(nombre);
        return null; // Retorno temporal
    }

    // Implementar en ItemRepository: List<Item> findByTipoItem(TipoItem tipo);
    @Transactional(readOnly = true)
    public List<Item> buscarItemsPorTipo(TipoItem tipo) {
        // return itemRepository.findByTipoItem(tipo);
        return null; // Retorno temporal
    }

    // Implementar en ItemRepository: List<Item> findItemsConStockBajo();
    @Transactional(readOnly = true)
    public List<Item> buscarItemsConStockBajo() {
        // Implementa este método en tu ItemRepository
        // return itemRepository.findItemsConStockBajo();
        return null; // Retorno temporal
    }

    // Implementar en ItemRepository: List<Item> findItemsPorVencer(Date fechaActual, Date fechaLimite);
    @Transactional(readOnly = true)
    public List<Item> buscarItemsPorVencer(java.sql.Date fechaLimite) {
        // Implementa este método en tu ItemRepository
        // return itemRepository.findItemsPorVencer(new java.sql.Date(System.currentTimeMillis()), fechaLimite);
        return null; // Retorno temporal
    }

    // Implementar en ItemRepository: @Modifying @Query("UPDATE Item i SET i.stockDisponible = i.stockDisponible + :cantidad WHERE i.itemId = :itemId") void actualizarStock(@Param("itemId") Long itemId, @Param("cantidad") Integer cantidad);
    @Transactional
    public void ajustarStock(Long itemId, Integer cantidad) {
        // Implementa este método en tu ItemRepository o maneja la lógica en el servicio
        // itemRepository.actualizarStock(itemId, cantidad);
    }

    @Transactional(readOnly = true)
    public List<Object[]> obtenerResumenInventario() {
        // Implementa este método en tu ItemRepository
        // return itemRepository.getResumenInventario();
        return null; // Retorno temporal
    }

    // ========== MÉTODOS PRIVADOS (Auxiliares) ========== //

    // Este método ya no es necesario en el servicio si se usa el mapper
    // para cargar las entidades relacionadas. La validación ahora ocurre
    // al intentar obtener las entidades por ID en los métodos CRUD.
    // private void validarRelacionesItem(Item item) {
    //     // ... (código de validación anterior)
    // }
}