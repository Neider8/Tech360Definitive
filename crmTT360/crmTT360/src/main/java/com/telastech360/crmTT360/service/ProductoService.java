package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.entity.Item.TipoItem; // Importar el enum TipoItem de Item
import com.telastech360.crmTT360.exception.*;
import com.telastech360.crmTT360.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ItemRepository itemRepository; // Para validación de código único y pedidos activos
    private final BodegaRepository bodegaRepository;
    private final CategoriaRepository categoriaRepository;
    private final EstadoRepository estadoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository,
                           ItemRepository itemRepository,
                           BodegaRepository bodegaRepository,
                           CategoriaRepository categoriaRepository,
                           EstadoRepository estadoRepository,
                           ProveedorRepository proveedorRepository,
                           UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.itemRepository = itemRepository;
        this.bodegaRepository = bodegaRepository;
        this.categoriaRepository = categoriaRepository;
        this.estadoRepository = estadoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ========== CRUD BÁSICO ========== //

    @Transactional
    public Producto crearProducto(Producto producto) {
        // Validar código único utilizando el ItemRepository
        if (itemRepository.existsByCodigo(producto.getCodigo())) {
            throw new DuplicateResourceException("Ya existe un producto con el código: " + producto.getCodigo());
        }

        // Validar que las relaciones existan en la base de datos
        validarRelacionesProducto(producto);

        // Establecer tipo de item (Aunque ya se hace en el constructor de Producto, lo reconfirmamos)
        producto.setTipoItem(Item.TipoItem.PRODUCTO_TERMINADO);

        // Guardar el producto
        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Long id) {
        // Buscar en el repositorio de Producto
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodosLosProductos() {
        // Listar todos los productos
        return productoRepository.findAll();
    }

    @Transactional
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        // Obtener el producto existente
        Producto productoExistente = obtenerProductoPorId(id);

        // Validar código único si cambia
        if (!productoExistente.getCodigo().equals(productoActualizado.getCodigo()) &&
                itemRepository.existsByCodigo(productoActualizado.getCodigo())) {
            throw new DuplicateResourceException("El código ya está registrado: " + productoActualizado.getCodigo());
        }

        // Validar que las relaciones actualizadas existan en la base de datos
        validarRelacionesProducto(productoActualizado);

        // Actualizar campos heredados de Item (usando los métodos correctos del padre)
        productoExistente.setCodigo(productoActualizado.getCodigo());
        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setUnidadMedida(productoActualizado.getUnidadMedida());
        productoExistente.setPrecio(productoActualizado.getPrecio()); // Corregido: usar getPrecio() y setPrecio()
        productoExistente.setStockMinimo(productoActualizado.getStockMinimo());
        productoExistente.setStockMaximo(productoActualizado.getStockMaximo());
        productoExistente.setStockDisponible(productoActualizado.getStockDisponible()); // Corregido: usar getStockDisponible() y setStockDisponible()
        productoExistente.setFechaVencimiento(productoActualizado.getFechaVencimiento());

        // Actualizar campos específicos de Producto
        productoExistente.setTipoPrenda(productoActualizado.getTipoPrenda());
        productoExistente.setTalla(productoActualizado.getTalla());
        productoExistente.setColor(productoActualizado.getColor());
        // productoExistente.setComposicion(productoActualizado.getComposicion()); // Removido: 'composicion' no en entidad Producto
        productoExistente.setFechaFabricacion(productoActualizado.getFechaFabricacion());
        productoExistente.setTemporada(productoActualizado.getTemporada());

        // Actualizar relaciones (usando los métodos privados auxiliares)
        actualizarRelacionesProducto(productoExistente, productoActualizado);

        // Guardar el producto actualizado
        return productoRepository.save(productoExistente);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        // Obtener el producto por ID
        Producto producto = obtenerProductoPorId(id);

        // Validar que no esté en pedidos activos (utilizando ItemRepository)
        if (itemRepository.existeEnPedidosActivos(id)) { // Método existeEnPedidosActivos debe estar en ItemRepository
            throw new IllegalOperationException("No se puede eliminar el producto porque está en pedidos activos");
        }

        // Eliminar el producto
        productoRepository.delete(producto);
    }

    // ========== MÉTODOS ESPECIALIZADOS (Requieren implementación en ProductoRepository) ========== //

    @Transactional(readOnly = true)
    public List<Producto> buscarPorTipoPrenda(Producto.TipoPrenda tipoPrenda) {
        return productoRepository.findByTipoPrenda(tipoPrenda);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorTalla(Producto.Talla talla) {
        return productoRepository.findByTalla(talla);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorColor(String color) {
        return productoRepository.findByColorContainingIgnoreCase(color);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorTemporada(String temporada) {
        return productoRepository.findByTemporada(temporada);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorVencer(Date fechaLimite) {
        return productoRepository.findProductosPorVencer(fechaLimite);
    }

    @Transactional(readOnly = true)
    public List<String> obtenerColoresDisponibles(Producto.TipoPrenda tipoPrenda) {
        return productoRepository.findColoresDisponibles(tipoPrenda);
    }

    @Transactional(readOnly = true)
    public List<Producto.Talla> obtenerTallasDisponibles(Producto.TipoPrenda tipoPrenda) {
        return productoRepository.findTallasDisponibles(tipoPrenda);
    }

    @Transactional
    public void actualizarTemporadaPorTipo(Producto.TipoPrenda tipoPrenda, String temporada) {
        productoRepository.actualizarTemporadaPorTipo(tipoPrenda, temporada);
    }

    // ========== MÉTODOS PRIVADOS (Auxiliares para validación y actualización de relaciones) ========== //

    private void validarRelacionesProducto(Producto producto) {
        // Validar bodega existente (obligatoria)
        if (producto.getBodega() == null || !bodegaRepository.existsById(producto.getBodega().getBodegaId())) {
            throw new ResourceNotFoundException("Bodega no encontrada o no especificada");
        }

        // Validar usuario existente (obligatorio)
        if (producto.getUsuario() == null || !usuarioRepository.existsById(producto.getUsuario().getUsuarioId())) {
            throw new ResourceNotFoundException("Usuario no encontrado o no especificado");
        }

        // Validar categoría si existe (opcional)
        if (producto.getCategoria() != null && producto.getCategoria().getCategoriaId() != null) {
            if (!categoriaRepository.existsById(producto.getCategoria().getCategoriaId())) {
                throw new ResourceNotFoundException("Categoría no encontrada con ID: " + producto.getCategoria().getCategoriaId());
            }
        }


        // Validar estado si existe (opcional)
        if (producto.getEstado() != null && producto.getEstado().getEstadoId() != null) {
            if (!estadoRepository.existsById(producto.getEstado().getEstadoId())) {
                throw new ResourceNotFoundException("Estado no encontrado con ID: " + producto.getEstado().getEstadoId());
            }
        }


        // Validar proveedor si existe (opcional)
        if (producto.getProveedor() != null && producto.getProveedor().getProveedorId() != null) {
            if (!proveedorRepository.existsById(producto.getProveedor().getProveedorId())) {
                throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + producto.getProveedor().getProveedorId());
            }
        }
    }


    /**
     * Actualiza las relaciones de un producto existente con los datos de un producto actualizado.
     * Este método asume que las validaciones previas ya se realizaron.
     */
    private void actualizarRelacionesProducto(Producto productoExistente, Producto productoActualizado) {
        // Actualizar relación con Bodega (obligatoria)
        if (productoActualizado.getBodega() != null && productoActualizado.getBodega().getBodegaId() != null) {
            productoExistente.setBodega(bodegaRepository.findById(productoActualizado.getBodega().getBodegaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Error interno: Bodega no encontrada durante actualización")));
        }


        // Actualizar relación con Categoría (opcional)
        if (productoActualizado.getCategoria() != null && productoActualizado.getCategoria().getCategoriaId() != null) {
            productoExistente.setCategoria(categoriaRepository.findById(productoActualizado.getCategoria().getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Error interno: Categoría no encontrada durante actualización")));
        } else {
            productoExistente.setCategoria(null); // Si se envía null, eliminar la relación existente
        }

        // Actualizar relación con Estado (opcional)
        if (productoActualizado.getEstado() != null && productoActualizado.getEstado().getEstadoId() != null) {
            productoExistente.setEstado(estadoRepository.findById(productoActualizado.getEstado().getEstadoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Error interno: Estado no encontrado durante actualización")));
        } else {
            productoExistente.setEstado(null); // Si se envía null, eliminar la relación existente
        }


        // Actualizar relación con Proveedor (opcional)
        if (productoActualizado.getProveedor() != null && productoActualizado.getProveedor().getProveedorId() != null) {
            productoExistente.setProveedor(proveedorRepository.findById(productoActualizado.getProveedor().getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Error interno: Proveedor no encontrado durante actualización")));
        } else {
            productoExistente.setProveedor(null); // Si se envía null, eliminar la relación existente
        }


        // Actualizar relación con Usuario (obligatorio)
        if (productoActualizado.getUsuario() != null && productoActualizado.getUsuario().getUsuarioId() != null) {
            productoExistente.setUsuario(usuarioRepository.findById(productoActualizado.getUsuario().getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Error interno: Usuario no encontrado durante actualización")));
        }

    }
}