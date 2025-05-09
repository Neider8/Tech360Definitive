// src/main/java/com/telastech360/crmTT360/service/ItemService.java
package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.ItemDTO;
import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.entity.Item.TipoItem;
import com.telastech360.crmTT360.exception.*;
import com.telastech360.crmTT360.repository.*;
import com.telastech360.crmTT360.mapper.ItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.Date; // Usar java.sql.Date si el repositorio lo espera
import java.math.BigDecimal; // Usar BigDecimal

/**
 * Servicio para gestionar la lógica de negocio de la entidad base Item
 * y sus operaciones comunes. Colabora con servicios específicos como
 * ProductoService y MateriaPrimaService para lógica especializada.
 */
@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;
    private final BodegaRepository bodegaRepository;
    private final CategoriaRepository categoriaRepository;
    private final EstadoRepository estadoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemMapper itemMapper;

    /**
     * Constructor para inyección de dependencias de repositorios y mappers necesarios.
     * @param itemRepository Repositorio para Items.
     * @param bodegaRepository Repositorio para Bodegas.
     * @param categoriaRepository Repositorio para Categorías.
     * @param estadoRepository Repositorio para Estados.
     * @param proveedorRepository Repositorio para Proveedores.
     * @param usuarioRepository Repositorio para Usuarios.
     * @param itemMapper Mapper para convertir entre Item e ItemDTO.
     */
    @Autowired
    public ItemService(ItemRepository itemRepository,
                       BodegaRepository bodegaRepository,
                       CategoriaRepository categoriaRepository,
                       EstadoRepository estadoRepository,
                       ProveedorRepository proveedorRepository,
                       UsuarioRepository usuarioRepository,
                       ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.bodegaRepository = bodegaRepository;
        this.categoriaRepository = categoriaRepository;
        this.estadoRepository = estadoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemMapper = itemMapper;
    }

    /**
     * Crea un nuevo ítem genérico (usualmente llamado por servicios específicos como ProductoService o MateriaPrimaService).
     * Valida código único y relaciones.
     * @param itemDTO DTO con la información del ítem a crear.
     * @return El ItemDTO del ítem creado.
     * @throws DuplicateResourceException Si el código ya existe.
     * @throws ResourceNotFoundException Si alguna entidad relacionada no existe.
     * @throws InvalidDataException Si el tipo de ítem en el DTO no es válido.
     */
    @Transactional
    public ItemDTO crearItem(ItemDTO itemDTO) {
        log.info("Intentando crear ítem con código: {}", itemDTO.getCodigo());
        if (itemRepository.existsByCodigo(itemDTO.getCodigo())) {
            log.warn("Código de ítem duplicado: {}", itemDTO.getCodigo());
            throw new DuplicateResourceException("Ya existe un ítem con el código: " + itemDTO.getCodigo());
        }

        log.debug("Buscando entidades relacionadas para el ítem {}", itemDTO.getCodigo());
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
        log.debug("Entidades relacionadas encontradas para el ítem {}", itemDTO.getCodigo());

        // El mapper ahora valida el tipo de item y lanza InvalidDataException si es necesario
        Item item = itemMapper.toEntity(itemDTO, bodega, categoria, estado, proveedor, usuario);
        log.debug("Ítem DTO mapeado a entidad: {}", item.getCodigo());

        Item nuevoItem = itemRepository.save(item);
        log.info("Ítem creado exitosamente con ID: {} y código: {}", nuevoItem.getItemId(), nuevoItem.getCodigo());
        return itemMapper.toDTO(nuevoItem);
    }

    /**
     * Obtiene un ítem por su ID y lo devuelve como DTO.
     * @param id ID del ítem a buscar.
     * @return El ItemDTO correspondiente.
     * @throws ResourceNotFoundException si el ítem no existe.
     */
    @Transactional(readOnly = true)
    public ItemDTO obtenerItemPorId(Long id) {
        log.info("Buscando ítem por ID: {}", id);
        Item item = findItemEntityById(id); // Usa método auxiliar que lanza excepción si no se encuentra
        log.debug("Ítem encontrado con ID: {}", id);
        return itemMapper.toDTO(item);
    }

    /**
     * Obtiene la entidad Item por su ID. Usado internamente o por otros servicios.
     * @param id ID del ítem a buscar.
     * @return La entidad Item encontrada.
     * @throws ResourceNotFoundException si el ítem no existe.
     */
    @Transactional(readOnly = true)
    public Item findItemEntityById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ítem no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Ítem no encontrado con ID: " + id);
                });
    }

    /**
     * Lista todos los ítems registrados.
     * @return Lista de ItemDTO.
     */
    @Transactional(readOnly = true)
    public List<ItemDTO> listarTodosLosItems() {
        log.info("Listando todos los ítems.");
        List<Item> items = itemRepository.findAll();
        log.debug("Se encontraron {} ítems.", items.size());
        return items.stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un ítem existente.
     * @param id ID del ítem a actualizar.
     * @param itemDTO DTO con los datos actualizados.
     * @return El ItemDTO actualizado.
     * @throws ResourceNotFoundException Si el ítem o alguna relación no existen.
     * @throws DuplicateResourceException Si el nuevo código ya está en uso por otro ítem.
     * @throws InvalidDataException Si el tipo de ítem en el DTO no es válido.
     */
    @Transactional
    public ItemDTO actualizarItem(Long id, ItemDTO itemDTO) {
        log.info("Intentando actualizar ítem con ID: {}", id);
        Item itemExistente = findItemEntityById(id); // Obtener entidad existente

        // Validar código único si cambia
        if (!itemExistente.getCodigo().equalsIgnoreCase(itemDTO.getCodigo())) {
            log.debug("Código cambiado para ítem ID {}. Verificando disponibilidad de '{}'...", id, itemDTO.getCodigo());
            if (itemRepository.existsByCodigo(itemDTO.getCodigo())) {
                log.warn("Conflicto: Código '{}' ya en uso al actualizar ítem ID {}", itemDTO.getCodigo(), id);
                throw new DuplicateResourceException("El código ya está registrado: " + itemDTO.getCodigo());
            }
            log.debug("Código '{}' disponible.", itemDTO.getCodigo());
        }

        // Buscar entidades relacionadas para la actualización
        log.debug("Buscando entidades relacionadas para actualizar ítem ID: {}", id);
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
        log.debug("Entidades relacionadas encontradas para actualizar ítem ID: {}", id);

        // Actualizar entidad desde DTO usando el mapper (valida tipo de ítem)
        itemMapper.updateEntityFromDTO(itemDTO, itemExistente, bodega, categoria, estado, proveedor, usuario);
        log.debug("Entidad Ítem ID {} actualizada desde DTO.", id);

        Item itemActualizado = itemRepository.save(itemExistente);
        log.info("Ítem ID {} actualizado exitosamente.", id);
        return itemMapper.toDTO(itemActualizado);
    }

    /**
     * Elimina un ítem por su ID.
     * Verifica que no esté en uso en pedidos activos.
     * @param id ID del ítem a eliminar.
     * @throws ResourceNotFoundException si el ítem no existe.
     * @throws IllegalOperationException si el ítem está en pedidos activos.
     */
    @Transactional
    public void eliminarItem(Long id) {
        log.info("Intentando eliminar ítem con ID: {}", id);
        Item item = findItemEntityById(id); // Valida que existe

        // Validar que no esté en pedidos activos
        if (itemRepository.existeEnPedidosActivos(id)) {
            log.warn("Intento de eliminar ítem ID {} ('{}') que está en pedidos activos.", id, item.getNombre());
            throw new IllegalOperationException("No se puede eliminar el ítem '" + item.getNombre() + "' porque está en pedidos activos.");
        }
        log.debug("Verificación de pedidos activos superada para ítem ID: {}", id);

        itemRepository.delete(item);
        log.info("Ítem ID {} ('{}') eliminado exitosamente.", id, item.getNombre());
    }

    /**
     * Ajusta el stock disponible de un ítem específico.
     * @param itemId ID del ítem a ajustar.
     * @param cantidad Cantidad a sumar (positivo) o restar (negativo).
     * @throws ResourceNotFoundException si el ítem no existe.
     * @throws IllegalOperationException si el ajuste resultaría en stock negativo.
     */
    @Transactional
    public void ajustarStock(Long itemId, Integer cantidad) {
        log.info("Ajustando stock para ítem ID {} en {} unidades.", itemId, cantidad);
        Item item = findItemEntityById(itemId); // Valida existencia
        int stockActual = item.getStockDisponible();
        int stockNuevo = stockActual + cantidad;

        // --- Validación de Stock Negativo ---
        if (stockNuevo < 0) {
            log.error("Stock insuficiente para ítem ID {}. Actual: {}, Ajuste: {}, Resultante: {}",
                    itemId, stockActual, cantidad, stockNuevo);
            throw new IllegalOperationException("Ajuste inválido. Stock insuficiente para el ítem '"
                    + item.getNombre() + "'. Stock disponible: " + stockActual
                    + ", se intentó ajustar en: " + cantidad);
        }
        // -------------------------------------

        log.debug("Stock anterior: {}, Ajuste: {}, Stock nuevo: {} para ítem ID {}",
                stockActual, cantidad, stockNuevo, itemId);

        item.setStockDisponible(stockNuevo);
        itemRepository.save(item); // Guardar la entidad modificada
        log.info("Stock para ítem ID {} ajustado exitosamente a {}.", itemId, stockNuevo);
    }


    // ========== MÉTODOS ADICIONALES ========== //

    /**
     * Busca ítems cuyo nombre contenga el texto dado (ignorando mayúsculas/minúsculas).
     * Devuelve entidades, el controlador debe mapear a DTO si es necesario.
     * @param nombre Texto a buscar en el nombre.
     * @return Lista de entidades Item coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Item> buscarItemsPorNombre(String nombre) {
        log.info("Buscando ítems por nombre que contenga: '{}'", nombre);
        List<Item> items = itemRepository.findByNombreContainingIgnoreCase(nombre);
        log.debug("Búsqueda por nombre '{}' encontró {} ítems.", nombre, items.size());
        return items;
    }

    /**
     * Busca ítems por su tipo (MATERIA_PRIMA o PRODUCTO_TERMINADO).
     * Devuelve entidades.
     * @param tipo El TipoItem a filtrar.
     * @return Lista de entidades Item coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Item> buscarItemsPorTipo(TipoItem tipo) {
        log.info("Buscando ítems por tipo: {}", tipo);
        List<Item> items = itemRepository.findByTipoItem(tipo);
        log.debug("Se encontraron {} ítems del tipo {}", items.size(), tipo);
        return items;
    }

    /**
     * Busca ítems cuyo stock disponible sea menor que su stock mínimo.
     * Devuelve entidades.
     * @return Lista de entidades Item con stock bajo.
     */
    @Transactional(readOnly = true)
    public List<Item> buscarItemsConStockBajo() {
        log.info("Buscando ítems con stock bajo.");
        List<Item> items = itemRepository.findItemsConStockBajo();
        log.debug("Se encontraron {} ítems con stock bajo.", items.size());
        return items;
    }

    /**
     * Busca ítems cuya fecha de vencimiento esté entre hoy y una fecha límite.
     * Devuelve entidades.
     * @param fechaLimite La fecha límite (inclusive).
     * @return Lista de entidades Item próximas a vencer.
     */
    @Transactional(readOnly = true)
    public List<Item> buscarItemsPorVencer(Date fechaLimite) {
        log.info("Buscando ítems por vencer antes de: {}", fechaLimite);
        Date hoy = new Date(System.currentTimeMillis());
        List<Item> items = itemRepository.findItemsPorVencer(hoy, fechaLimite);
        log.debug("Se encontraron {} ítems por vencer hasta {}", items.size(), fechaLimite);
        return items;
    }

    /**
     * Obtiene un resumen del inventario agrupado por tipo de ítem.
     * Incluye conteo, stock total y valor total del stock por tipo.
     * @return Lista de Object[], donde cada array contiene [TipoItem (enum), Long (conteo), Long (stock total), BigDecimal (valor total)].
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerResumenInventario() {
        log.info("Obteniendo resumen de inventario por tipo de ítem...");
        List<Object[]> resumen = itemRepository.getResumenInventario();
        log.debug("Resumen de inventario obtenido con {} tipos.", resumen.size());
        return resumen;
    }
}