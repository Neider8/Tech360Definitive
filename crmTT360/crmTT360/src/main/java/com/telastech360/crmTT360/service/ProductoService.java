package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.ProductoDTO; // Importar DTO
import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.exception.*;
import com.telastech360.crmTT360.mapper.ProductoMapper; // Importar Mapper
import com.telastech360.crmTT360.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Servicio para gestionar la lógica de negocio relacionada con los Productos Terminados.
 * Extiende la lógica de Items y añade validaciones/operaciones específicas de Producto.
 */
@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final ItemRepository itemRepository;
    private final BodegaRepository bodegaRepository;
    private final CategoriaRepository categoriaRepository;
    private final EstadoRepository estadoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoMapper productoMapper; // Inyectar Mapper

    /**
     * Constructor para inyección de dependencias.
     */
    @Autowired
    public ProductoService(ProductoRepository productoRepository,
                           ItemRepository itemRepository,
                           BodegaRepository bodegaRepository,
                           CategoriaRepository categoriaRepository,
                           EstadoRepository estadoRepository,
                           ProveedorRepository proveedorRepository,
                           UsuarioRepository usuarioRepository,
                           ProductoMapper productoMapper) { // Inyectar Mapper
        this.productoRepository = productoRepository;
        this.itemRepository = itemRepository;
        this.bodegaRepository = bodegaRepository;
        this.categoriaRepository = categoriaRepository;
        this.estadoRepository = estadoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoMapper = productoMapper; // Asignar Mapper
    }

    /**
     * Crea un nuevo Producto Terminado a partir de un DTO.
     * Valida código único y existencia de entidades relacionadas.
     * Asigna el tipo de item correcto.
     * @param productoDto DTO con la información del producto a crear.
     * @return La entidad Producto creada y guardada.
     * @throws DuplicateResourceException Si el código ya existe.
     * @throws ResourceNotFoundException Si alguna entidad relacionada no existe.
     * @throws InvalidDataException si el tipo de prenda o talla son inválidos.
     */
    @Transactional
    public Producto crearProducto(ProductoDTO productoDto) { // Firma cambiada a DTO
        log.info("Intentando crear producto con código: {}", productoDto.getCodigo());
        if (itemRepository.existsByCodigo(productoDto.getCodigo())) {
            log.warn("Código de ítem duplicado: {}", productoDto.getCodigo());
            throw new DuplicateResourceException("Ya existe un ítem con el código: " + productoDto.getCodigo());
        }

        // Cargar entidades relacionadas desde IDs en DTO
        log.debug("Buscando relaciones para producto DTO código: {}", productoDto.getCodigo());
        Bodega bodega = bodegaRepository.findById(productoDto.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + productoDto.getBodegaId()));
        Categoria categoria = categoriaRepository.findById(productoDto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + productoDto.getCategoriaId()));
        Estado estado = estadoRepository.findById(productoDto.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + productoDto.getEstadoId()));
        Proveedor proveedor = proveedorRepository.findById(productoDto.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + productoDto.getProveedorId()));
        Usuario usuario = usuarioRepository.findById(productoDto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + productoDto.getUsuarioId()));
        log.debug("Relaciones encontradas para producto DTO código: {}", productoDto.getCodigo());

        // Mapear DTO a Entidad usando el mapper y las entidades cargadas
        Producto producto = productoMapper.toEntity(productoDto, bodega, categoria, estado, proveedor, usuario);
        // El tipo de Item se setea dentro del mapper o constructor de Producto

        Producto productoGuardado = productoRepository.save(producto);
        log.info("Producto '{}' (Código: {}) creado exitosamente con ID: {}",
                productoGuardado.getNombre(), productoGuardado.getCodigo(), productoGuardado.getItemId());
        return productoGuardado;
    }

    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Long id) {
        log.info("Buscando producto por ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Producto no encontrado con ID: " + id);
                });
        log.debug("Producto encontrado: {} (ID: {})", producto.getNombre(), id);
        return producto;
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodosLosProductos() {
        log.info("Listando todos los productos terminados...");
        List<Producto> productos = productoRepository.findAll();
        log.debug("Se encontraron {} productos terminados.", productos.size());
        return productos;
    }

    /**
     * Actualiza un producto existente a partir de un DTO.
     * @param id ID del producto a actualizar.
     * @param productoDto DTO con los nuevos datos.
     * @return La entidad Producto actualizada.
     * @throws ResourceNotFoundException Si el producto o alguna relación no existen.
     * @throws DuplicateResourceException Si el nuevo código ya está en uso por otro item.
     * @throws InvalidDataException si el tipo de prenda o talla son inválidos.
     */
    @Transactional
    public Producto actualizarProducto(Long id, ProductoDTO productoDto) { // Firma cambiada a DTO
        log.info("Intentando actualizar producto con ID: {}", id);
        Producto productoExistente = obtenerProductoPorId(id); // Valida existencia

        // Validar código único si cambia
        if (!productoExistente.getCodigo().equalsIgnoreCase(productoDto.getCodigo())) {
            log.debug("El código del producto ID {} ha cambiado a '{}'. Verificando disponibilidad...", id, productoDto.getCodigo());
            if (itemRepository.existsByCodigo(productoDto.getCodigo())) {
                log.warn("Conflicto: El código '{}' ya está en uso por otro item.", productoDto.getCodigo());
                throw new DuplicateResourceException("El código ya está registrado: " + productoDto.getCodigo());
            }
            log.debug("Código '{}' disponible.", productoDto.getCodigo());
            // La actualización del código se hará mediante el mapper
        }

        // Cargar entidades relacionadas desde IDs en DTO para la actualización
        log.debug("Buscando relaciones para actualizar producto ID: {}", id);
        Bodega bodega = bodegaRepository.findById(productoDto.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + productoDto.getBodegaId()));
        Categoria categoria = categoriaRepository.findById(productoDto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + productoDto.getCategoriaId()));
        Estado estado = estadoRepository.findById(productoDto.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + productoDto.getEstadoId()));
        Proveedor proveedor = proveedorRepository.findById(productoDto.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + productoDto.getProveedorId()));
        Usuario usuario = usuarioRepository.findById(productoDto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + productoDto.getUsuarioId()));
        log.debug("Relaciones encontradas para actualizar producto ID: {}", id);

        // Usar el mapper para actualizar la entidad existente desde el DTO y las relaciones cargadas
        productoMapper.updateEntityFromDTO(productoDto, productoExistente, bodega, categoria, estado, proveedor, usuario);
        log.debug("Entidad Producto ID {} actualizada desde DTO.", id);


        Producto productoGuardado = productoRepository.save(productoExistente);
        log.info("Producto ID {} actualizado exitosamente.", id);
        return productoGuardado;
    }

    @Transactional
    public void eliminarProducto(Long id) {
        log.info("Intentando eliminar producto con ID: {}", id);
        Producto producto = obtenerProductoPorId(id);

        if (itemRepository.existeEnPedidosActivos(id)) {
            log.warn("Intento de eliminar producto ID {} ('{}') que está en pedidos activos.", id, producto.getNombre());
            throw new IllegalOperationException("No se puede eliminar el producto '" + producto.getNombre() + "' porque está en pedidos activos.");
        }
        log.debug("El producto ID {} ('{}') no está en pedidos activos.", id, producto.getNombre());

        productoRepository.delete(producto);
        log.info("Producto ID {} ('{}') eliminado exitosamente.", id, producto.getNombre());
    }

    // ========== MÉTODOS ESPECIALIZADOS (Sin cambios) ========== //

    @Transactional(readOnly = true)
    public List<Producto> buscarPorTipoPrenda(Producto.TipoPrenda tipoPrenda) {
        log.info("Buscando productos por tipo de prenda: {}", tipoPrenda);
        List<Producto> productos = productoRepository.findByTipoPrenda(tipoPrenda);
        log.debug("Se encontraron {} productos del tipo {}", productos.size(), tipoPrenda);
        return productos;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorTalla(Producto.Talla talla) {
        log.info("Buscando productos por talla: {}", talla);
        List<Producto> productos = productoRepository.findByTalla(talla);
        log.debug("Se encontraron {} productos de talla {}", productos.size(), talla);
        return productos;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorColor(String color) {
        log.info("Buscando productos por color que contenga: '{}'", color);
        List<Producto> productos = productoRepository.findByColorContainingIgnoreCase(color);
        log.debug("Búsqueda por color '{}' encontró {} productos.", color, productos.size());
        return productos;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorTemporada(String temporada) {
        log.info("Buscando productos por temporada: '{}'", temporada);
        List<Producto> productos = productoRepository.findByTemporada(temporada);
        log.debug("Se encontraron {} productos de la temporada '{}'.", productos.size(), temporada);
        return productos;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorVencer(Date fechaLimite) {
        log.info("Buscando productos por vencer antes de: {}", fechaLimite);
        List<Producto> productos = productoRepository.findProductosPorVencer(new java.sql.Date(fechaLimite.getTime())); // Ajuste a sql.Date si repo lo espera
        log.debug("Se encontraron {} productos por vencer.", productos.size());
        return productos;
    }

    @Transactional(readOnly = true)
    public List<String> obtenerColoresDisponibles(Producto.TipoPrenda tipoPrenda) {
        log.info("Obteniendo colores disponibles para tipo de prenda: {}", tipoPrenda);
        List<String> colores = productoRepository.findColoresDisponibles(tipoPrenda);
        log.debug("Se encontraron {} colores distintos para {}", colores.size(), tipoPrenda);
        return colores;
    }

    @Transactional(readOnly = true)
    public List<Producto.Talla> obtenerTallasDisponibles(Producto.TipoPrenda tipoPrenda) {
        log.info("Obteniendo tallas disponibles para tipo de prenda: {}", tipoPrenda);
        List<Producto.Talla> tallas = productoRepository.findTallasDisponibles(tipoPrenda);
        log.debug("Se encontraron {} tallas distintas para {}", tallas.size(), tipoPrenda);
        return tallas;
    }

    @Transactional
    public int actualizarTemporadaPorTipo(Producto.TipoPrenda tipoPrenda, String temporada) {
        log.info("Actualizando temporada a '{}' para todos los productos de tipo {}", temporada, tipoPrenda);
        int count = productoRepository.actualizarTemporadaPorTipo(tipoPrenda, temporada);
        log.info("Se actualizó la temporada para {} productos de tipo {}", count, tipoPrenda);
        return count;
    }

    // Método auxiliar removido ya que la lógica ahora está en los métodos principales
    // private void validarYAsignarRelacionesBase(Item item) { ... }
}